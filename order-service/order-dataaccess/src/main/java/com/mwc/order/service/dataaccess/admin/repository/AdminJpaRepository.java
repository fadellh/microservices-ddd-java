package com.mwc.order.service.dataaccess.admin.repository;

import com.mwc.order.service.dataaccess.admin.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminJpaRepository extends JpaRepository<AdminEntity, UUID> {
}
