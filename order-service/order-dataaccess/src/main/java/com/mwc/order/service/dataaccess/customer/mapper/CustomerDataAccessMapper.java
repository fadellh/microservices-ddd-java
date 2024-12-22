package com.mwc.order.service.dataaccess.customer.mapper;

import com.mwc.domain.valueobject.CustomerId;
import com.mwc.order.service.dataaccess.customer.entity.CustomerEntity;
import com.mwc.order.service.domain.entity.Customer;
import org.springframework.stereotype.Component;


@Component
public class CustomerDataAccessMapper {

    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(new CustomerId(customerEntity.getId()));
    }
}