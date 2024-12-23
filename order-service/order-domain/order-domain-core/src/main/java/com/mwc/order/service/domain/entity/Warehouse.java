package com.mwc.order.service.domain.entity;

import com.mwc.domain.entity.BaseEntity;
import com.mwc.domain.valueobject.WarehouseId;

public class Warehouse extends BaseEntity<WarehouseId> {
    private WarehouseId warehouseId;
    private String name;

    public Warehouse(WarehouseId warehouseId) {
        super.setId(warehouseId);
    }

    private Warehouse(Builder builder) {
        super.setId(builder.warehouseId);
        this.name = builder.name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public static final class Builder {
        private WarehouseId warehouseId;
        private String name;

        private Builder() {
        }

        public Builder warehouseId(WarehouseId val) {
            warehouseId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Warehouse build() {
            return new Warehouse(this);
        }
    }

}
