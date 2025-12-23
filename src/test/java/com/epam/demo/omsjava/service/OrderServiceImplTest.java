package com.epam.demo.omsjava.service;

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
import com.epam.demo.omsjava.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderSagaOrchestrator sagaOrchestrator;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID orderId;
    private Order order;
    private CreateOrderRequest createRequest;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        order = createTestOrder();
        createRequest = createTestRequest();
    }

    @Test
    void createOrder_Success() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        OrderResponse response = orderService.createOrder(createRequest);
        
        assertNotNull(response);
        assertEquals("user123", response.getUserId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(1050.99, response.getTotal());
        verify(orderRepository).save(any(Order.class));
        verify(sagaOrchestrator).startOrderSaga(any(UUID.class), eq(1050.99));
    }

    @Test
    void getOrder_Success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        OrderResponse response = orderService.getOrder(orderId);
        
        assertNotNull(response);
        assertEquals(orderId, response.getId());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrder_NotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId));
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getAllOrders_WithoutStatus() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
        
        List<OrderResponse> responses = orderService.getAllOrders(null);
        
        assertEquals(1, responses.size());
        verify(orderRepository).findAll();
    }

    @Test
    void getAllOrders_WithStatus() {
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(Arrays.asList(order));
        
        List<OrderResponse> responses = orderService.getAllOrders(OrderStatus.PENDING);
        
        assertEquals(1, responses.size());
        verify(orderRepository).findByStatus(OrderStatus.PENDING);
    }

    @Test
    void updateOrderStatus_Success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        OrderResponse response = orderService.updateOrderStatus(orderId, OrderStatus.PROCESSING);
        
        assertNotNull(response);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_NotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        assertThrows(OrderNotFoundException.class, 
            () -> orderService.updateOrderStatus(orderId, OrderStatus.PROCESSING));
    }

    @Test
    void cancelOrder_Success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        orderService.cancelOrder(orderId);
        
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
        verify(sagaOrchestrator).compensateOrder(orderId);
    }

    @Test
    void cancelOrder_InvalidState() {
        order.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        assertThrows(InvalidOrderStateException.class, () -> orderService.cancelOrder(orderId));
    }

    @Test
    void cancelOrder_NotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(orderId));
    }

    @Test
    void updatePendingOrdersToProcessing_Success() {
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(Arrays.asList(order));
        when(orderRepository.saveAll(anyList())).thenReturn(Arrays.asList(order));
        
        orderService.updatePendingOrdersToProcessing();
        
        verify(orderRepository).findByStatus(OrderStatus.PENDING);
        verify(orderRepository).saveAll(anyList());
    }

    private Order createTestOrder() {
        Order o = new Order();
        o.setId(orderId);
        o.setUserId("user123");
        o.setStatus(OrderStatus.PENDING);
        o.setCreatedAt(LocalDateTime.now());
        
        OrderItem item1 = new OrderItem();
        item1.setProductName("Laptop");
        item1.setQuantity(1);
        item1.setPrice(999.99);
        item1.setOrder(o);
        
        OrderItem item2 = new OrderItem();
        item2.setProductName("Mouse");
        item2.setQuantity(2);
        item2.setPrice(25.50);
        item2.setOrder(o);
        
        o.setItems(Arrays.asList(item1, item2));
        return o;
    }

    private CreateOrderRequest createTestRequest() {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setUserId("user123");
        
        OrderItemDto dto1 = new OrderItemDto();
        dto1.setProductName("Laptop");
        dto1.setQuantity(1);
        dto1.setPrice(999.99);
        
        OrderItemDto dto2 = new OrderItemDto();
        dto2.setProductName("Mouse");
        dto2.setQuantity(2);
        dto2.setPrice(25.50);
        
        req.setItems(Arrays.asList(dto1, dto2));
        return req;
    }
}
