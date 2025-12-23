package com.demo.oms.service.impl;

import com.demo.oms.domain.Order;
import com.demo.oms.domain.OrderItem;
import com.demo.oms.domain.OrderStatus;
import com.demo.oms.dto.CreateOrderRequest;
import com.demo.oms.dto.OrderItemDto;
import com.demo.oms.dto.OrderResponse;
import com.demo.oms.exception.InvalidOrderStateException;
import com.demo.oms.exception.OrderNotFoundException;
import com.demo.oms.repository.OrderRepository;
import com.demo.oms.saga.OrderSagaOrchestrator;
import com.demo.oms.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderSagaOrchestrator sagaOrchestrator;

    public OrderServiceImpl(OrderRepository orderRepository, OrderSagaOrchestrator sagaOrchestrator) {
        this.orderRepository = orderRepository;
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        List<OrderItem> items = request.getItems().stream().map(dto -> {
            OrderItem item = new OrderItem();
            item.setProductName(dto.getProductName());
            item.setQuantity(dto.getQuantity());
            item.setPrice(dto.getPrice());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());
        order.setItems(items);
        Order saved = orderRepository.save(order);
        
        Double totalAmount = items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        sagaOrchestrator.startOrderSaga(saved.getId(), totalAmount);
        
        return toResponse(saved);
    }

    @Override
    public OrderResponse getOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders(OrderStatus status) {
        List<Order> orders = (status == null) ? orderRepository.findAll() : orderRepository.findByStatus(status);
        return orders.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Order can only be cancelled when in PENDING status");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        sagaOrchestrator.compensateOrder(id);
    }

    @Override
    @Transactional
    public void updatePendingOrdersToProcessing() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        pendingOrders.forEach(order -> order.setStatus(OrderStatus.PROCESSING));
        orderRepository.saveAll(pendingOrders);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse resp = new OrderResponse();
        resp.setId(order.getId());
        resp.setUserId(order.getUserId());
        resp.setStatus(order.getStatus());
        resp.setCreatedAt(order.getCreatedAt());
        List<OrderItemDto> items = order.getItems().stream().map(item -> {
            OrderItemDto dto = new OrderItemDto();
            dto.setProductName(item.getProductName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            return dto;
        }).collect(Collectors.toList());
        resp.setItems(items);
        resp.setTotal(order.getItems().stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum());
        return resp;
    }
}
