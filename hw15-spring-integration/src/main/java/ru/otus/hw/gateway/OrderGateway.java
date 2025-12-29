package ru.otus.hw.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.entity.Order;

@MessagingGateway
public interface OrderGateway {

    @Gateway(requestChannel = "orderInputChannel")
    Order process(Order order);
}
