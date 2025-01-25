"""
revised_locustfile.py

Demonstrates end-to-end flow for an e-commerce CQRS app (Order & Inventory).
Explicitly calls out concurrency best practices and performance validations.
"""

import uuid
import random
from locust import HttpUser, TaskSet, task, between

# Example: If you have a stable UUID generator from your data_injection, import or replicate it:
# from your_data_injection_script import generate_static_uuid
def generate_static_uuid(model_name: str, index: int) -> str:
    # Stub or real logic. For demonstration, just return random.
    return str(uuid.uuid4())

WAREHOUSE_IDS = [generate_static_uuid("Warehouse", i) for i in range(3)]
CUSTOMER_IDS = [generate_static_uuid("CustomerUser", i) for i in range(5)]
ADMIN_IDS = [generate_static_uuid("AdminUser", i) for i in range(2)]

# ---------------------------------------------------------------------------
# E2E Order Flow
# ---------------------------------------------------------------------------
class E2EOrderFlow(TaskSet):
    def on_start(self):
        self.last_created_order_id = None
    
    @task
    def preview_order(self):
        customer_id = random.choice(CUSTOMER_IDS)
        payload = {
            "customerId": customer_id,
            "items": [
                {
                    "productId": str(uuid.uuid4()),
                    "quantity": random.randint(1, 3)
                }
            ],
            "address": {
                "street": "Main St",
                "city": "Sample City",
                "postalCode": "10000"
            }
        }
        with self.client.post("/v1/orders/preview", json=payload, name="Order: Preview", catch_response=True) as r:
            if r.status_code != 200:
                r.failure(f"PreviewOrder failed: {r.status_code} {r.text}")
    
    @task
    def create_order(self):
        customer_id = random.choice(CUSTOMER_IDS)
        warehouse_id = random.choice(WAREHOUSE_IDS)
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
                "postalCode": "10000"
            }
        }
        with self.client.post("/v1/orders", json=payload, name="Order: Create", catch_response=True) as r:
            if r.status_code == 200:
                try:
                    data = r.json()
                    self.last_created_order_id = data.get("orderId")
                except Exception as e:
                    r.failure(f"Error parsing createOrder JSON: {e}")
            else:
                r.failure(f"CreateOrder failed: {r.status_code} {r.text}")

    @task
    def get_order_detail(self):
        if not self.last_created_order_id:
            return  # Skip if no created order
        with self.client.get(f"/v1/orders/{self.last_created_order_id}", name="Order: Get Detail", catch_response=True) as r:
            if r.status_code not in [200, 404]:
                r.failure(f"GetOrderDetail failed: {r.status_code} {r.text}")

    @task
    def approve_order(self):
        if not self.last_created_order_id:
            return
        admin_id = random.choice(ADMIN_IDS)
        headers = {
            "X-User-Id": admin_id
        }
        with self.client.post(f"/v1/orders/{self.last_created_order_id}/approve", headers=headers, name="Order: Approve", catch_response=True) as r:
            if r.status_code != 200:
                r.failure(f"ApproveOrder failed: {r.status_code} {r.text}")

    @task
    def get_orders(self):
        customer_id = random.choice(CUSTOMER_IDS)
        headers = {
            "X-User-Id": customer_id
        }
        with self.client.get("/v1/orders", headers=headers, name="Order: Get List", catch_response=True) as r:
            if r.status_code != 200:
                r.failure(f"GetOrders failed: {r.status_code} {r.text}")

    @task
    def stop_flow(self):
        self.interrupt()

# ---------------------------------------------------------------------------
# E2E Inventory Flow
# ---------------------------------------------------------------------------
class E2EInventoryFlow(TaskSet):
    @task
    def get_products(self):
        with self.client.get("/v1/inventory/products", name="Inventory: Get Products", catch_response=True) as r:
            if r.status_code != 200:
                r.failure(f"GetProducts failed: {r.status_code} {r.text}")

    @task
    def transfer_inventory(self):
        inv_id = generate_static_uuid("Inventory", random.randint(0,5))
        payload = {
            "fromWarehouseId": generate_static_uuid("Warehouse", random.randint(0,3)),
            "toWarehouseId": generate_static_uuid("Warehouse", random.randint(0,3)),
            "quantity": random.randint(1, 10)
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
    Simulates a user that can run either E2EOrderFlow or E2EInventoryFlow.
    """
    tasks = {
        E2EOrderFlow: 5,
        E2EInventoryFlow: 3
    }
    wait_time = between(1, 3)

    def on_start(self):
        # You can do login/auth here if needed
        pass

    def on_stop(self):
        pass
