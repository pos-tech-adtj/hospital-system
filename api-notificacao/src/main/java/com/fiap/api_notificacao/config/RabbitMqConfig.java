package com.fiap.api_notificacao.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.boot.amqp.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    public static final String CONSULTAS_EXCHANGE = "consultas.exchange";
    public static final String NOTIFICACAO_CONSULTA_QUEUE = "notificacao.consulta.queue";
    public static final String NOTIFICACAO_CONSULTA_DLX = "notificacao.consulta.dlx";
    public static final String NOTIFICACAO_CONSULTA_DLQ = "notificacao.consulta.dlq";
    public static final String NOTIFICACAO_CONSULTA_ROUTING_KEY = "consulta.notificacao.dlq";
    public static final String NOTIFICACAO_CONSULTA_DLQ_ROUTING_KEY = "notificacao.consulta.dlq";

    @Bean
    public TopicExchange consultasExchange() {
        return new TopicExchange(CONSULTAS_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange notificacaoConsultaDlqExchange() {
        return new DirectExchange(NOTIFICACAO_CONSULTA_DLX, true, false);
    }

    @Bean
    public Queue notificacaoConsultaQueue() {
        return QueueBuilder.durable(NOTIFICACAO_CONSULTA_QUEUE)
                .deadLetterExchange(NOTIFICACAO_CONSULTA_DLX)
                .deadLetterRoutingKey(NOTIFICACAO_CONSULTA_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue notificacaoConsultaDlq() {
        return QueueBuilder.durable(NOTIFICACAO_CONSULTA_DLQ).build();
    }

    @Bean
    public Binding notificacaoConsultaBinding(TopicExchange consultasExchange, Queue notificacaoConsultaQueue) {
        return BindingBuilder.bind(notificacaoConsultaQueue)
                .to(consultasExchange)
                .with("consulta.*");
    }

    @Bean
    public Binding notificacaoConsultaDlqBinding(DirectExchange notificacaoConsultaDlqExchange, Queue notificacaoConsultaDlq) {
        return BindingBuilder.bind(notificacaoConsultaDlq)
                .to(notificacaoConsultaDlqExchange)
                .with(NOTIFICACAO_CONSULTA_DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new JacksonJsonMessageConverter();
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
        factory.setErrorHandler(new ConditionalRejectingErrorHandler());
        factory.setAdviceChain(
                org.springframework.amqp.rabbit.config.RetryInterceptorBuilder.stateless()
                        .maxRetries(3)
                        .backOffOptions(1000L, 2.0, 8000L)
                        .recoverer(new org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer())
                        .build()
        );
        return factory;
    }
}
