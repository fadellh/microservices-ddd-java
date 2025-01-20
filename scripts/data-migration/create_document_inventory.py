"""
create_documents_inventory.py

This script creates the MongoDB collections and indexes for the Inventory Service (READ side).
"""

from pymongo import MongoClient, ASCENDING, TEXT
from pymongo.operations import SearchIndexModel
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

    # admins collection (if Inventory Service also displays admin data)
    admins_coll = db["admins"]
    admins_coll.create_index([("id", ASCENDING)], name="idx_admin_id")

    client.close()

    create_index()

def create_index():
    """
    Create an Atlas Search Index for the specified collection.
    """
    try:
        client = MongoClient(INVENTORY_MONGO_URI)
        print("[INFO] Connected to MongoDB Atlas.")

        database = client[INVENTORY_MONGO_DB_NAME]
        collection = database['catalogs']

        search_index_model = SearchIndexModel(
            definition={
                "mappings": {
                    "dynamic": True, 
                    "fields": {
                        "name": {"type": "string"},
                        "brand": {"type": "string"},
                        "description": {"type": "string"},
                        "price": {"type": "number"}
                    }
                }
            },
            name="basic_search", 
        )

        # Create the search index
        result = collection.create_search_index(model=search_index_model)
        print(f"[SUCCESS] Search index created: {result}")

    except Exception as e:
        print(f"[ERROR] Failed to create search index: {e}")
    finally:
        client.close()
        print("[INFO] MongoDB connection closed.")

def main():
    create_inventory_collections()
    print("Inventory Service Mongo collections and indexes created.")

if __name__ == "__main__":
    main()
