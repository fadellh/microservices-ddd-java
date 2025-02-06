# Load Testing with Locust

This folder contains scripts and configurations for performing load/stress tests on the **microservices-java** application using [Locust](https://locust.io/). Below is an overview of the testing approach, setup instructions, and a brief explanation of the provided scripts and test results.

---

## Table of Contents

- [Load Testing with Locust](#load-testing-with-locust)
  - [Table of Contents](#table-of-contents)
  - [Overview](#overview)
  - [Prerequisites](#prerequisites)
  - [Test Setup and Execution](#test-setup-and-execution)
  - [Locust Script Explanation](#locust-script-explanation)
    - [UUID Generation \& Static IDs](#uuid-generation--static-ids)
    - [Customer Flow](#customer-flow)
    - [Admin Flow](#admin-flow)
    - [Main User (Traffic Distribution)](#main-user-traffic-distribution)
  - [Viewing Results](#viewing-results)
  - [Sample Results](#sample-results)

---

## Overview

We use **Locust** to simulate user behavior and measure the performance and scalability of various endpoints in our microservices (e.g., **Order**, **Inventory**, **User**, etc.). This setup allows us to:

- Generate realistic traffic to test system throughput, latency, and error rates.
- Execute both **customer-facing** and **admin-facing** requests in configurable ratios.
- Reproduce consistent load scenarios using static UUIDs for predictable behavior.

---

## Prerequisites

1. **Python 3** (Locust is a Python package).
2. **Locust** installed:  
   ```bash
   pip install locust
   ```
3. **Running services**: Ensure that the target microservices are up and accessible (e.g., on your local machine, Kubernetes, or another environment).
4. **Network/Firewall**: Make sure your Locust client can reach the microservices’ endpoints.

---

## Test Setup and Execution

1. **Start Services**  
   Verify that all microservices are running (locally or on a deployment environment).
2. **Adjust Locust Configuration**  
   - By default, Locust will use the file `locust-v1.py`.
   - If you have multiple files, specify which one to run using `-f <filename>`.
3. **Run Locust**  
   ```bash
   locust -f locust-v1.py
   ```
4. **Access Locust Web Interface**  
   - Go to [http://localhost:8089](http://localhost:8089) (or wherever Locust is bound).
   - Configure the **Number of users (concurrency)** and **Spawn rate**.
   - Optionally enter a “Host” if Locust does not auto-detect your target host.  
     (e.g., `http://localhost:8080` or `https://my-service.example.com`)

Locust will start generating load, and you can watch the dashboard in real time.

---

## Locust Script Explanation

The primary script is **`locust-v1.py`**, which contains:

### UUID Generation & Static IDs

- **Function**: `generate_static_uuid(model_name: str, unique_id: int)`
- **Purpose**: Create stable UUIDs for consistent referencing of the same customers, products, orders, etc. across test runs.
- **Reasoning**: This approach ensures that your test environment can reproduce similar scenarios on repeated runs (e.g., referencing the same products or warehouses).

Example usage:
```python
WAREHOUSE_IDS = [generate_static_uuid("Warehouse", i) for i in range(100)]
CUSTOMER_IDS  = [generate_static_uuid("CustomerUser", i) for i in range(50)]
```

### Customer Flow

Defined in the **`CustomerFlow(TaskSet)`** class. Represents an 80% share of traffic (by default), covering:

1. **Get Catalog** (`GET /v1/inventory/products`)  
2. **Preview Order** (`POST /v1/orders/preview`)  
3. **Create Order** (`POST /v1/orders`)  
4. **Submit Payment** (`POST /v1/orders/payment`)  
5. **Get Order List** (`GET /v1/orders`)  
6. **View Order Detail** (`GET /v1/orders/{orderId}`)

Each task has a “weight” that determines how frequently it is chosen within the **CustomerFlow**. For instance, “Get Catalog” is performed more frequently than “Submit Payment.”

### Admin Flow

Defined in the **`AdminFlow(TaskSet)`** class. Represents a 20% share of traffic, covering:

1. **Get Inventory** (`GET /v1/inventory/products`)  
2. **Admin Get Orders** (`GET /v1/orders`)  
3. **Admin Approve Order** (`POST /v1/orders/{orderId}/approve`)  
4. **Transfer Inventory** (`POST /v1/inventory/{inventoryId}/transfer`)

Each task has an equal weight within admin tasks, so they’re performed about equally.

### Main User (Traffic Distribution)

The **`EcommerceUser(HttpUser)`** class is the top-level user definition which sets:

- `tasks = { CustomerFlow: 80, AdminFlow: 20 }`  
  This means **80%** of load is from **CustomerFlow** and **20%** from **AdminFlow**.
- `wait_time = between(1, 3)`  
  A random pause between requests (1-3 seconds) for each virtual user.

---

## Viewing Results

While the test is running, Locust provides a real-time dashboard at [http://localhost:8089](http://localhost:8089):

1. **Statistics**: Summaries of requests, average response time, number of failures, etc.
2. **Charts**: Real-time charts for requests per second and response times.
3. **Stop Test**: You can stop or reset the load test at any time.

You can also export metrics to HTML by using Locust’s built-in capabilities or by capturing test artifacts programmatically.

---

## Sample Results

In the `result/` directory, you’ll find example test runs in HTML format:

- **[Baseline Testing (50 CCU)](/docs/load-test-result/baseline-testing-50-ccu.html)**  
- **[Performance Testing (300 CCU)](/docs/load-test-result/perfomace-ccu-300.html)**  
- **[Performance Testing (600 CCU)](/docs/load-test-result/perfomance-600-ccu.html)**  

Open these files in your web browser to review test metrics such as:

- Requests/s (TPS)
- Avg response time
- P95, P99 latencies
- Error rates

---


**Happy Testing!**  
If you have questions or encounter issues, please open a GitHub issue or contact the maintainers.