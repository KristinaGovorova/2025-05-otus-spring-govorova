package ru.otus.hw.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Order {
    private Long id;
    private BigDecimal amount;
    private OrderStatus status;
}