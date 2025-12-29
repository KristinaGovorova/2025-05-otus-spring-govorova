package ru.otus.hw.service;

import org.springframework.stereotype.Service;
import ru.otus.hw.entity.Order;
import ru.otus.hw.entity.OrderStatus;

@Service
public class ShippingService {

    public Order ship(Order order) {
        order.setStatus(OrderStatus.SHIPPED);
        return order;
    }
}