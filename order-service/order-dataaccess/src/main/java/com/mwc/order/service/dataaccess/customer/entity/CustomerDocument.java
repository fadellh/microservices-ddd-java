package com.mwc.order.service.dataaccess.customer.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("customers")
public class CustomerDocument {
    @Id
    private Object _id;
    @Field("id")
    private UUID id;

    private String email;
    private String fullname;
}
