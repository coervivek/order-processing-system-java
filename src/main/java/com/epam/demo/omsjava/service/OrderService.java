package com.epam.demo.omsjava.service;

import com.epam.demo.omsjava.domain.OrderStatus;
import com.epam.demo.omsjava.dto.CreateOrderRequest;
import com.epam.demo.omsjava.dto.OrderResponse;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrder(UUID id);
    List<OrderResponse> getAllOrders(OrderStatus status);
    OrderResponse updateOrderStatus(UUID id, OrderStatus status);
    void cancelOrder(UUID id);
    void updatePendingOrdersToProcessing();
}
