package com.mwc.order.service.domain.entity;

import com.mwc.domain.entity.BaseEntity;
import com.mwc.domain.valueobject.WarehouseId;

public class Warehouse extends BaseEntity<WarehouseId> {
    private WarehouseId warehouseId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double distance;

    public Warehouse(WarehouseId warehouseId) {
        super.setId(warehouseId);
    }

    private Warehouse(Builder builder) {
        super.setId(builder.warehouseId);
        this.name = builder.name;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.distance = builder.distance;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getDistance() {
        return distance;
    }


    public static final class Builder {
        private WarehouseId warehouseId;
        private String name;
        private Double latitude;
        private Double longitude;
        private Double distance;

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

        public Builder latitude(Double val) {
            latitude = val;
            return this;
        }

        public Builder longitude(Double val) {
            longitude = val;
            return this;
        }

        public Builder distance(Double val) {
            distance = val;
            return this;
        }

        public Warehouse build() {
            return new Warehouse(this);
        }
    }

}
