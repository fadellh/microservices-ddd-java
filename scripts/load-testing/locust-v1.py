import uuid
import hashlib
import random
from io import BytesIO

from locust import HttpUser, TaskSet, task, between
from dataclasses import dataclass
from typing import List

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
def generate_static_uuid(model_name: str, unique_id: int) -> str:
    """
    Generate a stable "UUID" from model name + unique_id for repeatable tests.
    """
    unique_string = f"{model_name}-{unique_id}"
    hashed = hashlib.md5(unique_string.encode()).hexdigest()
    return str(uuid.UUID(hashed))

WAREHOUSE_IDS = [generate_static_uuid("Warehouse", i) for i in range(100)]
CUSTOMER_IDS  = [generate_static_uuid("CustomerUser", i) for i in range(50)]
ADMIN_IDS     = [generate_static_uuid("AdminUser", i) for i in range(10)]
CATALOG_IDS   = [generate_static_uuid("CatalogProduct", i) for i in range(100)]
INVENTORY_IDS   = [generate_static_uuid("InventoryItem", i) for i in range(100)]
ORDER_IDS   = [generate_static_uuid("Order", i) for i in range(100)]

@dataclass
class CreateOrder:
    customer_id: str
    warehouse_id: str
    order_id: str
    order_status: str = "AWAITING_PAYMENT"


# For demonstration, keep track of last created order in tasks:
LAST_CREATED_ORDER_IDS : List[CreateOrder] = []



def generate_random_coordinates():
    latitude = round(random.uniform(-11.0, 6.0), 6)  # Latitude range for Indonesia
    longitude = round(random.uniform(95.0, 141.0), 6)  # Longitude range for Indonesia
    return latitude, longitude
# ---------------------------------------------------------------------------
# Customer Flow Tasks
# ---------------------------------------------------------------------------
class CustomerFlow(TaskSet):
    """
    Simulates customer-facing interactions:
      1. View Catalog
      2. Preview Order (Order Summary / Shipping Cost)
      3. Create Order
      4. Submit Payment
      5. View/Search Order List
      6. View Order Detail
    """

    @task
    def get_catalog(self):
        """
        Catalog Page: GET /v1/inventory/products
        """
        with self.client.get(
            "/v1/inventory/products",
            name="Catalog: Get Product List",
            catch_response=True
        ) as r:
            if r.status_code != 200:
                r.failure(f"Failed to get catalog: {r.status_code} {r.text}")

    @task
    def preview_order(self):
        """
        Order Summary & Shipping Cost: /v1/orders/preview
        """
        customer_id = random.choice(CUSTOMER_IDS)
        product_id = random.choice(CATALOG_IDS)
        latitude, longitude = generate_random_coordinates()

        payload = {
            "customerId": customer_id,
            "items": [
                {
                    "productId": product_id,
                    "quantity": random.randint(1, 3)
                }
            ],
            "address": {
                "street": "Main St",
                "city": "Sample City",
                "postalCode": "10000",
                "latitude": latitude,
                "longitude": longitude
            }
        }
        with self.client.post(
            "/v1/orders/preview",
            json=payload,
            name="Order: Preview",
            catch_response=True
        ) as r:
            if r.status_code != 200:
                r.failure(f"Order preview failed: {r.status_code} {r.text}")

    @task
    def create_order(self):
        """
        Create Order: POST /v1/orders
        """
        global LAST_CREATED_ORDER_IDS

        customer_id  = random.choice(CUSTOMER_IDS)
        warehouse_id = random.choice(WAREHOUSE_IDS)
        latitude, longitude = generate_random_coordinates()

        payload = {
            "customerId": customer_id,
            "warehouseId": warehouse_id,
            "items": [
                {
                    "productId": str(uuid.uuid4()),
                    "quantity": random.randint(1, 2)
                },
                {
                    "productId": str(uuid.uuid4()),
                    "quantity": random.randint(1, 2)
                }
            ],
            "address": {
                "street": "Main St",
                "city": "Sample City",
                "postalCode": "10000",
                "latitude": latitude,
                "longitude": longitude
            }
        }
        with self.client.post(
            "/v1/orders",
            json=payload,
            name="Order: Create",
            catch_response=True
        ) as r:
            if r.status_code == 200:
                try:
                    data = r.json()
                    create_order = CreateOrder(
                        customer_id=customer_id,
                        warehouse_id=warehouse_id,
                        order_id=data.get("orderId")
                    )
                    LAST_CREATED_ORDER_IDS.append(create_order)
                except Exception as e:
                    r.failure(f"Error parsing createOrder JSON: {e}")
            else:
                r.failure(f"CreateOrder failed: {r.status_code} {r.text}")

    @task
    def submit_payment(self):
        """
        Submit Payment (multipart file upload): POST /v1/orders/payment
        Expecting: orderId (UUID) & paymentProofFile (MultipartFile)
        """
        # If we have not created an order yet, skip
        if not LAST_CREATED_ORDER_IDS:
            return
        
        order = random.choice(LAST_CREATED_ORDER_IDS)

        if order.order_status != "AWAITING_PAYMENT":
            return

        # Prepare a small in-memory "fake" file
        fake_file_content = b"\xFF\xD8\xFF\xE0\x00\x10JFIF\x00\x01\x01\x01\x00\x60\x00\x60\x00\x00\xFF"  # Header JPEG
        fake_file = BytesIO(fake_file_content)
        files = {
            "paymentProofFile": (
                "test_payment.jpg",
                BytesIO(fake_file),
                "image/jpeg"
            )
        }
        data = {
            "orderId": str(order.order_id)
        }

        with self.client.post(
            "/v1/orders/payment",
            name="Order: Create Payment",
            files=files,
            data=data,
            catch_response=True
        ) as r:
            if r.status_code != 200:
                r.failure(f"CreatePayment failed: {r.status_code} {r.text}")
            else:
                # Update the order status
                order.order_status = "REVIEW_PAYMENT"

    @task
    def get_order_list(self):
        """
        View/Search Order List: GET /v1/orders
        - You can add optional query params for order_number or date range
        """
        customer_id = random.choice(CUSTOMER_IDS)
        if random.choice([True, False]) and not LAST_CREATED_ORDER_IDS:
            order = random.choice(LAST_CREATED_ORDER_IDS)
            order_id = order.order_id
            customer_id = order.customer_id

        headers = {
            "X-User-Id": customer_id
        }
        # Optionally pass random query params (50% chance?)
        params = {}
        if order_id:
            params["order_number"] = order_id
        # elif random.choice([True, False]):
        #     # Just a random example:
        #     params["order_start_date"] = "2023-01-01"
        #     params["order_end_date"]   = "2023-12-31"

        with self.client.get(
            "/v1/orders",
            headers=headers,
            name="Order: Get List (Customer)",
            params=params,
            catch_response=True
        ) as r:
            if r.status_code != 200:
                r.failure(f"GetOrders (Customer) failed: {r.status_code} {r.text}")

    @task
    def view_order_detail(self):
        """
        View Order Detail: GET /v1/orders/{orderId}
        """
        order_id = random.choice(ORDER_IDS)

        # If we have not created an order yet, skip
        if random.choice([True, False]) and not LAST_CREATED_ORDER_IDS:
            order = random.choice(LAST_CREATED_ORDER_IDS)
            order_id = order.order_id

        with self.client.get(
            f"/v1/orders/{order_id}",
            name="Order: Get Detail (Customer)",
            catch_response=True
        ) as r:
            if r.status_code not in [200, 404]:
                r.failure(f"GetOrderDetail failed: {r.status_code} {r.text}")

    @task
    def stop_flow(self):
        """
        Force this TaskSet to end so we can jump to another flow (if configured).
        """
        self.interrupt()

