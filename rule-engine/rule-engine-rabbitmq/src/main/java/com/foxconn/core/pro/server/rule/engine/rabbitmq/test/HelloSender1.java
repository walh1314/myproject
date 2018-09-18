/**
 * Project Name:rule-engine-core
 * File Name:HelloSender1.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.test
 * Date:2018年8月28日下午3:51:43
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.rabbitmq.test;
/**
 * ClassName:HelloSender1 <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月28日 下午3:51:43 <br/>
 * @author   liupingan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
public class HelloSender1
{
	@Resource(name = "actionRabbitTemplate")
	private AmqpTemplate actionRabbitTemplate;

	public void send(String msg)
	{
		String sendMsg = msg;
		System.out.println("Sender1 : " + sendMsg);
		// rule.engine.to.action
		// rule.engine.queue
		// rule.engine.exchange
		/* rule.engine.action.queue
		  
		 rule.engine.action.exchange*/
		//this.actionRabbitTemplate.convertAndSend("rule.engine.source.queue", sendMsg);
		//this.actionRabbitTemplate.convertAndSend("rule.engine.source.exchange","rule.engine.source.queue", sendMsg);
		//this.actionRabbitTemplate.convertAndSend("rule.engine.action.exchange", "rule.engine.action.queue", sendMsg);
		this.actionRabbitTemplate.convertAndSend("rule.engine.action.exchange","rule.engine.to.action", sendMsg);
		//this.sourceRabbitTemplate.convertAndSend("rule_engine", "rule_engine.event", sendMsg);
	}

	@Resource(name = "sourceRabbitTemplate")
	private AmqpTemplate sourceRabbitTemplate;

	public void sendSource(String msg)
	{
		String sendMsg = msg;
		System.out.println("Sender1 : " + sendMsg);
		// rule.engine.to.action
		// rule.engine.queue
		// rule.engine.exchange
		/*
		 * exchange: rule.engine.action.exchange queue: rule.engine.action.queue
		 * topic: rule.engine.to.action source: address: 127.0.0.1:5672
		 * username: guest password: guest virtual-host: / exchange:
		 * rule.engine.source.exchange queue: rule.engine.source.queue topic:
		 * rule.engine.to.source
		 */
		this.sourceRabbitTemplate.convertAndSend("rule_engine", "rule_engine", sendMsg);
		this.sourceRabbitTemplate.convertAndSend("rule_engine", "rule_engine.event", sendMsg);
		this.sourceRabbitTemplate.convertAndSend("rule_engine", sendMsg);
	}

}
