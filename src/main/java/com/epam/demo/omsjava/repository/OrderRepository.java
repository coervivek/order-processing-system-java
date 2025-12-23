package com.epam.demo.omsjava.repository;

import com.epam.demo.omsjava.domain.Order;
import com.epam.demo.omsjava.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatus(OrderStatus status);
}
