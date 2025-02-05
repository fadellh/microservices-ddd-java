# Order Service

This service handles the creation, management, and lifecycle of orders. It coordinates with other services (e.g. Inventory Service, Payment Service, User Service) via commands, events, and RESTful APIs to fulfill all steps needed when placing or managing an order.

---

## Table of Contents

- [Order Service](#order-service)
  - [Table of Contents](#table-of-contents)
  - [Business Logic](#business-logic)
    - [Sagas (Chain Operations)](#sagas-chain-operations)
    - [Order Workflow](#order-workflow)
    - [Order Status](#order-status)
  - [APIs](#apis)
    - [RESTful APIs](#restful-apis)
  - [Commands](#commands)
    - [Outbound Commands](#outbound-commands)
    - [Inbound Commands](#inbound-commands)
  - [Events](#events)
    - [Outbound Events](#outbound-events)
    - [Inbound Events](#inbound-events)
  - [Database](#database)
    - [`orders` Table](#orders-table)
    - [`order_items` Table](#order_items-table)
  - [Saga Flow](#saga-flow)
  - [OrderStatus Enum](#orderstatus-enum)

---

## Business Logic

### Sagas (Chain Operations)

A saga is a sequence of local transactions (commands) that update each service and publish events or perform compensation (rollback) in case of a failure. The following is an example saga for **approving** an order. Each step calls a different service or command to proceed.

| Step No. | Service         | Command               | Compensation (Rollback)     | Order Status Transition |
|----------|-----------------|-----------------------|-----------------------------|-------------------------|
| 1        | Order Service   | *RejectApproval*      | –                           | `APPROVAL_PENDING`      |
| 2        | Inventory Service | **StockDeducted**   | **UndoStockDeducted**       | `APPROVAL_PENDING`      |
| 3        | Inventory Service | **AutoStockTransferReq** | **StockTransferFailed** | `APPROVAL_PENDING`      |
| 4        | Order Service   | **ApprovalOrder**     | –                           | `APPROVED`             |



---

### Order Workflow

Below is a simplified representation of how an order might progress through different states:

1. **AWAITING_PAYMEBT**: A newly created order is awaiting payment.  
2. **REVIEW_PAYMENT** *(optional)*: You might place the order in a temporary review state (e.g. waiting for an off-site payment confirmation).  
3. **APPROVED_PENDING**: The order is ready to be fully approved, pending final checks.  
4. **APPROVED**: The order has met all conditions (e.g. stock availability, user validation, and/or payment).  
5. **CANCEL_PENDING**: The order is in the process of being canceled (waiting for rollbacks in other services).  
6. **CANCELLED**: The order is canceled (e.g. after a rollback or explicit user action).

---

### Order Status

The service (and domain) often uses the following statuses (though these can be adapted as needed):

- **AWAITING_PAYMENT**
- **REVIEW_PAYMENT**
- **APPROVED_PENDING**
- **APPROVED**
- **CANCEL_PENDING**
- **CANCELLED**

---

## APIs

### RESTful APIs

> **Base URL Example**: `https://api.example.com/v1`

| **Method** | **Endpoint**                  | **Description**                                                    |
|------------|-------------------------------|--------------------------------------------------------------------|
| `GET`      | `/orders`                    | Retrieve a list of orders (supports filters such as order number, date range). |
| `GET`      | `/orders/{orderId}`          | Fetch detailed information for a specific order.                   |
| `POST`     | `/orders`                    | Create a new order.                                               |
| `POST`     | `/orders/preview`            | Preview cost or details of an order before creation.               |
| `POST`     | `/orders/payment`            | Upload or create a payment for an order (with proof file).         |
| `POST`     | `/orders/{orderId}/approve`  | Approve an order (after verifying stock, user, payment, etc.).     |

> **Note**: The above endpoints can vary based on your exact business requirements.  
> Since there is no gRPC requirement mentioned, we are skipping gRPC interfaces.

---

## Commands

### Outbound Commands

Outbound commands are sent to other services to trigger a specific action in the context of the saga. For example:

| **Target Service**     | **Command**             | **Usage / Saga**             | **Rollback (if any)**  |
|------------------------|-------------------------|------------------------------|------------------------|
| **Inventory Service**  | `StockDeducted`         | *Order Approval Saga*        | `UndoStockDeducted`    |
| **Inventory Service**  | `AutoStockTransferReq`  | *Order Approval Saga*        | `StockTransferFailed`  |
| **Order Service**      | `ApprovalOrder`         | *Order Approval Saga*        | –                      |
| **Order Service**      | `RejectApproval`        | *Order Approval Saga*        | –                      |


### Inbound Commands

Inbound commands are messages that this Order Service listens to (via a message broker or HTTP, etc.) to perform an action. For instance:

| **Command**            | **Description**                                           |
|------------------------|-----------------------------------------------------------|
| `RejectApproval`       | Used to reject an order approval if validation fails.     |
| `ApprovalOrder`        | Used to finalize the order approval if all checks pass.   |
| `CancelOrder`          | Used to cancel an order (initiates the cancel saga).      |
| `RollbackCancelOrder`  | Used to revert a cancel action if needed.                 |

---

## Events

### Outbound Events

These are domain events or integration events published by the Order Service when something occurs that external services may need to know about (e.g., `OrderCreatedEvent`, `OrderApprovedEvent`, etc.):

| **Event**              | **Description**                                            | **Published to**     |
|------------------------|------------------------------------------------------------|----------------------|
| `OrderCreatedEvent`    | Emitted when a new order is created.                      | Could be Inventory, Payment, etc. |
| `OrderApprovedEvent`   | Emitted when an order is approved.                        | (Same or other services) |
| `OrderCancelledEvent`  | Emitted when an order is canceled.                        | (Same or other services) |

### Inbound Events

These are events that the Order Service listens to from other services:

| **Source Service**       | **Event**               | **Description**                                       |
|--------------------------|-------------------------|-------------------------------------------------------|
| **Inventory Service**    | `StockTransferredEvent` | Notifies that stock transfer has been completed.      |

---

## Database

The Order Service will store and track order details in a local database. The schema might look like this (simplified):

### `orders` Table

| **Column**        | **Type**       | **Description**                                           |
|-------------------|----------------|-----------------------------------------------------------|
| `id`              | `UUID` / `BIGINT`  | Primary key.                                              |
| `customer_id`     | `UUID` / `BIGINT`  | References the user/customer who placed the order.        |
| `order_status`    | `VARCHAR`      | Current order state (`APPROVAL_PENDING`, `APPROVED`, etc.).|
| `total_amount`    | `NUMERIC`      | Computed total amount of the order.                        |
| `failure_messages`| `TEXT`         | Any error messages if the order failed or was cancelled.   |
| `created_at`      | `TIMESTAMP`    | Creation time.                                             |
| `updated_at`      | `TIMESTAMP`    | Last update time.                                          |

### `order_items` Table

| **Column**        | **Type**     | **Description**                                  |
|-------------------|-------------|--------------------------------------------------|
| `id`              | `BIGINT`    | Primary key.                                     |
| `order_id`        | `UUID`      | Foreign key to `orders.id`.                      |
| `product_id`      | `UUID`      | Product identifier.                              |
| `product_name`    | `VARCHAR`   | Name of the product at the time of ordering.     |
| `price`           | `NUMERIC`   | Price of the product at the time of ordering.    |
| `quantity`        | `INT`       | Quantity of this product in the order.           |
| `sub_total`       | `NUMERIC`   | `quantity * price`.                              |


---


## Saga Flow

This is a simplified matrix describing the “approval” saga flow:

| # | Service            | Command               | Rollback Command       | Order Status    |
|---|--------------------|-----------------------|------------------------|-----------------|
| 1 | **Order Service**  | `RejectApproval`      | –                      | `APPROVAL_PENDING` |
| 2 | **Inventory Service** | `StockDeducted`    | `UndoStockDeducted`    | `APPROVAL_PENDING` |
| 3 | **Inventory Service** | `AutoStockTransferReq` | `StockTransferFailed`| `APPROVAL_PENDING` |
| 4 | **Order Service**  | `ApprovalOrder`       | –                      | `APPROVED`      |

> You can extend this table for cancellation, revision, or other complicated flows.

---

## OrderStatus Enum

Below is an example Java enum (as a reference) to illustrate the different possible states in an Order’s lifecycle:

```java
package com.mwc.domain.valueobject;

public enum OrderStatus {
    AWAITING_PAYMENT,
    REVIEW_PAYMENT,
    APPROVED_PENDING,
    APPROVED,
    CANCEL_PENDING,
    CANCELLED
}
```
