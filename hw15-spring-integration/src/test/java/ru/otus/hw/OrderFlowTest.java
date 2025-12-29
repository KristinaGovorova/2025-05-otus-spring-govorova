package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.entity.Order;
import ru.otus.hw.entity.OrderStatus;
import ru.otus.hw.gateway.OrderGateway;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OrderFlowTest {

    @Autowired
    private OrderGateway orderGateway;

    @Test
    void testValidOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setAmount(new BigDecimal("100"));

        Order result = orderGateway.process(order);

        assertEquals(OrderStatus.SHIPPED, result.getStatus());
    }

    @Test
    void testRejectedOrder() {
        Order order = new Order();
        order.setId(2L);
        order.setAmount(BigDecimal.ZERO);

        Order result = orderGateway.process(order);

        assertEquals(OrderStatus.REJECTED, result.getStatus());
    }
}

