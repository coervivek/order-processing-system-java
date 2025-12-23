package com.demo.oms.job;

import com.demo.oms.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusJobTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderStatusJob orderStatusJob;

    @Test
    void updatePendingOrders_Success() {
        doNothing().when(orderService).updatePendingOrdersToProcessing();
        
        orderStatusJob.updatePendingOrders();
        
        verify(orderService).updatePendingOrdersToProcessing();
    }
}
