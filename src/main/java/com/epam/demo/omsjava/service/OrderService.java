package com.epam.demo.omsjava.service;

import com.epam.demo.omsjava.domain.OrderStatus;
import com.epam.demo.omsjava.dto.CreateOrderRequest;
import com.epam.demo.omsjava.dto.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrder(Long id);
    List<OrderResponse> getAllOrders(OrderStatus status);
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
    void cancelOrder(Long id);
    void updatePendingOrdersToProcessing();
}
