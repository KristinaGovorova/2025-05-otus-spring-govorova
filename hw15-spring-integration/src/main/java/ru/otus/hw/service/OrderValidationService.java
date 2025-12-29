package ru.otus.hw.service;

import org.springframework.stereotype.Service;
import ru.otus.hw.entity.Order;
import ru.otus.hw.entity.OrderStatus;

import java.math.BigDecimal;

@Service
public class OrderValidationService {

    public Order validate(Order order) {
        if (order.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            order.setStatus(OrderStatus.VALIDATED);
        } else {
            order.setStatus(OrderStatus.REJECTED);
        }
        return order;
    }
}

