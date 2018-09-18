/**
 * 
 */
package com.foxconn.core.pro.server.rule.engine.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * kafka topic 消费类
 * @author lxy
 *
 */
@Component
@Slf4j
public class KafkaTopicConsumer {

	/**
	 * 消费 _data开头的 topic
	 * @param record
	 */
	@KafkaListener(topicPattern = "${kafka.consumer.base.topic}")
	//@KafkaListener(topicPattern = "test")
	public void listen(ConsumerRecord<?, ?> record) {
		log.error("threadName:" + Thread.currentThread().getName() + "topic = " + record.topic() + " ,offset = " + record.offset() + " ,value = " + record.value());
	}
	
}
