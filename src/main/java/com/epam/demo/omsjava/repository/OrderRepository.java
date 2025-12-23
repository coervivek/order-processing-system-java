package com.epam.demo.omsjava.repository;

import com.epam.demo.omsjava.domain.Order;
import com.epam.demo.omsjava.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
}
