package com.mwc.inventory.service.dataaccess.inventory.command.entity;

import com.mwc.inventory.service.domain.valueobject.StockJournalReason;
import com.mwc.inventory.service.domain.valueobject.StockJournalStatus;
import com.mwc.inventory.service.domain.valueobject.StockJournalType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock_journal")
@Entity
public class StockJournalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItemEntity inventoryItem;

    private Integer totalQuantityChanged;
    private Integer quantityChanged;

    @Enumerated(EnumType.STRING)
    private StockJournalReason reason;

    @Enumerated(EnumType.STRING)
    private StockJournalType type;

    @Enumerated(EnumType.STRING)
    private StockJournalStatus status;

    private LocalDateTime timestamp;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;


}