package com.mwc.order.service.dataaccess.admin.mapper;

import com.mwc.domain.valueobject.AdminId;
import com.mwc.order.service.dataaccess.admin.entity.AdminEntity;
import com.mwc.order.service.domain.entity.Admin;

public class AdminDataAccessMapper {

    public Admin adminEntityToAdmin(AdminEntity adminEntity) {
        return Admin.builder()
                .adminId(new AdminId(adminEntity.getId()))
                .username(adminEntity.getFullname())
                .active(adminEntity.isActive())
                .role(adminEntity.getAdminRole())
                .build();
    }
}
