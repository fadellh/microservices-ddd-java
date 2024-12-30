package com.mwc.inventory.service.domain.entity;

import com.mwc.domain.entity.AggregateRoot;
import com.mwc.domain.valueobject.InventoryId;
import com.mwc.domain.valueobject.OrderId;
import com.mwc.domain.valueobject.ProductId;
import com.mwc.domain.valueobject.WarehouseId;
import com.mwc.inventory.service.domain.exception.*;
import com.mwc.inventory.service.domain.valueobject.Quantity;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;
import com.mwc.inventory.service.domain.valueobject.StockJournalType;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends AggregateRoot<InventoryId> {
    private ProductId productId;
    private WarehouseId warehouseId;
    private Quantity quantity;

    private List<String> failureMessages;
    public static final String FAILURE_MESSAGE_DELIMITER = ",";

    private List<StockJournal> journals;

    private Inventory(Builder builder) {
        super.setId(builder.inventoryId);
        productId = builder.productId;
        warehouseId = builder.warehouseId;
        journals = new ArrayList<>();
        quantity = builder.quantity;
        failureMessages = builder.failureMessages;
    }

    public void addStock(Quantity toAdd, StockJournalReason reason) {
        if (toAdd.getValue() <= 0) {
            throw new InventoryDomainException("Cannot add non-positive quantity");
        }
        this.quantity = this.quantity.add(toAdd);

        StockJournal journal = StockJournal.create(getId(), toAdd, reason, StockJournalType.INCREASE);
        journals.add(journal);
    }

    // check if sourceInventory has enough quantity to transfer
    public void checkIfEnoughQuantity(Quantity toTransfer) {
        if (this.quantity.isLessThan(toTransfer)) {
            throw new NotEnoughInventoryException("Insufficient stock to transfer");
        }
    }

    public void reduceStock(Quantity toReduce, StockJournalReason reason) {
        if (toReduce.getValue() <= 0) {
            throw new InventoryDomainException("Cannot reduce non-positive quantity");
        }
        if (this.quantity.isLessThan(toReduce)) {
            throw new InventoryDomainException("Insufficient stock to reduce");
        }
        this.quantity = this.quantity.subtract(toReduce);

        StockJournal journal = StockJournal.create(getId(), toReduce, reason, StockJournalType.DECREASE);
        journals.add(journal);
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

    public Quantity getQuantity() {
        return quantity;
    }

    public List<StockJournal> getJournals() {
        return journals;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }


    public static class Builder {
        private InventoryId inventoryId;
        private ProductId productId;
        private WarehouseId warehouseId;
        private Quantity quantity;
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

        public Builder quantity(Quantity quantity) {
            this.quantity = quantity;
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