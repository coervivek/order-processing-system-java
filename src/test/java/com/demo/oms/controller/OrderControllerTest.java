package com.demo.oms.controller;

import com.demo.oms.domain.OrderStatus;
import com.demo.oms.dto.CreateOrderRequest;
import com.demo.oms.dto.OrderItemDto;
import com.demo.oms.dto.OrderResponse;
import com.demo.oms.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private UUID orderId;
    private OrderResponse orderResponse;
    private CreateOrderRequest createRequest;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        orderResponse = createOrderResponse();
        createRequest = createTestRequest();
    }

    @Test
    void createOrder_Success() {
        when(orderService.createOrder(createRequest)).thenReturn(orderResponse);
        
        ResponseEntity<OrderResponse> response = orderController.createOrder(createRequest);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orderResponse, response.getBody());
        verify(orderService).createOrder(createRequest);
    }

    @Test
    void getOrder_Success() {
        when(orderService.getOrder(orderId)).thenReturn(orderResponse);
        
        ResponseEntity<OrderResponse> response = orderController.getOrder(orderId);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orderResponse, response.getBody());
        verify(orderService).getOrder(orderId);
    }

    @Test
    void getAllOrders_WithoutStatus() {
        when(orderService.getAllOrders(null)).thenReturn(Arrays.asList(orderResponse));
        
        ResponseEntity<List<OrderResponse>> response = orderController.getAllOrders(null);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(orderService).getAllOrders(null);
    }

    @Test
    void getAllOrders_WithStatus() {
        when(orderService.getAllOrders(OrderStatus.PENDING)).thenReturn(Arrays.asList(orderResponse));
        
        ResponseEntity<List<OrderResponse>> response = orderController.getAllOrders(OrderStatus.PENDING);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(orderService).getAllOrders(OrderStatus.PENDING);
    }

    @Test
    void updateOrderStatus_Success() {
        when(orderService.updateOrderStatus(orderId, OrderStatus.PROCESSING)).thenReturn(orderResponse);
        
        ResponseEntity<OrderResponse> response = orderController.updateOrderStatus(orderId, OrderStatus.PROCESSING);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService).updateOrderStatus(orderId, OrderStatus.PROCESSING);
    }

    @Test
    void cancelOrder_Success() {
        doNothing().when(orderService).cancelOrder(orderId);
        
        ResponseEntity<Void> response = orderController.cancelOrder(orderId);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService).cancelOrder(orderId);
    }

    private OrderResponse createOrderResponse() {
        OrderResponse resp = new OrderResponse();
        resp.setId(orderId);
        resp.setUserId("user123");
        resp.setStatus(OrderStatus.PENDING);
        resp.setCreatedAt(LocalDateTime.now());
        resp.setTotal(1050.99);
        
        OrderItemDto dto = new OrderItemDto();
        dto.setProductName("Laptop");
        dto.setQuantity(1);
        dto.setPrice(999.99);
        
        resp.setItems(Arrays.asList(dto));
        return resp;
    }

    private CreateOrderRequest createTestRequest() {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setUserId("user123");
        
        OrderItemDto dto = new OrderItemDto();
        dto.setProductName("Laptop");
        dto.setQuantity(1);
        dto.setPrice(999.99);
        
        req.setItems(Arrays.asList(dto));
        return req;
    }
}
