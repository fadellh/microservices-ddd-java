package com.mwc.order.service.domain.entity;

import com.mwc.domain.entity.AggregateRoot;
import com.mwc.domain.valueobject.AdminId;
import com.mwc.domain.valueobject.AdminRole;

public class Admin extends AggregateRoot<AdminId> {
    private String username;
    private boolean active;
    private AdminRole role;

    private Admin(Builder builder) {
        super.setId(builder.adminId);
        username = builder.username;
        active = builder.active;
        role = builder.role;
    }

    public Admin(AdminId adminId) {
        super.setId(adminId);
    }

    public boolean isActive() {
        return active;
    }

    public static final class Builder {
        private AdminId adminId;
        private String username;
        private boolean active;
        private AdminRole role;

        private Builder() {
        }

        public Builder adminId(AdminId val) {
            adminId = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder active(boolean val) {
            active = val;
            return this;
        }

        public Builder role(AdminRole val) {
            role = val;
            return this;
        }

        public Admin build() {
            return new Admin(this);
        }
    }

    public static Admin.Builder builder() {
        return new Admin.Builder();
    }

}
