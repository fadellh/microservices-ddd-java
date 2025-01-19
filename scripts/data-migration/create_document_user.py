"""
create_documents_user.py

This script creates the MongoDB collections and indexes for the User Service (READ side),
if the User Service also uses a MongoDB read model.
"""

from pymongo import MongoClient, ASCENDING
from dotenv import load_dotenv
import os

load_dotenv()


USER_MONGO_URI = os.getenv("MONGO_URI")
USER_MONGO_DB_NAME = "user_read_db"

def get_user_mongo_client():
    return MongoClient(USER_MONGO_URI)

def create_user_collections():
    """
    Creates collections such as 'admin_users', 'customer_users', etc. for read model.
    Sets up indexes if needed.
    """
    client = get_user_mongo_client()
    db = client[USER_MONGO_DB_NAME]

    # Example collections
    admins_coll = db["admin_users"]
    admins_coll.create_index([("id", ASCENDING)], name="idx_admin_id")

    customers_coll = db["customer_users"]
    customers_coll.create_index([("id", ASCENDING)], name="idx_customer_id")

    client.close()

def main():
    create_user_collections()
    print("User Service Mongo collections and indexes created.")

if __name__ == "__main__":
    main()
