package com.mwc.order.service.dataaccess.customer.adapter;

import com.mwc.order.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import com.mwc.order.service.dataaccess.customer.repository.CustomerJpaRepository;
import com.mwc.order.service.dataaccess.customer.repository.CustomerMongoRepository;
import com.mwc.order.service.domain.entity.Customer;
import com.mwc.order.service.domain.ports.output.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;
    private final CustomerMongoRepository customerMongoRepository;

    public CustomerRepositoryImpl(CustomerJpaRepository customerJpaRepository,
                                  CustomerDataAccessMapper customerDataAccessMapper, CustomerMongoRepository customerMongoRepository) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerDataAccessMapper = customerDataAccessMapper;
        this.customerMongoRepository = customerMongoRepository;
    }

    @Override
    public Optional<Customer> findCustomer(UUID customerId) {
        return customerMongoRepository.findByCustomerId(customerId.toString())
                .map(customerDataAccessMapper::customerDocumentToCustomer);
    }
}
