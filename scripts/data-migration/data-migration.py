"""
data_injection.py

Main script to:
1) Run .sql files for each microservice's Postgres (Write).
2) Inject consistent data across services (Write -> Read).
3) Use loops to insert multiple sample records (e.g., 10).
"""

import uuid
import psycopg2
from psycopg2 import sql
from uuid import UUID, uuid4
from decimal import Decimal
import random
from datetime import datetime
from pymongo import MongoClient
import hashlib

# Import Pydantic schemas from schemas.py
from schemas import (
    # User service
    AdminUser, CustomerUser,
    # Order service - WRITE
    OrderEntity, OrderItemEntity, OrderAddressEntity, OrderStatus,
    # Inventory service - WRITE
    InventoryEntity, InventoryItem, WarehouseEntity,
    # Inventory service - READ
    CatalogData,
)

from create_document_inventory import create_inventory_collections
from create_document_order import create_order_collections
from create_document_user import create_user_collections
from dotenv import load_dotenv
import os

# ------------------------------------------------------------------------------
# CONFIG: Each service has its own Postgres and possibly its own Mongo
# ------------------------------------------------------------------------------
# Example configs:

load_dotenv()

# USER SERVICE (Write)
USER_POSTGRES_CONFIG = {
    "dbname": os.getenv("USER_DB_NAME"),
    "user": os.getenv("USER_DB_USER"),
    "password": os.getenv("USER_DB_PASSWORD"),
    "host": os.getenv("USER_DB_HOST"),
    "port": int(os.getenv("USER_DB_PORT"))
}

# ORDER SERVICE (Write)
ORDER_POSTGRES_CONFIG = {
    "dbname": os.getenv("ORDER_DB_NAME"),
    "user": os.getenv("ORDER_DB_USER"),
    "password": os.getenv("ORDER_DB_PASSWORD"),
    "host": os.getenv("ORDER_DB_HOST"),
    "port": int(os.getenv("ORDER_DB_PORT"))
}

# INVENTORY SERVICE (Write)
INVENTORY_POSTGRES_CONFIG = {
    "dbname": os.getenv("INVENTORY_DB_NAME"),
    "user": os.getenv("INVENTORY_DB_USER"),
    "password": os.getenv("INVENTORY_DB_PASSWORD"),
    "host": os.getenv("INVENTORY_DB_HOST"),
    "port": int(os.getenv("INVENTORY_DB_PORT"))
}

# MONGODB (READ)
MONGO_URI = os.getenv("MONGO_URI")
USER_MONGO_URI = MONGO_URI
ORDER_MONGO_URI = MONGO_URI
INVENTORY_MONGO_URI = MONGO_URI

# ------------------------------------------------------------------------------
# HELPER FUNCTIONS
# ------------------------------------------------------------------------------
def get_postgres_connection(config_dict):
    return psycopg2.connect(**config_dict)

def run_sql_file(filepath: str, config_dict):
    conn = get_postgres_connection(config_dict)
    cursor = conn.cursor()
    try:
        with open(filepath, "r", encoding="utf-8") as f:
            sql_script = f.read()
        cursor.execute(sql_script)
        conn.commit()
    except Exception as e:
        conn.rollback()
        raise e
    finally:
        cursor.close()
        conn.close()

def get_mongo_client(uri):
    return MongoClient(uri)

def generate_static_uuid(model_name: str, unique_id: int) -> str:
    """
    Generate a static but unique UUID for each model based on the model name and a unique identifier.

    Args:
        model_name (str): The name of the model (e.g., "AdminUser", "Order", "Inventory").
        unique_id (int): A unique identifier for the record (e.g., sequential number).

    Returns:
        str: A UUID string.
    """
    unique_string = f"{model_name}-{unique_id}"
    hashed = hashlib.md5(unique_string.encode()).hexdigest()
    return str(uuid.UUID(hashed))

