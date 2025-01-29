# **Data Migration Instructions**

This project contains scripts for migrating data across services and setting up necessary databases and collections.

## **Prerequisites**

1. **Python**: Ensure Python 3.8 or later is installed.
2. **Dependencies**: Install the required Python libraries.
3. **Databases**:
   - **PostgreSQL**: Ensure PostgreSQL is running locally or remotely.
   - **MongoDB**: Ensure MongoDB is available and accessible.
4. **Environment Variables**: Set up necessary credentials in a `.env` file.

## **Setup Instructions**

### 1. **Install Dependencies**
Use `pip` to install the required libraries:
```bash
pip install -r requirements.txt
```

### 2. **Set Up Environment Variables**
- Create a `.env` file in the root directory.
- Add the necessary configurations:
  ```plaintext
    # PostgreSQL (Write)
    USER_DB_NAME=postgres
    USER_DB_USER=<your_username>
    USER_DB_PASSWORD=<your_password>
    USER_DB_HOST=localhost
    USER_DB_PORT=5432

    ORDER_DB_NAME=postgres
    ORDER_DB_USER=<your_username>
    ORDER_DB_PASSWORD=<your_password>
    ORDER_DB_HOST=localhost
    ORDER_DB_PORT=5432

    INVENTORY_DB_NAME=postgres
    INVENTORY_DB_USER=<your_username>
    INVENTORY_DB_PASSWORD=<your_password>
    INVENTORY_DB_HOST=localhost
    INVENTORY_DB_PORT=5435

    # MongoDB (Read)
    MONGO_URI=mongodb+srv://<username>:<password>@cluster.mongodb.net/?retryWrites=true&w=majority
  ```

### 3. **Run Data Migration**

Run the main data migration script to set up schemas and populate data into databases:
```bash
python3 data-migration.py
```

Drop collection
```bash
python3 drop_collection.py
```

Drop table
```bash
python3 drop_tables.py
```


## **Troubleshooting**

- **Database Connection Issues**: Ensure PostgreSQL and MongoDB are running and accessible.
- **Missing Dependencies**: Run `pip install -r requirements.txt`.
- **Environment Variables**: Ensure all required variables are set correctly in the `.env` file.

---
