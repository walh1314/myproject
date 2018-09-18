/**
 * Project Name:rule-engine-core
 * File Name:RabbitmqConsumer.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.rabbit
 * Date:2018年8月28日上午8:53:18
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.rabbitmq.rabbitmq;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.core.pro.server.rule.engine.core.entity.data.MqttData;
import com.foxconn.core.pro.server.rule.engine.rabbitmq.taskexecutor.RuleEngineTask;
import com.foxconn.core.pro.server.rule.engine.rabbitmq.taskexecutor.RuleEngineTaskExecutorConfig;
import com.foxconn.core.pro.server.rule.engine.rabbitmq.taskexecutor.ThreadPoolRuleEngineTaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName:RabbitmqConsumer <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月28日 上午8:53:18 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@Component
@Slf4j
public class RabbitmqConsumer
{
	@Resource(name = "sourceRabbitTemplate")
	private AmqpTemplate sourceRabbitTemplate;

	@Resource
	private ThreadPoolRuleEngineTaskExecutor ruleEngineTaskExecutor;
	

	@Autowired
	private RuleEngineTaskExecutorConfig ruleEngineTaskExecutorConfig;

	@Autowired
	private RuleEngineTask ruleEngineTask;

	@RabbitListener(bindings =
	{ @QueueBinding(value = @Queue(value = "${spring.rabbitmq.source.queue}", autoDelete = "${spring.rabbitmq.source.queue-auto-delete}", durable = "${spring.rabbitmq.source.queue-durable}", exclusive = "${spring.rabbitmq.source.exclusive}"), exchange = @Exchange(value = "${spring.rabbitmq.source.exchange}", durable = "${spring.rabbitmq.source.exchange-durable}", autoDelete = "${spring.rabbitmq.source.exchange-auto-delete}", type = ExchangeTypes.TOPIC)) }, containerFactory = "sourceContainerFactory")
	public void consumer(@Header("amqp_receivedRoutingKey") String routeKey,
			org.springframework.amqp.core.Message message)
	{
		log.info("===============start===============");
		String responseMessage = null;
		try
		{
			if (message == null || message.getBody() == null || StringUtils.isEmpty(new String(message.getBody())))
			{
				return;
			}
			responseMessage = new String(message.getBody());

			MqttData mqttData = JSONObject.parseObject(responseMessage, MqttData.class);
			log.info("===============body===============" + mqttData != null ? JSONObject.toJSONString(mqttData) : null);
			ruleEngineTask.setData(mqttData);
			ruleEngineTaskExecutor.execute(ruleEngineTask);// 开启多线程处理
		} catch (Exception e)
		{
			log.error("rule engine exception:" + responseMessage, e);
		}

		Integer executorCount = 0;
		Integer maxPoolSize = 0;
		executorCount = ruleEngineTaskExecutor.getActiveCount();
		maxPoolSize = ruleEngineTaskExecutor.getMaxPoolSize();
		if ((executorCount * 100 / maxPoolSize) > ruleEngineTaskExecutorConfig.getPercentage())
		{
			try
			{
				TimeUnit.SECONDS.sleep(ruleEngineTaskExecutorConfig.getSleepTime());
			} catch (InterruptedException e)
			{
				log.info("ruleEngineTaskExecutor sleep error", e);
			}
		}
		log.info("===============end===============");
	}
}
