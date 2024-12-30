package com.mwc.inventory.service.dataaccess.inventory.command.adapter;

import com.mwc.inventory.service.dataaccess.inventory.command.mapper.InventoryDataAccessMapper;
import com.mwc.inventory.service.dataaccess.inventory.command.repository.InventoryJpaRepository;
import com.mwc.inventory.service.domain.ports.output.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("inventoryCommandRepository")
public class InventoryCommandRepositoryImpl implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;
    private final InventoryDataAccessMapper inventoryDataAccessMapper;


    public InventoryCommandRepositoryImpl(InventoryJpaRepository inventoryJpaRepository, InventoryDataAccessMapper inventoryDataAccessMapper) {
        this.inventoryJpaRepository = inventoryJpaRepository;
        this.inventoryDataAccessMapper = inventoryDataAccessMapper;
    }



}
