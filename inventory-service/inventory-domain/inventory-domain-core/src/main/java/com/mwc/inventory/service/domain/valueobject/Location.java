package com.mwc.inventory.service.domain.valueobject;

public class Location {

    private float latitude;
    private float longitude;
    private String address;
    private String city;
    private String province;
    private String district;
    private String postalCode;

    public Location(Builder builder) {
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.address = builder.address;
        this.city = builder.city;
        this.province = builder.province;
        this.district = builder.district;
        this.postalCode = builder.postalCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public float getLatitude() { return latitude; }
    public float getLongitude() { return longitude; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getProvince() { return province; }
    public String getDistrict() { return district; }
    public String getPostalCode() { return postalCode; }

    // Builder class
    public static class Builder {
        private float latitude;
        private float longitude;
        private String address;
        private String city;
        private String province;
        private String district;
        private String postalCode;

        public Builder latitude(float latitude) {
            validateLatitude(latitude);
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(float longitude) {
            validateLongitude(longitude);
            this.longitude = longitude;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder province(String province) {
            this.province = province;
            return this;
        }

        public Builder district(String district) {
            this.district = district;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        private void validateLatitude(float latitude) {
            if (latitude < -90 || latitude > 90) {
                throw new IllegalArgumentException("Latitude must be between -90 and 90.");
            }
        }

        private void validateLongitude(float longitude) {
            if (longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException("Longitude must be between -180 and 180.");
            }
        }

        public Location build() {
            return new Location(this);
        }
    }
}