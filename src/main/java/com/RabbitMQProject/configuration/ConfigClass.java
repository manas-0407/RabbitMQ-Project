package com.RabbitMQProject.configuration;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ConfigClass {

    @Value("${rabbitmq.queue.name}")
    String queue;

    @Value("${rabbitmq.exchange.name}")
    String exchange;

    @Value("${rabbitmq.routing.key}")
    String routing_key;

    @Value("${rabbitmq.reply.queue.name}")
    String replyQueue;

    @Value("${rabbitmq.reply.routing.key}")
    String reply_routing_key;


    @Bean
    public Queue myQueue() {
        return new Queue( queue , false);
    }

    @Bean
    public Queue myReplyQueue() {
        return new Queue( replyQueue , false);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(myQueue()).
                to(exchange()).
                with(routing_key);
    }

    @Bean
    public Binding binding2(){
        return BindingBuilder.bind(myReplyQueue()).
                to(exchange()).
                with(reply_routing_key);
    }

}
