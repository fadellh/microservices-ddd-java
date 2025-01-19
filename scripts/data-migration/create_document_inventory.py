"""
create_documents_inventory.py

This script creates the MongoDB collections and indexes for the Inventory Service (READ side).
"""

from pymongo import MongoClient, ASCENDING, TEXT
from dotenv import load_dotenv
import os

load_dotenv()

INVENTORY_MONGO_URI = os.getenv("MONGO_URI")
INVENTORY_MONGO_DB_NAME = "inventory_read_db"

def get_inventory_mongo_client():
    return MongoClient(INVENTORY_MONGO_URI)

def create_inventory_collections():
    """
    Creates necessary collections for Inventory Service read model in MongoDB,
    such as 'catalogs', 'admins' (if needed), etc.
    Sets up appropriate indexes.
    """
    client = get_inventory_mongo_client()
    db = client[INVENTORY_MONGO_DB_NAME]

    # catalogs collection
    catalogs_coll = db["catalogs"]
    catalogs_coll.create_index([("id", ASCENDING)], name="idx_catalog_id")
    # catalogs_coll.create_index([("name", TEXT)], name="text_catalog_name")
    # catalogs_coll.drop_index("text_catalog_name")
    catalogs_coll.create_index(
        [("name", "text"), ("brand", "text"), ("description", "text")],
        name="catalog_text_index",
        weights={"name": 5, "brand": 3, "description": 1}
    )

    # admins collection (if Inventory Service also displays admin data)
    admins_coll = db["admins"]
    admins_coll.create_index([("id", ASCENDING)], name="idx_admin_id")

    client.close()

def main():
    create_inventory_collections()
    print("Inventory Service Mongo collections and indexes created.")

if __name__ == "__main__":
    main()
