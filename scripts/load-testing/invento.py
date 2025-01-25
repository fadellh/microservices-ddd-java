from locust import HttpUser, TaskSet, task, between
import uuid
import json

class InventoryTest(TaskSet):

    @task(1)
    def get_products(self):
        headers = {
            "Accept": "application/vnd.api.v1+json"
        }
        response = self.client.get("/v1/inventory/products", headers=headers)
        print("Get Products Response:", response.text)

class InventoryUser(HttpUser):
    tasks = [InventoryTest]
    wait_time = between(1, 3)
