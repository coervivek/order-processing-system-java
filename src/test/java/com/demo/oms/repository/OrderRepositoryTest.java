package com.demo.oms.repository;

import com.demo.oms.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {

    @Test
    void orderRepository_ExtendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(OrderRepository.class));
    }

    @Test
    void orderRepository_HasFindByStatusMethod() throws NoSuchMethodException {
        assertNotNull(OrderRepository.class.getMethod("findByStatus", OrderStatus.class));
    }
}
