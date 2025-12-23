package com.epam.demo.omsjava.controller;

import com.epam.demo.omsjava.domain.OrderStatus;
import com.epam.demo.omsjava.dto.CreateOrderRequest;
import com.epam.demo.omsjava.dto.OrderResponse;
import com.epam.demo.omsjava.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order with items")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Returns a single order by UUID")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns all orders or filtered by status")
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @Parameter(description = "Filter by order status") @RequestParam(value = "status", required = false) OrderStatus status) {
        return ResponseEntity.ok(orderService.getAllOrders(status));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an existing order")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "Order UUID") @PathVariable UUID id,
            @Parameter(description = "New order status") @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an order (only PENDING orders can be cancelled)")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }
}

