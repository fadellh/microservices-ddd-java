package com.mwc.inventory.service.dataaccess.inventory.command.repository;

import com.mwc.inventory.service.dataaccess.inventory.command.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, UUID> {
}
