package com.mwc.order.service.dataaccess.admin.entity;

import com.mwc.domain.valueobject.AdminRole;
import jakarta.persistence.Id;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("admins")
public class AdminDocument {

    @Id
    private ObjectId _id;
    @Field("id")
    private UUID id;

    private String email;
    private String fullname;

    private AdminRole adminRole;
    private boolean active;

}
