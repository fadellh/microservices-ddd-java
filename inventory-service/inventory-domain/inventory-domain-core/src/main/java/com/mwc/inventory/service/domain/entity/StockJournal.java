package com.mwc.inventory.service.domain.entity;

import com.mwc.domain.valueobject.InventoryId;
import com.mwc.inventory.service.domain.valueobject.Quantity;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;
import com.mwc.inventory.service.domain.valueobject.StockJournalStatus;
import com.mwc.inventory.service.domain.valueobject.StockJournalType;

import java.time.Instant;

public class StockJournal {
    private InventoryId inventoryId;
    private Quantity quantityChanged;
    private StockJournalReason reason;
    private StockJournalType type; // INCREASE / DECREASE
    private StockJournalStatus status;
    private Instant createdAt;

    private StockJournal() {
        // private constructor
    }

    public static StockJournal create(InventoryId inventoryId,
                                      Quantity qty,
                                      StockJournalReason reason,
                                      StockJournalType type, StockJournalStatus status) {
        StockJournal journal = new StockJournal();
        journal.inventoryId = inventoryId;
        journal.quantityChanged = qty;
        journal.reason = reason;
        journal.status = status;
        journal.type = type;
        journal.createdAt = Instant.now();
        return journal;
    }

    // GETTER
    public InventoryId getInventoryId() { return inventoryId; }
    public Quantity getQuantityChanged() { return quantityChanged; }
    public StockJournalReason getReason() { return reason; }
    public StockJournalType getType() { return type; }
    public Instant getCreatedAt() { return createdAt; }
    public StockJournalStatus getStatus() { return status; }
}