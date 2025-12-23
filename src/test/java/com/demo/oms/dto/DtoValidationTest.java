package com.demo.oms.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createOrderRequest_ValidRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId("user123");
        
        OrderItemDto item = new OrderItemDto();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(999.99);
        request.setItems(Arrays.asList(item));
        
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void createOrderRequest_BlankUserId() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId("");
        
        OrderItemDto item = new OrderItemDto();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(999.99);
        request.setItems(Arrays.asList(item));
        
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void createOrderRequest_EmptyItems() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId("user123");
        request.setItems(Collections.emptyList());
        
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void orderItemDto_ValidItem() {
        OrderItemDto item = new OrderItemDto();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(999.99);
        
        Set<ConstraintViolation<OrderItemDto>> violations = validator.validate(item);
        assertTrue(violations.isEmpty());
    }

    @Test
    void orderItemDto_BlankProductName() {
        OrderItemDto item = new OrderItemDto();
        item.setProductName("");
        item.setQuantity(1);
        item.setPrice(999.99);
        
        Set<ConstraintViolation<OrderItemDto>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
    }

    @Test
    void orderItemDto_ZeroQuantity() {
        OrderItemDto item = new OrderItemDto();
        item.setProductName("Laptop");
        item.setQuantity(0);
        item.setPrice(999.99);
        
        Set<ConstraintViolation<OrderItemDto>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
    }

    @Test
    void orderItemDto_NegativePrice() {
        OrderItemDto item = new OrderItemDto();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(-10.0);
        
        Set<ConstraintViolation<OrderItemDto>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
    }
}
