package com.demo.oms.job;

import com.demo.oms.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusJob {
    private final OrderService orderService;

    public OrderStatusJob(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(cron = "${order.status.update.cron}")
    public void updatePendingOrders() {
        orderService.updatePendingOrdersToProcessing();
    }
}

