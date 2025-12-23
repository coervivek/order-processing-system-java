package com.demo.oms.repository;

import com.demo.oms.domain.Order;
import com.demo.oms.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatus(OrderStatus status);
}