def create_order_detail_doc(
    order: OrderEntity,
    admin_user: AdminUser,
    customer_user: CustomerUser,
    warehouse_entity: WarehouseEntity,
    payment: dict
) -> dict:
    """
    Creates a fully aggregated order detail document with embedded admin, customer, warehouse, and items.
    This avoids the need for joins when reading Order Detail in MongoDB.
    """
    # Basic info
    doc = {
        "orderId": str(order.id),
        "orderStatus": order.orderStatus.value,
        "failureMessages": order.failureMessages,
        "totalAmount": str(order.totalAmount),
        "shippingCost": str(order.shippingCost),
        "customerAddress": order.customerAddress,
        
        # Embedded admin
        "admin": {
            "id": str(admin_user.id),
            "email": admin_user.email,
            "fullname": admin_user.fullname,
            "adminRole": admin_user.adminRole.value,
            "active": admin_user.active
        },
        
        # Embedded customer
        "customer": {
            "id": str(customer_user.id),
            "email": customer_user.email,
            "fullname": customer_user.fullname
        },
        
        # Embedded warehouse
        "warehouse": {
            "id": str(warehouse_entity.id),
            "name": warehouse_entity.name,
            "latitude": warehouse_entity.latitude,
            "longitude": warehouse_entity.longitude
        },
        
        # Items
        "items": [],
        
        # Address (if we want to store it as a string or sub-doc)
        "address": None
    }
    
    # Convert items
    item_docs = []
    for it in order.items:
        item_docs.append({
            "productId": str(it.productId),
            "price": str(it.price),
            "quantity": it.quantity,
            "subTotal": str(it.subTotal)
        })
    doc["items"] = item_docs
    
    # If order.address is not None, embed it
    if order.address:
        addr = order.address
        address_str = f"{addr.street}, {addr.city}, {addr.postalCode}"
        doc["address"] = address_str
    
    # Embed payment data
    doc["payment"] = {
        "paymentMethod": payment["paymentMethod"],
        "paymentStatus": payment["paymentStatus"],
        "paymentAmount": str(payment["paymentAmount"]),
        "paymentReference": payment["paymentReference"],
        "paymentDate": payment["paymentDate"].isoformat(),
        "paymentProofUrl": payment["paymentProofUrl"]
    }

    return doc


# ------------------------------------------------------------------------------
# INSERT FUNCTIONS (WRITE DB)
# ------------------------------------------------------------------------------
def insert_admin_user_postgres(user: AdminUser):
    """
    Insert admin user into the User Service Postgres after checking for existence.
    """
    conn = get_postgres_connection(USER_POSTGRES_CONFIG)
    cursor = conn.cursor()
    try:
        # Check if the record exists
        check_query = "SELECT 1 FROM admin_users WHERE id = %s"
        cursor.execute(check_query, (str(user.id),))
        exists = cursor.fetchone()
        
        if not exists:
            # Insert new record
            q = """
            INSERT INTO admin_users (id, email, fullname, admin_role, active)
            VALUES (%s, %s, %s, %s, %s)
            """
            cursor.execute(q, (
                str(user.id), user.email, user.fullname,
                user.adminRole.value, user.active
            ))
            conn.commit()
        else:
            print(f"[INFO] Admin user with ID {user.id} already exists. Skipping insert.")
    except Exception as e:
        conn.rollback()
        raise e
    finally:
        cursor.close()
        conn.close()

def insert_customer_user_postgres(user: CustomerUser):
    """
    Insert customer user into the User Service Postgres after checking for existence.
    """
    conn = get_postgres_connection(USER_POSTGRES_CONFIG)
    cursor = conn.cursor()
    try:
        # Check if the record exists
        check_query = "SELECT 1 FROM customer_users WHERE id = %s"
        cursor.execute(check_query, (str(user.id),))
        exists = cursor.fetchone()
        
        if not exists:
            # Insert new record
            q = """
            INSERT INTO customer_users (id, email, fullname)
            VALUES (%s, %s, %s)
            """
            cursor.execute(q, (user.id, user.email, user.fullname))
            conn.commit()
        else:
            print(f"[INFO] Customer user with ID {user.id} already exists. Skipping insert.")
    except Exception as e:
        conn.rollback()
        raise e
    finally:
        cursor.close()
        conn.close()

def insert_warehouse_postgres(wh: WarehouseEntity):
    """
    Insert warehouse into Inventory Service Postgres after checking for existence.
    """
    conn = get_postgres_connection(INVENTORY_POSTGRES_CONFIG)
    cursor = conn.cursor()
    try:
        # Check if the record exists
        check_query = "SELECT 1 FROM warehouses WHERE id = %s"
        cursor.execute(check_query, (str(wh.id),))
        exists = cursor.fetchone()
        
        if not exists:
            # Insert new record
            q = """
            INSERT INTO warehouses (id, name, latitude, longitude)
            VALUES (%s, %s, %s, %s)
            """
            cursor.execute(q, (wh.id, wh.name, wh.latitude, wh.longitude))
            conn.commit()
        else:
            print(f"[INFO] Warehouse with ID {wh.id} already exists. Skipping insert.")
    except Exception as e:
        conn.rollback()
        raise e
    finally:
        cursor.close()
        conn.close()

