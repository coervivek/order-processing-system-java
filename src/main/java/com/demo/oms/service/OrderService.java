package com.demo.oms.service;

import com.demo.oms.domain.OrderStatus;
import com.demo.oms.dto.CreateOrderRequest;
import com.demo.oms.dto.OrderResponse;
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
