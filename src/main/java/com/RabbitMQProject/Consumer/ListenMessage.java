package com.RabbitMQProject.Consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Component
public class ListenMessage {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    String exchange;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consumer(Message message){

        String request = new String(message.getBody());
        String correaltion_id = message.getMessageProperties().getCorrelationId();
        String replyTo = message.getMessageProperties().getReplyTo();

        // Process task Here
        String processed_reponse = "PROCESSED WITH MESSAGE : "+ request;

        rabbitTemplate.convertAndSend(exchange, replyTo, processed_reponse, replyMessage -> {
            replyMessage.getMessageProperties().setCorrelationId(correaltion_id);
            return  replyMessage;
        });
    }

}
