package com.epam.demo.omsjava.service.impl;

import com.epam.demo.omsjava.domain.Order;
import com.epam.demo.omsjava.domain.OrderItem;
import com.epam.demo.omsjava.domain.OrderStatus;
import com.epam.demo.omsjava.dto.CreateOrderRequest;
import com.epam.demo.omsjava.dto.OrderItemDto;
import com.epam.demo.omsjava.dto.OrderResponse;
import com.epam.demo.omsjava.exception.InvalidOrderStateException;
import com.epam.demo.omsjava.exception.OrderNotFoundException;
import com.epam.demo.omsjava.repository.OrderRepository;
import com.epam.demo.omsjava.saga.OrderSagaOrchestrator;
import com.epam.demo.omsjava.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public OrderResponse getOrder(Long id) {
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
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
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
        return resp;
    }
}