def insert_inventory_postgres(inv: InventoryEntity):
    """
    Insert inventory + items into Inventory Service Postgres after checking for existence.
    """
    conn = get_postgres_connection(INVENTORY_POSTGRES_CONFIG)
    cursor = conn.cursor()
    try:
        # Check if the inventory record exists
        check_query = "SELECT 1 FROM inventory WHERE id = %s"
        cursor.execute(check_query, (inv.id,))
        exists = cursor.fetchone()
        
        if not exists:
            # Insert inventory record
            q_inv = """
            INSERT INTO inventory (id, product_id, total_quantity, created_at, updated_at, deleted_at)
            VALUES (%s, %s, %s, %s, %s, %s)
            """
            cursor.execute(q_inv, (
                inv.id, inv.productId, inv.totalQuantity,
                inv.createdAt, inv.updatedAt, inv.deletedAt
            ))

            # Insert inventory items
            q_item = """
            INSERT INTO inventory_items (id, inventory_id, warehouse_id, quantity, created_at, updated_at, deleted_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            """
            for it in inv.inventoryItems:
                cursor.execute(q_item, (
                    it.id, inv.id, it.warehouseId, it.quantity,
                    it.createdAt, it.updatedAt, it.deletedAt
                ))
            conn.commit()
        else:
            print(f"[INFO] Inventory with ID {inv.id} already exists. Skipping insert.")
    except Exception as e:
        conn.rollback()
        raise e
    finally:
        cursor.close()
        conn.close()

def insert_order_postgres(order: OrderEntity):
    """
    Insert order, address, items into Order Service Postgres after checking for existence.
    """
    conn = get_postgres_connection(ORDER_POSTGRES_CONFIG)
    cursor = conn.cursor()
    try:
        # Check if the order record exists
        check_query = "SELECT 1 FROM orders WHERE id = %s"
        cursor.execute(check_query, (order.id,))
        exists = cursor.fetchone()
        
        if not exists:
            # Insert order record
            q_order = """
            INSERT INTO orders (id, customer_id, customer_address, warehouse_id, total_amount, shipping_cost, order_status, failure_messages)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
            """
            cursor.execute(q_order, (
                order.id, order.customerId, order.customerAddress,
                order.warehouseId, order.totalAmount, order.shippingCost,
                order.orderStatus.value, order.failureMessages
            ))

            # Insert address
            if order.address:
                addr = order.address
                q_address = """
                INSERT INTO order_address (id, city, latitude, longitude, postal_code, street, order_id)
                VALUES (%s, %s, %s, %s, %s, %s, %s)
                """
                cursor.execute(q_address, (
                    str(addr.id), addr.city, addr.latitude, addr.longitude,
                    addr.postalCode, addr.street, str(addr.orderId)
                ))

            # Insert items
            q_items = """
            INSERT INTO order_items (id, order_id, product_id, price, quantity, sub_total)
            VALUES (%s, %s, %s, %s, %s, %s)
            """
            for it in order.items:
                cursor.execute(q_items, (
                    str(it.id), str(it.orderId), str(it.productId),
                    it.price, it.quantity, it.subTotal
                ))
            conn.commit()
        else:
            print(f"[INFO] Order with ID {order.id} already exists. Skipping insert.")
    except Exception as e:
        conn.rollback()
        raise e
    finally:
        cursor.close()
        conn.close()

