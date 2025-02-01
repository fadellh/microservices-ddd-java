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

def add_geospatial_index():
    """
    Adds a geospatial field to existing warehouse documents and creates a 2dsphere index.
    """
    client = get_order_mongo_client()
    db = client[ORDER_MONGO_DB_NAME]
    warehouses_coll = db["warehouses"]

    # Fetch warehouses that do not have a 'location' field
    warehouses = warehouses_coll.find({"location": {"$exists": False}})
    
    updates = []
    for warehouse in warehouses:
        if "latitude" in warehouse and "longitude" in warehouse:
            location = {
                "type": "Point",
                "coordinates": [warehouse["longitude"], warehouse["latitude"]]
            }
            updates.append({
                "filter": {"_id": warehouse["_id"]},
                "update": {"$set": {"location": location}}
            })
    
    # Perform bulk update
    if updates:
        for update in updates:
            warehouses_coll.update_one(update["filter"], update["update"])
        print(f"Updated {len(updates)} warehouse documents with location field.")
    else:
        print("No warehouses needed updating.")
    
    # Create 2dsphere index on location
    warehouses_coll.create_index([("location", "2dsphere")], name="idx_location_2dsphere")
    print("Geospatial index created on location field.")
    
    client.close()

def reindex_orders_collection():
    """
    Drops all existing indexes in the 'orders' collection and recreates them.
    """
    client = get_order_mongo_client()
    db = client[ORDER_MONGO_DB_NAME]
    orders_coll = db["orders"]

    # Drop all existing indexes
    print("Dropping all indexes on 'orders' collection...")
    orders_coll.drop_indexes()
    print("Indexes dropped successfully.")

    # Recreate necessary indexes
    print("Recreating indexes...")
    orders_coll.create_index([("orderId", ASCENDING)], name="idx_orderId")
    orders_coll.create_index([("customerId", ASCENDING)], name="idx_customerId")
    orders_coll.create_index([("items.productName", TEXT)], name="text_item_productName")
    print("Indexes recreated successfully.")

    client.close()

def main():
    # create_order_collections()
    # print("Order Service Mongo collections and indexes created.")

    # add_geospatial_index()
    # print("Geospatial setup complete.")

    reindex_orders_collection()
    print("Order collection reindexed.")

if __name__ == "__main__":
    main()