# ---------------------------------------------------------------------------
# Admin Flow Tasks
# ---------------------------------------------------------------------------
class AdminFlow(TaskSet):
    """
    Simulates admin-facing interactions:
      1. View Orders (Dashboard)
      2. Approve Order
      3. View Inventory Stock
      4. Transfer Inventory Stock
    """
    @task
    def admin_get_orders(self):
        """
        Admin Dashboard (Order List): GET /v1/orders
        Using an Admin ID in X-User-Id
        """
        customer_id = random.choice(CUSTOMER_IDS)
        headers  = {
            "X-User-Id": customer_id
        }
        with self.client.get(
            "/v1/orders",
            headers=headers,
            name="Order: Get List (Admin)",
            catch_response=True
        ) as r:
            if r.status_code != 200:
                r.failure(f"Admin GetOrders failed: {r.status_code} {r.text}")

    @task
    def admin_approve_order(self):
        """
        Admin Approve an existing Order: POST /v1/orders/{orderId}/approve
        """
        if not LAST_CREATED_ORDER_IDS:
            return
        
        order = random.choice(LAST_CREATED_ORDER_IDS)
        if order.order_status != "REVIEW_PAYMENT":
            return

        admin_id = random.choice(ADMIN_IDS)
        headers = {
            "X-User-Id": admin_id
        }
        with self.client.post(
            f"/v1/orders/{order.order_id}/approve",
            headers=headers,
            name="Order: Approve",
            catch_response=True
        ) as r:
            if r.status_code != 200:
                r.failure(f"ApproveOrder failed: {r.status_code} {r.text}")

    @task
    def get_inventory(self):
        """
        Admin: View Inventory Stock
        GET /v1/inventory/products
        """
        with self.client.get(
            "/v1/inventory/products",
            name="Inventory: Get Products (Admin)",
            catch_response=True
        ) as r:
            if r.status_code != 200:
                r.failure(f"GetProducts failed: {r.status_code} {r.text}")

    @task
    def transfer_inventory(self):
        """
        Admin Transfer Stock:
        POST /v1/inventory/{inventoryId}/transfer
        """
        from_warehouse = random.choice(WAREHOUSE_IDS)
        to_warehouse   = random.choice(WAREHOUSE_IDS)
        inv_id = random.choice(INVENTORY_IDS)

        payload = {
            "fromWarehouseId": from_warehouse,
            "toWarehouseId":   to_warehouse,
            "quantity":        random.randint(1, 100)
        }
        with self.client.post(
            f"/v1/inventory/{inv_id}/transfer",
            json=payload,
            name="Inventory: Transfer",
            catch_response=True
        ) as r:
            if r.status_code != 200:
                r.failure(f"TransferInventory failed: {r.status_code} {r.text}")

    @task
    def stop_flow(self):
        self.interrupt()

# ---------------------------------------------------------------------------
# Main User
# ---------------------------------------------------------------------------
class EcommerceUser(HttpUser):
    """
    Simulates a user that can be either a Customer or an Admin, 
    with a given weight distribution.
    """
    # These "tasks" define which TaskSet classes to run and how often.
    # e.g. 8 customers for every 2 admins in concurrency.
    tasks = {
        CustomerFlow: 8,
        AdminFlow: 2
    }

    wait_time = between(1, 3)

    def on_start(self):
        # Do something on user start if needed (e.g., login).
        pass

    def on_stop(self):
        # Cleanup if needed.
        pass