def insert_payment_postgres(payment: dict):
    """
    Insert payment data into the payments table after checking for existence.
    """
    conn = get_postgres_connection(ORDER_POSTGRES_CONFIG)
    cursor = conn.cursor()
    try:
        # Check if the payment record exists
        check_query = "SELECT 1 FROM payments WHERE id = %s"
        cursor.execute(check_query, (payment["id"],))
        exists = cursor.fetchone()
        
        if not exists:
            # Insert payment record
            q_payment = """
            INSERT INTO payments (id, order_id, payment_method, payment_status, payment_date, payment_amount, payment_reference, payment_proof_url)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
            """
            cursor.execute(q_payment, (
                payment["id"], payment["orderId"], payment["paymentMethod"],
                payment["paymentStatus"], payment["paymentDate"], payment["paymentAmount"],
                payment["paymentReference"], payment["paymentProofUrl"]
            ))
            conn.commit()
        else:
            print(f"[INFO] Payment with ID {payment['id']} already exists. Skipping insert.")
    except Exception as e:
        conn.rollback()
        raise e
    finally:
        cursor.close()
        conn.close()

# ------------------------------------------------------------------------------
# MONGO (READ) INSERTS
# ------------------------------------------------------------------------------
def insert_admin_data_order_mongo(admin: AdminUser):
    """
    Insert or update Admin user doc in Order Service's Mongo (READ).
    """
    client = get_mongo_client(ORDER_MONGO_URI)
    db = client["order_read_db"]
    admins_coll = db["admins"]
    doc = {
        "id": str(admin.id),
        "email": admin.email,
        "fullname": admin.fullname,
        "adminRole": admin.adminRole.value,
        "active": admin.active
    }
    admins_coll.replace_one({"id": doc["id"]}, doc, upsert=True)
    client.close()

def insert_admin_data_inventory_mongo(admin: AdminUser):
    """
    Insert or update Admin user doc in Inventory Service's Mongo (READ).
    """
    client = get_mongo_client(INVENTORY_MONGO_URI)
    db = client["inventory_read_db"]
    admins_coll = db["admins"]
    doc = {
        "id": str(admin.id),
        "email": admin.email,
        "fullname": admin.fullname,
        "adminRole": admin.adminRole.value,
        "active": admin.active
    }
    admins_coll.replace_one({"id": doc["id"]}, doc, upsert=True)
    client.close()

def insert_customer_data_order_mongo(cust: CustomerUser):
    """
    Insert or update Customer user doc in Order Service's Mongo (READ).
    """
    client = get_mongo_client(ORDER_MONGO_URI)
    db = client["order_read_db"]
    coll = db["customers"]
    doc = {
        "id": str(cust.id),
        "email": cust.email,
        "fullname": cust.fullname
    }
    coll.replace_one({"id": doc["id"]}, doc, upsert=True)
    client.close()

def insert_warehouse_data_order_mongo(wh: WarehouseEntity):
    """
    Insert or update Warehouse doc in Order Service's Mongo (READ).
    """
    client = get_mongo_client(ORDER_MONGO_URI)
    db = client["order_read_db"]
    coll = db["warehouses"]
    doc = {
        "id": str(wh.id),
        "name": wh.name,
        "latitude": wh.latitude,
        "longitude": wh.longitude
    }
    coll.replace_one({"id": doc["id"]}, doc, upsert=True)
    client.close()

def insert_catalog_data_inventory_mongo(cat: CatalogData):
    """
    Insert or update Catalog doc in Inventory Service's Mongo (READ).
    """
    client = get_mongo_client(INVENTORY_MONGO_URI)
    db = client["inventory_read_db"]
    coll = db["catalogs"]
    
    data = cat.dict()
    if isinstance(data.get("id"), uuid.UUID):
        data["id"] = str(data["id"])
    
    coll.replace_one({"id": data["id"]}, data, upsert=True)
    client.close()


def insert_product_data_order_mongo(product_id, name, price):
    """
    Insert or update a Product doc in Order Service's Mongo (READ).
    """
    client = get_mongo_client(ORDER_MONGO_URI)
    db = client["order_read_db"]
    coll = db["products"]
    doc = {
        "id": str(product_id),
        "name": name,
        "price": str(price)
    }
    coll.replace_one({"id": doc["id"]}, doc, upsert=True)
    client.close()

def insert_order_detail_doc_mongo(order_doc: dict):
    """
    Insert or update an aggregated order detail document in Order Service's MongoDB.
    """
    client = get_mongo_client(ORDER_MONGO_URI)
    db = client["order_read_db"]
    coll = db["orders"]  # or "orders", depending on your design
    coll.replace_one({"orderId": order_doc["orderId"]}, order_doc, upsert=True)
    client.close()


