package com.mwc.order.service.dataaccess.admin.entity;


import com.mwc.domain.valueobject.AdminRole;
import com.mwc.domain.valueobject.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_admin_m_view", schema = "admin")
@Entity
public class AdminEntity {
    @Id
    private UUID id;

    @NotBlank
    private String email;

    @NotBlank
    private String fullname;

    @Enumerated(EnumType.STRING)
    private AdminRole adminRole;

    @NotBlank
    private boolean active;
}
