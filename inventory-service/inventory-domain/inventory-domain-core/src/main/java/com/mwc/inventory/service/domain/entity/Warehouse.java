package com.mwc.inventory.service.domain.entity;

import com.mwc.domain.entity.AggregateRoot;
import com.mwc.domain.entity.BaseEntity;
import com.mwc.domain.valueobject.WarehouseId;
import com.mwc.inventory.service.domain.exception.InventoryDomainException;
import com.mwc.inventory.service.domain.valueobject.Location;
import com.mwc.inventory.service.domain.valueobject.WarehouseStatus;

import java.util.List;

public class Warehouse extends BaseEntity<WarehouseId> {

    private String name;
    private Location location;
    private WarehouseStatus status;
    private List<String> failureMessages;

    public static final String FAILURE_MESSAGE_DELIMITER = ",";

    private Warehouse(Builder builder) {
        super.setId(builder.warehouseId);
        this.name = builder.name;
        this.location = builder.location;
        this.status = builder.status;
        this.failureMessages = builder.failureMessages;

    }

    public static Builder builder() {
        return new Builder();
    }

    public void deactivateWarehouse() {
        this.status = WarehouseStatus.DEACTIVE;
    }

    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = newName;
    }

    public void updateLocation(Location newLocation) {
        this.location = newLocation;
    }


    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(failureMessages.stream().filter(message -> !message.isEmpty()).toList());
        }
        if (this.failureMessages == null) {
            this.failureMessages = failureMessages;
        }
    }

    // Getters
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public WarehouseStatus getStatus() { return status; }
    public WarehouseId getWarehouseId() { return getId(); }
    public List<String> getFailureMessages() {
        return failureMessages;
    }

    // Builder class
    public static class Builder {
        private WarehouseId warehouseId;
        private String name;
        private Location location;
        private WarehouseStatus status = WarehouseStatus.ACTIVE;
        private List<String> failureMessages;


        public Builder warehouseId(WarehouseId warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public Builder name(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new InventoryDomainException("Name cannot be null or empty.");
            }
            this.name = name;
            return this;
        }

        public Builder location(Location location) {
            if (location == null) {
                throw new InventoryDomainException("Location cannot be null.");
            }
            this.location = location;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            this.failureMessages = val;
            return this;
        }

        public Builder status(WarehouseStatus status) {
            if (status != null) {
                this.status = status;
            }
            return this;
        }

        public Warehouse build() {
            if (this.warehouseId == null) {
                throw new InventoryDomainException("WarehouseId must be set.");
            }
            if (this.name == null || this.name.trim().isEmpty()) {
                throw new InventoryDomainException("Name must be set.");
            }
            if (this.location == null) {
                throw new InventoryDomainException("Location must be set.");
            }
            return new Warehouse(this);
        }
    }
}