/**
 * Project Name:rule-engine-core
 * File Name:RabbitMqConfiguration.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.config.rabbitmq
 * Date:2018年8月30日上午8:02:09
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.rabbitmq.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * ClassName:RabbitMqConfiguration <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月30日 上午8:02:09 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@Configuration("rabbitmqConfiguration")
public class RabbitMqConfiguration
{

	@Autowired
	private RabbitmqConfigProperties rabbitmqConfigProperties;

	@Bean(name = "sourceConnectionFactory")
	public ConnectionFactory sourceConnectionFactory()
	{
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses(rabbitmqConfigProperties.getSource().getAddress());
		connectionFactory.setUsername(rabbitmqConfigProperties.getSource().getUsername());
		connectionFactory.setPassword(rabbitmqConfigProperties.getSource().getPassword());
		connectionFactory.setVirtualHost(rabbitmqConfigProperties.getSource().getVirtualHost());
		return connectionFactory;
	}


	@Bean(name = "sourceRabbitTemplate")
	@Primary
	public RabbitTemplate secondRabbitTemplate(
			@Qualifier("sourceConnectionFactory") ConnectionFactory connectionFactory)
	{
		RabbitTemplate sourceRabbitTemplate = new RabbitTemplate(connectionFactory);
		// 使用外部事物
		// lpzRabbitTemplate.setChannelTransacted(true);
		return sourceRabbitTemplate;
	}


	@Bean(name = "sourceContainerFactory")
	public SimpleRabbitListenerContainerFactory sourceFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
			@Qualifier("sourceConnectionFactory") ConnectionFactory connectionFactory)
	{
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		// 设置消费者能力
		factory.setPrefetchCount(rabbitmqConfigProperties.getSource().getDefaultPrefetchCount());
		factory.setConcurrentConsumers(rabbitmqConfigProperties.getSource().getDefaultConcurrent());
		configurer.configure(factory, connectionFactory);
		return factory;
	}


	@Bean(name = "sourceQueue")
	public Object sourceQueue()
	{
		return new Queue(rabbitmqConfigProperties.getSource().getQueue(), rabbitmqConfigProperties.getSource().isQueueDurable(),
				rabbitmqConfigProperties.getSource().isExclusive(), rabbitmqConfigProperties.getSource().isQueueAutoDelete());
	}

	@Bean(name = "exchangeSource")
	public TopicExchange exchangeSource()
	{

		return new TopicExchange(rabbitmqConfigProperties.getSource().getExchange(),
				rabbitmqConfigProperties.getSource().isExchangeDurable(), rabbitmqConfigProperties.getSource().isExchangeAutoDelete());
	}

	@Bean
	Binding bindingExchangeSourceMessage(@Qualifier("sourceQueue") Queue queueMessage,
			@Qualifier("exchangeSource") TopicExchange exchange)
	{
		return BindingBuilder.bind(queueMessage).to(exchange).with(rabbitmqConfigProperties.getSource().getTopic());
	}
	
	@Bean(name = "actionConnectionFactory")
	@Primary
	public ConnectionFactory actionConnectionFactory()
	{
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses(rabbitmqConfigProperties.getAction().getAddress());
		connectionFactory.setUsername(rabbitmqConfigProperties.getAction().getUsername());
		connectionFactory.setPassword(rabbitmqConfigProperties.getAction().getPassword());
		connectionFactory.setVirtualHost(rabbitmqConfigProperties.getAction().getVirtualHost());
		return connectionFactory;
	}

	@Bean(name = "actionRabbitTemplate")
	// @Primary
	public RabbitTemplate actionRabbitTemplate(@Qualifier("actionConnectionFactory") ConnectionFactory connectionFactory)
	{
		RabbitTemplate actionRabbitTemplate = new RabbitTemplate(connectionFactory);
		//actionRabbitTemplate.setChannelTransacted(true);
		// 使用外部事物
		// ydtRabbitTemplate.setChannelTransacted(true);
		return actionRabbitTemplate;
	}

	@Bean(name = "actionContainerFactory")
	public SimpleRabbitListenerContainerFactory actionFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
			@Qualifier("actionConnectionFactory") ConnectionFactory connectionFactory)
	{
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		return factory;
	}


	@Bean(name = "actionQueue")
	public Queue actionQueue()
	{
		return new Queue(rabbitmqConfigProperties.getAction().getQueue(), rabbitmqConfigProperties.getAction().isQueueDurable(),
				rabbitmqConfigProperties.getAction().isExclusive(), rabbitmqConfigProperties.getAction().isQueueAutoDelete());
	}


	@Bean(name = "exchangeAction")
	public TopicExchange exchangeAction()
	{
		return new TopicExchange(rabbitmqConfigProperties.getAction().getExchange(),
				rabbitmqConfigProperties.getAction().isExchangeDurable(), rabbitmqConfigProperties.getAction().isExchangeAutoDelete());
	}


	@Bean
	Binding bindingExchangeActionMessage(@Qualifier("actionQueue") Queue queueMessage,
			@Qualifier("exchangeAction") TopicExchange exchange)
	{
		return BindingBuilder.bind(queueMessage).to(exchange).with(rabbitmqConfigProperties.getAction().getTopic());
	}

}
