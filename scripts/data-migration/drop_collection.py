from pymongo import MongoClient
from dotenv import load_dotenv
import os

# Load environment variables from .env file
load_dotenv()

# MongoDB connection URIs
USER_MONGO_URI = os.getenv("MONGO_URI")
ORDER_MONGO_URI = os.getenv("MONGO_URI")
INVENTORY_MONGO_URI = os.getenv("MONGO_URI")

# Databases
USER_MONGO_DB_NAME = "user_read_db"
ORDER_MONGO_DB_NAME = "order_read_db"
INVENTORY_MONGO_DB_NAME = "inventory_read_db"

# Collections to drop for each service
USER_COLLECTIONS = ["admin_users", "customer_users"]
ORDER_COLLECTIONS = ["orders", "admins", "customers", "products", "warehouses"]
INVENTORY_COLLECTIONS = ["catalogs", "admins"]

def drop_collections(mongo_uri, database_name, collections):
    """
    Drops specified collections from a MongoDB database.
    """
    client = MongoClient(mongo_uri)
    db = client[database_name]

    for collection in collections:
        if collection in db.list_collection_names():
            db[collection].drop()
            print(f"[INFO] Dropped collection: {collection} from database: {database_name}")
        else:
            print(f"[INFO] Collection '{collection}' does not exist in database: {database_name}. Skipping.")
    
    client.close()

def main():
    """
    Main function to drop MongoDB collections for all services.
    """
    print("[INFO] Dropping collections for User Service...")
    drop_collections(USER_MONGO_URI, USER_MONGO_DB_NAME, USER_COLLECTIONS)

    print("[INFO] Dropping collections for Order Service...")
    drop_collections(ORDER_MONGO_URI, ORDER_MONGO_DB_NAME, ORDER_COLLECTIONS)

    print("[INFO] Dropping collections for Inventory Service...")
    drop_collections(INVENTORY_MONGO_URI, INVENTORY_MONGO_DB_NAME, INVENTORY_COLLECTIONS)

    print("[INFO] All specified collections processed.")

if __name__ == "__main__":
    main()
