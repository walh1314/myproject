package com.foxconn.core.pro.server.rule.engine.action.config.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "com.mqtt")
@Getter
@Setter
public class MqttProperties
{
	private MqttOutboundProperties outbound;

}
