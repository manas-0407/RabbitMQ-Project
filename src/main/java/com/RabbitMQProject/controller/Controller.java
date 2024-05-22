package com.RabbitMQProject.controller;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
public class Controller {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    String exchange;

    @Value("${rabbitmq.routing.key}")
    String routingKey;

    @Value("${rabbitmq.reply.routing.key}")
    String reply_routing_key;

    public ConcurrentHashMap<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    @GetMapping(value = "/send")
    public CompletableFuture<String> send(@RequestParam(value = "message") String message){

        String correlate_id = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();

        pendingRequests.put(correlate_id , future);

        rabbitTemplate.convertAndSend(exchange , routingKey , message , message1 -> {
            message1.getMessageProperties().setCorrelationId(correlate_id);
            message1.getMessageProperties().setReplyTo(reply_routing_key);
            return message1;
        });

        return future;
    }

    @RabbitListener(queues = "${rabbitmq.reply.queue.name}")
    public void receiveReply(org.springframework.amqp.core.Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        String response = new String(message.getBody());

        CompletableFuture<String> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(response);
        }
    }

}
