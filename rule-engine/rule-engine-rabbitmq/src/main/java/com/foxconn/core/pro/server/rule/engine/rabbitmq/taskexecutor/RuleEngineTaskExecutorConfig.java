package com.foxconn.core.pro.server.rule.engine.rabbitmq.taskexecutor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "com.mqtt.task")
@Setter
@Getter
public class RuleEngineTaskExecutorConfig
{
	private Integer corePoolSize = 5;
	
	private Integer keepAliveSeconds = 30000;
	
	private Integer maxPoolSize = 100;
	
	private Integer queueCapacity = 200;
	
	private Integer percentage = 90;
	
	private Integer sleepTime = 2;
	
}
