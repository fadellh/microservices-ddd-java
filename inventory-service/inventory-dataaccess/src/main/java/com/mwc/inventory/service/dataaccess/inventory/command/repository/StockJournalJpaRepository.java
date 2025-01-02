package com.mwc.inventory.service.dataaccess.inventory.command.repository;

import com.mwc.inventory.service.dataaccess.inventory.command.entity.StockJournalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockJournalJpaRepository extends JpaRepository<StockJournalEntity, UUID> {
}
