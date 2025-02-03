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

from sample_data import warehouse_locations, shoe_brands, shoe_models, indonesian_names, asean_names, customer_locations
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
        unique_id (int): A unique integer used to differentiate the record.

    Returns:
        str: A UUID string derived from a stable MD5 hash of model_name + unique_id.
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
        "customerId": str(customer_user.id),
        "warehouseId": str(warehouse_entity.id),
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
            "city": warehouse_entity.city,
            "district": warehouse_entity.district,
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
            "name": it.productName,
            "inventoryId": str(it.inventoryId),
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
            INSERT INTO customer_users (id, jwt_user_id, email, fullname, address, city, district, latitude, longitude)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            cursor.execute(q, (
                user.id,
                user.jwt_user_id,
                user.email,
                user.fullname,
                user.address,
                user.city,
                user.district,
                user.latitude,
                user.longitude
            ))
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
            INSERT INTO warehouses (id, name, city, district ,latitude, longitude)
            VALUES (%s, %s, %s,%s,%s, %s)
            """
            cursor.execute(q, (wh.id, wh.name,wh.city,wh.district, wh.latitude, wh.longitude))
            conn.commit()
        else:
            print(f"[INFO] Warehouse with ID {wh.id} already exists. Skipping insert.")
    except Exception as e:
        conn.rollback()
        raise e
    finally:
        cursor.close()
        conn.close()

def insert_inventory_postgres(inv: InventoryEntity, invoice_id_set: set):
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
            INSERT INTO inventory (id, product_id, name, price, total_quantity, created_at, updated_at, deleted_at)
            VALUES (%s, %s,%s, %s, %s, %s, %s, %s)
            """
            if inv.id not in invoice_id_set:
                cursor.execute(q_inv, (
                    inv.id, inv.productId, inv.name, inv.price, inv.totalQuantity,
                    inv.createdAt, inv.updatedAt, inv.deletedAt
                ))

            # Insert inventory items
            q_item = """
            INSERT INTO inventory_items (id, inventory_id, warehouse_id, quantity, created_at, updated_at, deleted_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            """
            for it in inv.inventoryItems:
                if it.id not in invoice_id_set:
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
            INSERT INTO order_items (id, order_id, product_name ,product_id, inventory_id, price, quantity, sub_total)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
            """
            for it in order.items:
                cursor.execute(q_items, (
                    str(it.id), str(it.orderId), str(it.productName) ,str(it.productId), str(it.inventoryId),
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
        "jwt_user_id": str(cust.jwt_user_id),
        "latitude": cust.latitude,
        "longitude": cust.longitude,
        "city": cust.city,
        "address": cust.address,
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
        "city": wh.city,
        "district": wh.district,
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
    coll = db["orders"]
    coll.replace_one({"orderId": order_doc["orderId"]}, order_doc, upsert=True)
    client.close()

# ------------------------------------------------------------------------------
# SAMPLE DATA GENERATION
# ------------------------------------------------------------------------------
def random_integer(min_v=1, max_v=100):
    val = round(random.uniform(min_v, max_v))
    # return float(Decimal(str(val)))
    return val

def generate_admin_user(index: int) -> AdminUser:
    return AdminUser(
        id=generate_static_uuid("AdminUser", index),
        email=f"admin_{random.randint(1,9999)}@example.com",
        fullname="Admin Example",
        adminRole=random.choice(["SUPER_ADMIN", "WAREHOUSE_ADMIN"]),
        active=True
    )

def generate_customer_user(index: int) -> CustomerUser:
    location = random.choice(customer_locations)
    
    # 80% chance to pick an Indonesian name, 20% chance for an ASEAN name
    fullname = random.choice(indonesian_names) if random.random() < 0.8 else random.choice(asean_names)
    email_prefix = fullname.lower().replace(" ", ".")  # Convert to lowercase & replace spaces with dots
    email = f"{email_prefix}{index}@example.com"

    if index == 0:
        return CustomerUser(
            id=generate_static_uuid("CustomerUser", index),
            jwt_user_id = "3bfBIna23GW1SXpqbrKUOJM54BI2",
            email = "fadel.lukman.dev@gmail.com",
            fullname= "Fadel",
            address="Malang",
            city="Malang",
            district="Malang",
            latitude=-7.9666,
            longitude=112.6326,
        )

    return CustomerUser(
        id=generate_static_uuid("CustomerUser", index),
        jwt_user_id=str(uuid4()),
        email=email,
        fullname=fullname,
        address=f"{random.randint(1,500)} {random.choice(['Jl. Sudirman', 'Jl. Thamrin', 'Jl. Merdeka', 'Jl. Diponegoro'])}",
        city=location["city"],
        district=location["district"],
        latitude=location["latitude"],
        longitude=location["longitude"]
    )

def generate_warehouse_entity(index: int) -> WarehouseEntity:
    location = random.choice(warehouse_locations)
    return WarehouseEntity(
        id=generate_static_uuid("Warehouse", index),
        name=f"Warehouse_{location['city']}_{random.randint(1,9999)}",
        city=location["city"],
        district=location["district"],
        latitude=location["latitude"],
        longitude=location["longitude"]
    )

def generate_catalog_data(index: int) -> CatalogData:
    """
    Generate a 'catalog' record used for the Inventory (read) side.
    We'll reuse the same ID for product references if desired.
    """
    return CatalogData(
        id=generate_static_uuid("CatalogProduct", index),
        name=f"{random.choice(shoe_brands)} {random.choice(shoe_models)} {random.randint(1,99)}",
        brand=random.choice(shoe_brands),
        price=random.randint(50, 1000),
        image=None,
        size=random.choice(["US 6", "US 7", "US 8", "US 9", "US 10", "US 11", "US 12", "EU 40", "EU 41", "EU 42"]),
        availableColors=["Red", "Blue", "Green", "Yellow", "Black", "White", "Grey", "Navy", "Beige", "Brown"],
        maxQuantity=100,
        quantity=1
    )

def generate_inventory(index: int, catalog_data: CatalogData, wh_list: list) -> InventoryEntity:
    now = datetime.now()
    # inv_id = generate_static_uuid("Inventory", index)  # returns str already
    items = []
    inv_id = str(catalog_data.id)
    
    item_idx = 0
    for wh in wh_list:
        it_id = generate_static_uuid("InventoryItem", index*1000 + item_idx)
        qty = random.randint(1, 500)
        items.append(
            InventoryItem(
                id=it_id,
                inventoryId=inv_id,         # also a str
                warehouseId=str(wh.id),
                quantity=qty,
                createdAt=now,
                updatedAt=now,
                deletedAt=None,
                stockJournals=[]
            )
        )
        item_idx += 1


    total_qty = sum(i.quantity for i in items)
    catalog_data.maxQuantity = total_qty # Update the max quantity to the total

    return InventoryEntity(
        id=inv_id,                     # already str
        name=catalog_data.name,
        price=catalog_data.price,
        productId=str(catalog_data.id),     # convert to string
        totalQuantity=total_qty,
        createdAt=now,
        updatedAt=now,
        deletedAt=None,
        inventoryItems=items
    )

def generate_order(index: int, warehouse_id: str, customer: CustomerUser, inventory_list) -> OrderEntity:
    """
    Generate an Order referencing an existing Warehouse + Customer.
    For OrderItems, pick a random inventory from `inventory_list`,
    so the productId & inventoryId remain consistent across microservices.
    """
    oid = generate_static_uuid("Order", index)
    addr_id = generate_static_uuid("OrderAddress", index)

    # Build items by picking random existing inventory
    num_items = random.randint(1, MAX_ORDER_ITEMS_RANGE)
    items = []
    inventory_item_set = set()
    for item_index in range(num_items):
        chosen_inv : InventoryEntity = random.choice(inventory_list)  # pick from existing
        if chosen_inv.id in inventory_item_set:
            continue
        # Optionally, we can re-use the same product price from the catalog or generate random
        # price = random_decimal(10, 200)
        # price = random_decimal(10, 200)  # or override with a "catalog price"
        qty = random.randint(1, 5)

        # i_id = generate_static_uuid("OrderItem", index * 1000 + item_index)
        i_id = random.randint(1000, 9999)  # random int for now
        items.append(
            OrderItemEntity(
                id=i_id,
                orderId=oid,
                productId=chosen_inv.productId,     # reference the same productId
                inventoryId=chosen_inv.id,          # reference the same inventoryId
                productName=chosen_inv.name,          # reference the same name
                price=chosen_inv.price,              # reference the same price
                quantity=qty,
                subTotal=chosen_inv.price * qty
            )
        )
        inventory_item_set.add(chosen_inv.id)

    total_amt = sum(i.subTotal for i in items)
    shipping = random_integer(5, 25)

    return OrderEntity(
        id=oid,
        customerId=customer.id,
        customerAddress=customer.address,
        warehouseId=warehouse_id,
        totalAmount=total_amt,
        shippingCost=shipping,
        orderStatus=random.choice(list(OrderStatus)),
        failureMessages=None,
        address=OrderAddressEntity(
            id=addr_id,
            city=customer.city,
            latitude=customer.latitude,
            longitude=customer.longitude,
            postalCode="12345",
            street=customer.address,
            orderId=oid
        ),
        items=items
    )

def generate_payment(index: int, order_id: str) -> dict:
    """
    Generate payment data for an order.
    """
    return {
        "id": generate_static_uuid("Payment", index),
        "orderId": order_id,
        "paymentMethod": random.choice(["CREDIT_CARD", "BANK_TRANSFER", "PAYPAL"]),
        "paymentStatus": random.choice(["COMPLETED", "PENDING", "FAILED"]),
        "paymentDate": datetime.now(),
        "paymentAmount": random_integer(100, 500),  # Payment amount between 100 and 500
        "paymentReference": f"REF-{random.randint(100000, 999999)}",
        "paymentProofUrl": f"http://example.com/proof/{random.randint(1000, 9999)}"
    }

# ------------------------------------------------------------------------------
# MAIN
# ------------------------------------------------------------------------------

TOTAL_WAREHOUSES = 100
TOTAL_CUSTOMER_USERS = 100
TOTAL_ADMIN_USERS = 10
TOTAL_CATALOG_ITEMS = 100
TOTAL_ORDERS = 1000
MAX_ORDER_ITEMS_RANGE = 10

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
    # --------------------------------------------------------------------------
    # 2.a) Admin users
    admin_list = []
    for idx in range(TOTAL_ADMIN_USERS):
        admin = generate_admin_user(idx)
        # Write DB
        insert_admin_user_postgres(admin)
        # Read DB
        insert_admin_data_order_mongo(admin)
        insert_admin_data_inventory_mongo(admin)
        admin_list.append(admin)
    print("[INFO] All Admin Data Inserted.")

    # 2.b) Customer users
    cust_list = []
    for idx in range(TOTAL_CUSTOMER_USERS):
        cust = generate_customer_user(idx)
        # Write DB
        insert_customer_user_postgres(cust)
        # Read DB
        insert_customer_data_order_mongo(cust)
        cust_list.append(cust)
    print("[INFO] All Customer Data Inserted.")

    # 2.c) Warehouses
    wh_list = []
    for idx in range(TOTAL_WAREHOUSES):
        wh = generate_warehouse_entity(idx)
        insert_warehouse_postgres(wh)                   # Inventory (write)
        insert_warehouse_data_order_mongo(wh)           # Order (read)
        wh_list.append(wh)
    print("[INFO] All Warehouse Data Inserted.")

    # 2.d) Catalog (Inventory read)
    # Also treat each Catalog as a "product" for the sake of referencing
    catalog_list = []
    for idx in range(TOTAL_CATALOG_ITEMS):
        cdoc = generate_catalog_data(idx)
        insert_catalog_data_inventory_mongo(cdoc)
        # Also insert into Order's read DB as "product"
        # So the same ID is recognized in the Order Read side
        insert_product_data_order_mongo(cdoc.id, cdoc.name, cdoc.price)
        catalog_list.append(cdoc)
    print("[INFO] All Catalog Data Inserted into Inventory (read) and Product Data inserted into Order (read).")

    # 2.e) Inventory + Items
    # We tie each Inventory to the same ID as the Catalog's ID for productId
    # (So that Orders can reference them consistently)
    inventory_list = []
    inv_index = 0
    for cat in catalog_list:
        # For demonstration, create 2 inventories (2 different warehouses) per catalog
        # for wh in wh_list:
            invoice_id_set = set()
            # wh = random.choice(wh_list)
            inv : InventoryEntity = generate_inventory(inv_index, cat, wh_list)
            insert_inventory_postgres(inv, invoice_id_set)  # Inventory (write)
            inventory_list.append(inv)
            invoice_id_set.add(inv.id)
            inv_index += 1

    print("[INFO] All Inventory Data Inserted.")

    # 2.f) Create orders with aggregated doc
    # Let's do 10 orders, each referencing random admin, customer, warehouse
    # For each order, pick random items from inventory_list
    for i in range(TOTAL_ORDERS):
        selected_admin = random.choice(admin_list)
        selected_customer = random.choice(cust_list)
        if i == 0:
            selected_customer = cust_list[0]
        selected_wh = random.choice(wh_list)

        # Create an order referencing warehouse + customer + random inventory
        order_entity = generate_order(
            index=i,
            warehouse_id=selected_wh.id,
            customer=selected_customer,
            inventory_list=inventory_list  # pass the entire list to randomly pick items
        )

        # Insert to Postgres (WRITE)
        insert_order_postgres(order_entity)

        # Generate and insert payment for the order
        payment_data = generate_payment(i, order_entity.id)
        insert_payment_postgres(payment_data)

        # Now create aggregated doc for Mongo READ
        order_detail_doc = create_order_detail_doc(order_entity, selected_admin, selected_customer, selected_wh, payment_data)

        # Insert aggregated doc to Order Service's Mongo
        insert_order_detail_doc_mongo(order_detail_doc)

    print("[INFO] All Order Data Inserted.")
    print("[INFO] Data injection completed successfully!")

if __name__ == "__main__":
    main()