# ------------------------------------------------------------------------------
# SAMPLE DATA GENERATION
# ------------------------------------------------------------------------------
def random_decimal(min_v=1, max_v=100):
    val = round(random.uniform(min_v, max_v), 2)
    return float(Decimal(str(val)))

def generate_product_data():
    """
    Returns a tuple (product_id, name, price).
    """
    return (uuid4(), f"Product_{random.randint(1,9999)}", random_decimal(10,200))

def generate_admin_user(index: int) -> AdminUser:
    return AdminUser(
        # id=str(uuid4()),
        id=generate_static_uuid("AdminUser", index * 1000 + index),
        email=f"admin_{random.randint(1,9999)}@example.com",
        fullname="Admin Example",
        adminRole=random.choice(["SUPER_ADMIN", "WAREHOUSE_ADMIN"]),
        active=True
    )

def generate_customer_user(index: int) -> CustomerUser:
    return CustomerUser(
        # id=str(uuid4()),
        id=generate_static_uuid("CustomerUser", index),
        email=f"cust_{random.randint(1,9999)}@example.com",
        fullname="Customer Example"
    )

def generate_warehouse_entity(index: int) -> WarehouseEntity:
    return WarehouseEntity(
        # id=str(uuid4()),
        id=generate_static_uuid("Warehouse", index),
        name=f"Warehouse_{random.randint(1,9999)}",
        latitude=round(random.uniform(-8,8),4),
        longitude=round(random.uniform(100,140),4)
    )

def generate_catalog_data(index: int) -> CatalogData:
    return CatalogData(
        # id=str(uuid4()),
        id=generate_static_uuid("Catalog", index),
        name=f"Catalog_{random.randint(1,9999)}",
        brand=random.choice(["Nike", "Adidas", "Puma"]),
        price=random_decimal(50, 1000),
        image=None,
        size=random.choice(["S","M","L","XL"]),
        availableColors=["red","blue","green","yellow","black","white"],
        maxQuantity=100,
        quantity=1
    )

def generate_inventory(index, product_id: UUID, warehouse_id: UUID) -> InventoryEntity:
    now = datetime.now()
    # inv_id = str(uuid4())
    inv_id = generate_static_uuid("Inventory", index)
    items = []
    for idx in range(random.randint(1,2)):
        # it_id = str(uuid4())
        it_id = generate_static_uuid("InventoryItem", index * 1000 + idx)
        qty = random.randint(1,500)
        items.append(
            InventoryItem(
                id=it_id,
                inventoryId=inv_id,
                warehouseId=warehouse_id,
                quantity=qty,
                createdAt=now,
                updatedAt=now,
                deletedAt=None,
                stockJournals=[]
            )
        )

    return InventoryEntity(
        id=str(inv_id),
        productId=str(product_id),
        totalQuantity=sum(i.quantity for i in items),
        createdAt=now,
        updatedAt=now,
        deletedAt=None,
        inventoryItems=items
    )

def generate_order(index: int, warehouse_id: UUID, customer_id: UUID) -> OrderEntity:
    oid = generate_static_uuid("Order", index)
    addr_id = generate_static_uuid("OrderAddress", index)
    items = []
    for item_index in range(random.randint(1, 3)):
        i_id = random.randint(1000,9999)
        # i_id = generate_static_uuid("OrderItem", index * 1000 + item_index)
        pr = random_decimal(10, 200)
        qty = random.randint(1, 5)
        items.append(
            OrderItemEntity(
                id=i_id,
                orderId=oid,
                productId=generate_static_uuid("Product", index * 1000 + item_index),
                price=pr,
                quantity=qty,
                subTotal=pr * qty
            )
        )

    total_amt = sum(i.subTotal for i in items)
    shipping = random_decimal(5, 25)
    return OrderEntity(
        id=oid,
        customerId=customer_id,
        customerAddress="Some Street",
        warehouseId=warehouse_id,
        totalAmount=total_amt,
        shippingCost=shipping,
        orderStatus=random.choice(list(OrderStatus)),
        failureMessages=None,
        address=OrderAddressEntity(
            id=addr_id,
            city="SomeCity",
            latitude=-6.2,
            longitude=106.8,
            postalCode="12345",
            street="MainStreet",
            orderId=oid
        ),
        items=items
    )

