# Inventory Service

This service is responsible for managing product inventory levels, tracking stock across one or more locations/warehouses, and handling stock transfers. It interacts with other services (like Order Service) through commands and events to ensure the system maintains correct and up-to-date inventory data.

---

## Table of Contents

- [Inventory Service](#inventory-service)
  - [Table of Contents](#table-of-contents)
  - [Business Logic](#business-logic)
    - [Sagas (Chain Operations)](#sagas-chain-operations)
    - [Inventory Workflow](#inventory-workflow)
  - [APIs](#apis)
    - [RESTful APIs](#restful-apis)
  - [Commands](#commands)
    - [Outbound Commands](#outbound-commands)
    - [Inbound Commands](#inbound-commands)
  - [Events](#events)
    - [Outbound Events](#outbound-events)
    - [Inbound Events](#inbound-events)
  - [Database](#database)
    - [`inventory` Table](#inventory-table)
    - [`stock_transfer` Table (Optional)](#stock_transfer-table-optional)
  - [Saga Flow](#saga-flow)

---

## Business Logic

### Sagas (Chain Operations)

In a microservices environment, the Inventory Service may participate in sagas (i.e., coordinated transactions) to ensure system consistency. For example:

1. **Deduct Stock** – When an order is placed, the Inventory Service receives a command to deduct stock. If something fails later in the workflow, a compensation command (e.g., `UndoStockDeducted`) is sent to restore stock levels.
2. **Auto Stock Transfer** – If local stock is insufficient, the service may initiate or receive a command to transfer stock from another warehouse or location (`AutoStockTransferReq`). On failure, it may roll back via `StockTransferFailed`.


### Inventory Workflow

A simplified flow for how inventory might be updated in response to an order creation:

1. **Stock Check**: Verify if there is enough stock for the requested products.  
2. **Deduct Stock**: Reduce the quantity from the specified warehouse/location.  
3. **(Optional) Stock Transfer**: If local stock is insufficient, automatically transfer stock from another location or warehouse.  
4. **Confirm**: Send success/failure notifications to the requesting service (e.g., the Order Service).  

---

## APIs

### RESTful APIs

> **Base URL Example**: `https://api.example.com/v1`

| **Method** | **Endpoint**                          | **Description**                                 |
|------------|---------------------------------------|-------------------------------------------------|
| `GET`      | `/inventory/products`                 | Retrieve a list of all products and stock info. |
| `POST`     | `/inventory/{inventoryId}/transfer`   | Transfer inventory from one location to another.|


---

## Commands

### Outbound Commands

The Inventory Service might send commands to other services (or even to itself) in response to local logic or saga steps. For instance:

| **Target Service**     | **Command**                | **Usage / Saga**                 | **Rollback**            |
|------------------------|----------------------------|----------------------------------|-------------------------|
| **Order Service**      | *(If needed)*              | E.g., notify order status change | *N/A or define if needed* |
| **Inventory Service**  | `AutoStockTransferReq`     | *Triggered internally to handle short supply in one warehouse, requesting a transfer from another.* | `StockTransferFailed` |


### Inbound Commands

Inbound commands are sent to the Inventory Service from other microservices (or orchestrators) to modify or query stock levels. Common examples:

| **Command**             | **Description**                                            |
|-------------------------|------------------------------------------------------------|
| `StockDeducted`         | Deduct stock for products in an order.                     |
| `UndoStockDeducted`     | Restore (compensate) stock if the order fails or is rolled back. |
| `AutoStockTransferReq`  | Attempt to transfer stock from a different warehouse/location.    |
| `StockTransferFailed`   | Notify that a stock transfer failed (and thus revert?).    |

---

## Events

### Outbound Events

Events published by the Inventory Service to notify other services of state changes:

| **Event**               | **Description**                                           | **Published To**        |
|-------------------------|-----------------------------------------------------------|-------------------------|
| `StockDeductedEvent`    | Emitted when stock was successfully deducted.            | Order Service, etc.     |
| `StockTransferedEvent`  | Emitted when stock is successfully transferred.          | Other listening services |
| `StockTransferFailedEvent` | Emitted when a transfer attempt fails.               | Other listening services |

### Inbound Events

Events consumed by the Inventory Service that might originate from other services:

| **Source Service**   | **Event**                | **Description**                                                   |
|----------------------|--------------------------|-------------------------------------------------------------------|
| **Order Service**    | `OrderApprovedEvent`      | Possibly triggers a stock deduction flow.                         |

---

## Database

A possible data model for the Inventory Service:

### `inventory` Table

| **Column**    | **Type**       | **Description**                                   |
|---------------|----------------|---------------------------------------------------|
| `id`          | `BIGINT` / `UUID` | Primary key.                                     |
| `product_id`  | `BIGINT` / `UUID` | References the product.                          |
| `warehouse_id`| `BIGINT` / `UUID` | References a warehouse/location (if applicable). |
| `quantity`    | `INT`          | Current stock level.                              |
| `updated_at`  | `TIMESTAMP`    | Last updated timestamp.                           |

### `stock_transfer` Table (Optional)

| **Column**      | **Type**       | **Description**                                      |
|-----------------|----------------|------------------------------------------------------|
| `id`            | `UUID`         | Primary key (transfer record).                       |
| `from_location` | `UUID`         | Warehouse/location from which stock is pulled.       |
| `to_location`   | `UUID`         | Warehouse/location receiving the stock.             |
| `product_id`    | `UUID`         | Product involved.                                    |
| `quantity`      | `INT`          | Transfer quantity.                                   |
| `status`        | `VARCHAR`      | Status of the transfer (e.g., `PENDING`, `COMPLETED`, `FAILED`). |
| `created_at`    | `TIMESTAMP`    | Creation date/time.                                  |
| `updated_at`    | `TIMESTAMP`    | Last updated date/time.                              |


---


## Saga Flow

Below is an example saga flow (focusing on inventory-related steps). The Inventory Service receives commands from the Order Service and either completes them or rolls back:

| # | Service            | Command                 | Rollback Command       | Description                          |
|---|--------------------|-------------------------|------------------------|--------------------------------------|
| 1 | **Order Service**  | `StockDeducted`         | `UndoStockDeducted`    | Deduct stock for the order items.    |
| 2 | **Inventory Service** | `AutoStockTransferReq` | `StockTransferFailed` | If local stock is insufficient, attempt transfer from another warehouse. |
| 3 | **Inventory Service** | (Event) `StockTransferedEvent` | – | Publish success event when transfer completes. |
| 4 | **Order Service**  | –                       | –                      | Continues with next step in its saga flow. |
