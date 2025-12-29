package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import ru.otus.hw.entity.Order;
import ru.otus.hw.entity.OrderStatus;
import ru.otus.hw.service.OrderValidationService;
import ru.otus.hw.service.PaymentService;
import ru.otus.hw.service.ShippingService;

@Configuration
@EnableIntegration
public class OrderIntegrationConfig {

    @Bean
    public MessageChannel orderInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow orderFlow(OrderValidationService validationService,
                                     PaymentService paymentService,
                                     ShippingService shippingService) {

        return IntegrationFlow.from("orderInputChannel")

                .handle(validationService, "validate")

                .<Order, OrderStatus>route(
                        Order::getStatus,
                        mapping -> mapping

                                .subFlowMapping(OrderStatus.VALIDATED, sf -> sf
                                        .handle(paymentService, "pay")
                                        .handle(shippingService, "ship")
                                )

                                .subFlowMapping(OrderStatus.REJECTED, sf -> sf
                                        .handle((payload, headers) -> payload)
                                )
                )
                .get();
    }
}

