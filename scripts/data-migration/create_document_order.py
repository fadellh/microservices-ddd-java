"""
create_documents_order.py

This script creates the MongoDB collections and indexes for the Order Service (READ side).
It is assumed the Order Service uses its own MongoDB instance or database.
"""

from pymongo import MongoClient, ASCENDING, TEXT
from dotenv import load_dotenv
import os

load_dotenv()


ORDER_MONGO_URI = os.getenv("MONGO_URI")
ORDER_MONGO_DB_NAME = "order_read_db"

def get_order_mongo_client():
    return MongoClient(ORDER_MONGO_URI)

def create_order_collections():
    """
    Creates the necessary collections for the Order Service read model in MongoDB,
    such as 'orders', 'admins', 'customers', 'products', 'warehouses', etc.
    Sets up appropriate indexes.
    """
    client = get_order_mongo_client()
    db = client[ORDER_MONGO_DB_NAME]

    # orders collection (for aggregated order detail docs, if needed)
    orders_coll = db["orders"]
    orders_coll.create_index([("orderId", ASCENDING)], name="idx_orderId")
    orders_coll.create_index([("customerId", ASCENDING)], name="idx_customerId")
    # maybe a text index on "items.productName"
    orders_coll.create_index([("items.productName", TEXT)], name="text_item_productName")

    # admins collection
    admins_coll = db["admins"]
    admins_coll.create_index([("id", ASCENDING)], name="idx_admin_id")

    # customers collection
    customers_coll = db["customers"]
    customers_coll.create_index([("id", ASCENDING)], name="idx_customer_id")

    # products collection
    products_coll = db["products"]
    products_coll.create_index([("id", ASCENDING)], name="idx_product_id")
    products_coll.create_index([("name", TEXT)], name="text_product_name")

    # warehouses collection
    warehouses_coll = db["warehouses"]
    warehouses_coll.create_index([("id", ASCENDING)], name="idx_warehouse_id")

    client.close()

def main():
    create_order_collections()
    print("Order Service Mongo collections and indexes created.")

if __name__ == "__main__":
    main()
