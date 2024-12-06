package org.app.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.app.RabbitMQConfig;

@Component
@Log4j2
public class EchoService {
    private final RabbitTemplate rabbit;

    @Autowired
    public EchoService(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = RabbitMQConfig.ECHO_IN_QUEUE_NAME)
    public void processMessage(String message, @Header(RabbitMQConfig.ECHO_MESSAGE_COUNT_PROPERTY_NAME) int messageCount) {
        log.info("Recieved Message #" + messageCount+ ": " + message);
        rabbit.convertAndSend(RabbitMQConfig.ECHO_OUT_QUEUE_NAME, "Echo " + message);
        log.info("Sent Message: Echo " + message);
    }
}