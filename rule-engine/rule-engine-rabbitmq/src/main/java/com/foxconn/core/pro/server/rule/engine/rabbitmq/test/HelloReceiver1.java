/**
 * Project Name:rule-engine-core
 * File Name:HelloReceiver1.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.test
 * Date:2018年8月28日下午3:51:19
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.rabbitmq.test;

import org.springframework.stereotype.Component;

@Component
//@RabbitListener(queues = CommonConstant.RABBITMQ_QUEUE+"1111")
public class HelloReceiver1
{
	//@RabbitHandler
	public void process(String hello)
	{
		System.out.println("Receiver1  : " + hello);
	}

}
