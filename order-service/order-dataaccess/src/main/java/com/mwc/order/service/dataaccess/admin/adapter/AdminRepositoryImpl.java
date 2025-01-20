package com.mwc.order.service.dataaccess.admin.adapter;

import com.mwc.order.service.dataaccess.admin.mapper.AdminDataAccessMapper;
import com.mwc.order.service.dataaccess.admin.repository.AdminJpaRepository;
import com.mwc.order.service.dataaccess.admin.repository.AdminMongoRepository;
import com.mwc.order.service.domain.entity.Admin;
import com.mwc.order.service.domain.ports.output.repository.AdminRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AdminRepositoryImpl implements AdminRepository {
    private final AdminJpaRepository adminJpaRepository;
    private final AdminDataAccessMapper adminDataAccessMapper;
    private final AdminMongoRepository adminMongoRepository;

    public AdminRepositoryImpl(AdminJpaRepository adminJpaRepository,
                               AdminDataAccessMapper adminDataAccessMapper, AdminMongoRepository adminMongoRepository) {
        this.adminJpaRepository = adminJpaRepository;
        this.adminDataAccessMapper = adminDataAccessMapper;
        this.adminMongoRepository = adminMongoRepository;
    }

    @Override
    public Optional<Admin> findAdminById(UUID id) {
        return adminMongoRepository.findByAdminId(id.toString()).map(adminDataAccessMapper::adminDocumentToAdmin);
    }
}
