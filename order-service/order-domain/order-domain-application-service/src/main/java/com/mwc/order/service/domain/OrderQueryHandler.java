package com.mwc.order.service.domain;

import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderQuery;
import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderQueryResponse;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.exception.OrderNotFoundException;
import com.mwc.order.service.domain.mapper.OrderDataMapper;
import com.mwc.order.service.domain.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class OrderQueryHandler {
    private final OrderDataMapper orderDataMapper;

    private final OrderRepository orderRepository;

    public OrderQueryHandler(OrderDataMapper orderDataMapper, @Qualifier("queryRepository") OrderRepository orderRepository) {
        this.orderDataMapper = orderDataMapper;
        this.orderRepository = orderRepository;
    }


    @Transactional(readOnly = true)
    public List<RetrieveOrderQueryResponse> retrieveOrders(RetrieveOrderQuery orderListQuery){
        List<Order> orderResult =
                orderRepository.findByCustomerIdAndFilters(orderListQuery.getCustomerId(), orderListQuery.getOrderNumber(),
                        orderListQuery.getOrderStartDate(), orderListQuery.getOrderEndDate());
        if(orderResult.isEmpty()){
            log.warn("Couldn't find order with customer id: {} ", orderListQuery.getCustomerId());
            throw new OrderNotFoundException("Couldn't find order with customer id: {} " +
                    orderListQuery.getCustomerId());
        }

        return orderDataMapper.orderListToRetrieveOrderQueryResponse(orderResult);
    }

//    @Transactional(readOnly = true)
//    public OrderListQueryResponse retrieveOrders(OrderListQuery orderListQuery){
//        Optional<Order> orderResult =
//                orderRepository.findById(new OrderId(orderListQuery.getOrderNumber()).getValue());
//        if(orderResult.isEmpty()){
//            log.warn("Couldn't find order with tracking id: {} ", orderListQuery.getOrderNumber());
//            throw new OrderNotFoundException("Couldn't find order with tracking id: {} " +
//                    orderListQuery.getOrderNumber());
//        }
//
//
//        return orderDataMapper.orderToTrackOrderResponse(orderResult.get());
//    }


}
