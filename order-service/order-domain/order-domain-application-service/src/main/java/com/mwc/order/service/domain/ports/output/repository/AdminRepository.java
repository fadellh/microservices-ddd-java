package com.mwc.order.service.domain.ports.output.repository;

import com.mwc.order.service.domain.entity.Admin;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository {
    Optional<Admin> findAdminById(UUID id);
}
