package com.fiap.api_agendamento.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    public static final String CONSULTAS_EXCHANGE = "consultas.exchange";
    public static final String HISTORICO_CONSULTA_QUEUE = "historico.consulta.queue";
    public static final String HISTORICO_CONSULTA_DLX = "historico.consulta.dlx";
    public static final String HISTORICO_CONSULTA_DLQ = "historico.consulta.dlq";
    public static final String HISTORICO_CONSULTA_DLQ_ROUTING_KEY = "historico.consulta.dlq";

    @Bean
    public TopicExchange consultasExchange() {
        return new TopicExchange(CONSULTAS_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange historicoConsultaDlqExchange() {
        return new DirectExchange(HISTORICO_CONSULTA_DLX, true, false);
    }

    @Bean
    public Queue historicoConsultaQueue() {
        return QueueBuilder.durable(HISTORICO_CONSULTA_QUEUE)
                .deadLetterExchange(HISTORICO_CONSULTA_DLX)
                .deadLetterRoutingKey(HISTORICO_CONSULTA_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue historicoConsultaDlq() {
        return QueueBuilder.durable(HISTORICO_CONSULTA_DLQ).build();
    }

    @Bean
    public Binding historicoConsultaBinding(TopicExchange consultasExchange, Queue historicoConsultaQueue) {
        return BindingBuilder.bind(historicoConsultaQueue)
                .to(consultasExchange)
                .with("consulta.*");
    }

    @Bean
    public Binding historicoConsultaDlqBinding(DirectExchange historicoConsultaDlqExchange, Queue historicoConsultaDlq) {
        return BindingBuilder.bind(historicoConsultaDlq)
                .to(historicoConsultaDlqExchange)
                .with(HISTORICO_CONSULTA_DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory,
            MessageConverter jackson2JsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}