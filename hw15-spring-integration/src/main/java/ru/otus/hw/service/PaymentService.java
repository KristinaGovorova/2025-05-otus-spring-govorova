package ru.otus.hw.service;

import org.springframework.stereotype.Service;
import ru.otus.hw.entity.Order;
import ru.otus.hw.entity.OrderStatus;

@Service
public class PaymentService {

    public Order pay(Order order) {
        order.setStatus(OrderStatus.PAID);
        return order;
    }
}
