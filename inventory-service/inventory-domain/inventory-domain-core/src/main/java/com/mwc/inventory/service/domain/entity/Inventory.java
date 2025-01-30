package com.mwc.inventory.service.domain.entity;

import com.mwc.domain.entity.AggregateRoot;
import com.mwc.domain.valueobject.InventoryId;
import com.mwc.domain.valueobject.OrderId;
import com.mwc.domain.valueobject.ProductId;
import com.mwc.domain.valueobject.WarehouseId;
import com.mwc.inventory.service.domain.exception.*;
import com.mwc.inventory.service.domain.valueobject.Quantity;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;
import com.mwc.inventory.service.domain.valueobject.StockJournalStatus;
import com.mwc.inventory.service.domain.valueobject.StockJournalType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Inventory extends AggregateRoot<InventoryId> {
    private ProductId productId;
    private WarehouseId warehouseId;
    private OrderId orderId;
    private UUID inventoryItemId;
    private Quantity quantity;

    private List<String> failureMessages;
    public static final String FAILURE_MESSAGE_DELIMITER = ",";

    private List<StockJournal> journals;
    private boolean needAutoTransfer = false;

    private Inventory(Builder builder) {
        super.setId(builder.inventoryId);
        productId = builder.productId;
        warehouseId = builder.warehouseId;
        orderId = builder.orderId;
        inventoryItemId = builder.inventoryItemId;

        journals = new ArrayList<>();
        quantity = builder.quantity;
        needAutoTransfer = builder.needAutoTransfer;
        failureMessages = new ArrayList<>();
    }

    public void addStock(Quantity toAdd, StockJournalReason reason, StockJournalStatus status) {
        if (toAdd.getValue() <= 0) {
            throw new InventoryDomainException("Cannot add non-positive quantity");
        }
        this.quantity = this.quantity.add(toAdd);

        StockJournal journal = StockJournal.create(getId(), toAdd, reason, StockJournalType.INCREASE, status);
        journals.add(journal);
    }

    // check if sourceInventory has enough quantity to transfer
    public void checkIfEnoughQuantity(Quantity toTransfer) {
        if (this.quantity.isLessThan(toTransfer)) {
            throw new NotEnoughInventoryException("Insufficient stock to transfer. Please use another warehouse");
        }
    }

    public void reduceStock(Quantity toReduce, StockJournalReason reason, StockJournalStatus status) {
        if (toReduce.getValue() <= 0) {
            throw new InventoryDomainException("Cannot reduce non-positive quantity");
        }
        if (this.quantity.isLessThan(toReduce)) {
            throw new InsufficientStock("Insufficient stock to reduce");
        }
        this.quantity = this.quantity.subtract(toReduce);

        StockJournal journal = StockJournal.create(getId(), toReduce, reason, StockJournalType.DECREASE, status);
        journals.add(journal);
    }

    public void order(Quantity quantity) {
        try {
            reduceStock(quantity, StockJournalReason.ORDER, StockJournalStatus.APPROVED);
        } catch (InsufficientStock e) {
            needAutoTransfer = true;
            failureMessages.add("Insufficient stock to order");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public ProductId getProductId() {
        return productId;
    }

    public WarehouseId getWarehouseId() {
        return warehouseId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public UUID getInventoryItemId() {
        return inventoryItemId;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public boolean isNeedAutoTransfer() {
        return needAutoTransfer;
    }

    public List<StockJournal> getJournals() {
        return journals;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }


    public static class Builder {
        private InventoryId inventoryId;
        private ProductId productId;
        private WarehouseId warehouseId;
        private OrderId orderId;
        private UUID inventoryItemId;
        private Quantity quantity;
        private boolean needAutoTransfer;
        private List<String> failureMessages;

        public Builder id(InventoryId val) {
            inventoryId = val;
            return this;
        }

        public Builder productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public Builder warehouseId(WarehouseId warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder inventoryItemId(UUID inventoryItemId) {
            this.inventoryItemId = inventoryItemId;
            return this;
        }

        public Builder quantity(Quantity quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder needAutoTransfer(boolean needAutoTransfer) {
            this.needAutoTransfer = needAutoTransfer;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Inventory build() {
            return new Inventory(this);
        }
    }

}