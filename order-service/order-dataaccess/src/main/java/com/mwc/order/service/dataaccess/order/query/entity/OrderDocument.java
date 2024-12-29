package com.mwc.order.service.dataaccess.order.query.entity;

import com.mwc.domain.valueobject.OrderStatus;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("orders") // MongoDB collection name
public class OrderDocument {

    @Id
    private ObjectId _id;
    private UUID orderId; // Primary key for MongoDB document

    @Indexed
    private UUID customerId; // Index for frequent queries by customerId

    private String customerAddress;

    @Indexed
    private UUID warehouseId; // Index for frequent queries by warehouseId

    private BigDecimal totalAmount;
    private BigDecimal shippingCost;

    private OrderStatus orderStatus; // Enum stored as String
    private String failureMessages;

    private OrderAddressDocument orderAddress; // Embedded sub-document for address

    private List<OrderItemDocument> items; // Embedded list for order items

}
