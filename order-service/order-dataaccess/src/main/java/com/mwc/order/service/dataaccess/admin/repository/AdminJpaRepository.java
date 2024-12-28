package com.mwc.order.service.dataaccess.admin.repository;

import com.mwc.order.service.dataaccess.admin.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminJpaRepository extends JpaRepository<AdminEntity, UUID> {
}