def generate_payment(index: int, order_id: UUID) -> dict:
    """
    Generate payment data for an order.
    """
    return {
        # "id": str(uuid4()),
        "id": generate_static_uuid("Payment", index),
        "orderId": order_id,
        "paymentMethod": random.choice(["CREDIT_CARD", "BANK_TRANSFER", "PAYPAL"]),
        "paymentStatus": random.choice(["COMPLETED", "PENDING", "FAILED"]),
        "paymentDate": datetime.now(),
        "paymentAmount": random_decimal(100, 500),  # Payment amount between 100 and 500
        "paymentReference": f"REF-{random.randint(100000, 999999)}",
        "paymentProofUrl": f"http://example.com/proof/{random.randint(1000, 9999)}"
    }


# ------------------------------------------------------------------------------
# MAIN
# ------------------------------------------------------------------------------
def main():
    """
    1) Run .sql for user, order, inventory
    2) Insert data with consistent IDs across microservices
    3) Loop some times (e.g., 10) for basic load test
    """
    # 1) Create schemas
    # a. Postgres
    run_sql_file("user-schema.sql", USER_POSTGRES_CONFIG)
    run_sql_file("order-schema.sql", ORDER_POSTGRES_CONFIG)
    run_sql_file("inventory-schema.sql", INVENTORY_POSTGRES_CONFIG)
    print("[INFO] All Postgres schemas created.")

    # b. MongoDB
    create_inventory_collections()
    create_order_collections()
    create_user_collections()
    print("[INFO] All Mongo schemas created.")

    # 2) Data injection
    # Admin users
    admin_list = []
    for idx in range(2):
        admin = generate_admin_user(idx)
        insert_admin_user_postgres(admin)               # user (write)
        insert_admin_data_order_mongo(admin)            # order (read)
        insert_admin_data_inventory_mongo(admin)        # inventory (read)
        admin_list.append(admin)
    print("[INFO] All Admin Data Inserted.")

    # Customer users
    cust_list = []
    for idx in range(2):
        cust = generate_customer_user(idx)
        insert_customer_user_postgres(cust)             # user (write)
        insert_customer_data_order_mongo(cust)          # order (read)
        cust_list.append(cust)
    print("[INFO] All Customer Data Inserted.")

    # Warehouses
    wh_list = []
    for idx in range(2):
        wh = generate_warehouse_entity(idx)
        insert_warehouse_postgres(wh)                   # inventory (write)
        insert_warehouse_data_order_mongo(wh)           # order (read)
        wh_list.append(wh)
    print("[INFO] All Warehouse Data Inserted.")

    # Catalog (inventory read)
    for idx in range(3):
        cdoc = generate_catalog_data(idx)
        insert_catalog_data_inventory_mongo(cdoc)
    print("[INFO] All Catalog Data Inserted.")

    # Inventory + Items
    # We assume we have "products" in a table, or we consider the same "catalog" as products
    # For simplicity, let's reuse the catalog as the product ID
    for cat_index in range(3):
        # product_id = uuid4()
        product_id = generate_static_uuid("Product", cat_index)
        # or reuse cdoc.id if you want the same ID
        wh = random.choice(wh_list)
        inv = generate_inventory(cat_index, product_id, wh.id)
        insert_inventory_postgres(inv)  # inventory (write)
    print("[INFO] All Inventory Data Inserted.")

    # 3) Create orders with aggregated doc
    # Let's do 10 orders, each referencing random admin, customer, warehouse
    for i in range(10):
        selected_admin = random.choice(admin_list)
        selected_customer = random.choice(cust_list)
        selected_wh = random.choice(wh_list)
        
        # Create an order referencing warehouse + customer
        order_entity = generate_order(i, selected_wh.id, selected_customer.id)
        
        # Insert to Postgres (WRITE)
        insert_order_postgres(order_entity)

        # Generate and insert payment for the order
        payment_data = generate_payment(i, order_entity.id)
        insert_payment_postgres(payment_data)
        
        # Now create aggregated doc for Mongo READ
        # We embed the selected_admin, selected_customer, and selected_wh
        order_detail_doc = create_order_detail_doc(order_entity, selected_admin, selected_customer, selected_wh, payment_data)
        
        # Insert aggregated doc to Order Service's Mongo
        insert_order_detail_doc_mongo(order_detail_doc)
    print("[INFO] All Order Data Inserted.")

    print("[INFO] Data injection completed successfully!")

if __name__ == "__main__":
    main()
