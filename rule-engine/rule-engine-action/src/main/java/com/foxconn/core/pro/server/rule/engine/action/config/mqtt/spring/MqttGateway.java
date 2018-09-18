package com.foxconn.core.pro.server.rule.engine.action.config.mqtt.spring;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway
{
	void sendToMqtt(String data);

	void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

	void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);
}