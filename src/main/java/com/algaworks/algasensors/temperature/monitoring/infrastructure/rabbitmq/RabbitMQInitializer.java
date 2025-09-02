package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Configuration;

//chama o rabbitmiq e cria as queues, exchanges e bindings configuradas
@Configuration
@RequiredArgsConstructor
public class RabbitMQInitializer {

    private final RabbitAdmin rabbitAdmin;


    @PostConstruct
    public void init() {
        rabbitAdmin.initialize();
    }
}
