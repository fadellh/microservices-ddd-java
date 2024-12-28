package com.mwc.order.service.dataaccess.order.command.repository;

import com.mwc.order.service.dataaccess.order.command.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}