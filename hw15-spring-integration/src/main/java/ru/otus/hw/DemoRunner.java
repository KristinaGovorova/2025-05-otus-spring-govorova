package ru.otus.hw;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.entity.Order;
import ru.otus.hw.gateway.OrderGateway;

import java.math.BigDecimal;

@Component
public class DemoRunner implements CommandLineRunner {

    private final OrderGateway orderGateway;

    public DemoRunner(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    @Override
    public void run(String... args) throws Exception {
        Order validOrder = new Order();
        validOrder.setId(1L);
        validOrder.setAmount(new BigDecimal("50"));

        Order rejectedOrder = new Order();
        rejectedOrder.setId(2L);
        rejectedOrder.setAmount(BigDecimal.ZERO);

        Order resultValid = orderGateway.process(validOrder);
        System.out.println("Valid order result: " + resultValid.getStatus());

        Order resultRejected = orderGateway.process(rejectedOrder);
        System.out.println("Rejected order result: " + resultRejected.getStatus());
    }
}