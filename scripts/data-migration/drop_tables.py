import os
import psycopg2
from dotenv import load_dotenv
import os

load_dotenv()

def get_postgres_connection(config_dict):
    """Establish a connection to the PostgreSQL database."""
    return psycopg2.connect(**config_dict)

def execute_sql_statements(statements, config):
    """Execute SQL statements on the database."""
    conn = get_postgres_connection(config)
    cursor = conn.cursor()
    try:
        for statement in statements:
            cursor.execute(statement)
        conn.commit()
    except Exception as e:
        conn.rollback()
        print(f"Error executing statements: {e}")
    finally:
        cursor.close()
        conn.close()

# PostgreSQL configurations
ORDER_POSTGRES_CONFIG = {
    "dbname": os.getenv("ORDER_DB_NAME"),
    "user": os.getenv("ORDER_DB_USER"),
    "password": os.getenv("ORDER_DB_PASSWORD"),
    "host": os.getenv("ORDER_DB_HOST"),
    "port": int(os.getenv("ORDER_DB_PORT")),
}

INVENTORY_POSTGRES_CONFIG = {
    "dbname": os.getenv("INVENTORY_DB_NAME"),
    "user": os.getenv("INVENTORY_DB_USER"),
    "password": os.getenv("INVENTORY_DB_PASSWORD"),
    "host": os.getenv("INVENTORY_DB_HOST"),
    "port": int(os.getenv("INVENTORY_DB_PORT")),
}

USER_POSTGRES_CONFIG = {
    "dbname": os.getenv("USER_DB_NAME"),
    "user": os.getenv("USER_DB_USER"),
    "password": os.getenv("USER_DB_PASSWORD"),
    "host": os.getenv("USER_DB_HOST"),
    "port": int(os.getenv("USER_DB_PORT")),
}

# Drop statements
order_drop_statements = [
    "DROP TABLE IF EXISTS order_items CASCADE;",
    "DROP TABLE IF EXISTS order_address CASCADE;",
    "DROP TABLE IF EXISTS orders CASCADE;",
    "DROP TABLE IF EXISTS warehouses CASCADE;",
]

inventory_drop_statements = [
    "DROP TABLE IF EXISTS stock_journal CASCADE;",
    "DROP TABLE IF EXISTS inventory_items CASCADE;",
    "DROP TABLE IF EXISTS inventory CASCADE;",
    "DROP TABLE IF EXISTS warehouses CASCADE;",
]

user_drop_statements = [
    "DROP TABLE IF EXISTS admin_users CASCADE;",
    "DROP TABLE IF EXISTS customer_users CASCADE;",
]

def main():
    print("[INFO] Dropping tables for Order Service...")
    execute_sql_statements(order_drop_statements, ORDER_POSTGRES_CONFIG)

    print("[INFO] Dropping tables for Inventory Service...")
    execute_sql_statements(inventory_drop_statements, INVENTORY_POSTGRES_CONFIG)

    print("[INFO] Dropping tables for User Service...")
    execute_sql_statements(user_drop_statements, USER_POSTGRES_CONFIG)

    print("[INFO] All tables dropped successfully.")

if __name__ == "__main__":
    main()
