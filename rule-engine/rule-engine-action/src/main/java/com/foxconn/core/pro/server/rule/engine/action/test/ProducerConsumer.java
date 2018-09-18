/**
 * Project Name:rule-engine-action
 * File Name:ProducerConsumer.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.action.test
 * Date:2018年9月13日下午2:20:29
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.action.test;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * ClassName:ProducerConsumer <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年9月13日 下午2:20:29 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
public class ProducerConsumer
{
	public static void main(String[] args) throws IOException
	{

		/*
		 * //211.159.183.125:9092, 139.199.77.21:9092, 118.89.37.185:9092
		 * //props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
		 * "192.168.1.233:9092");
		 * props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
		 * "211.159.183.125:9092, 139.199.77.21:9092, 118.89.37.185:9092");
		 * props.put("group.id", "test01"); //props.put("client.id", "test");
		 * //props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
		 * "3"); props.put("enable.auto.commit", true);// 显示设置偏移量自动提交
		 * props.put("auto.commit.interval.ms", 1000);// 设置偏移量提交时间间隔
		 * props.put("key.deserializer",
		 * "org.apache.kafka.common.serialization.StringDeserializer");
		 * props.put("value.deserializer",
		 * "org.apache.kafka.common.serialization.StringDeserializer");
		 */
		Properties props = new Properties();

		//props.put("bootstrap.servers", "192.168.1.233:9092");
		props.put("bootstrap.servers", "211.159.183.125:9092, 139.199.77.21:9092, 118.89.37.185:9092");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");// 16M
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		/**
		 * 两个泛型参数 第一个泛型参数：指的就是kafka中一条记录key的类型 第二个泛型参数：指的就是kafka中一条记录value的类型
		 */
		String[] girls = new String[]
		{ "姚慧莹", "刘向前", "周  新", "杨柳" };
		Producer<String, String> producer = new KafkaProducer<String, String>(props);

		// consumer.subscribe(Arrays.asList("sys.corepro.action"));// 订阅主题
		//String topic = "sys.corepro.action";// props.getProperty(Constants.KAFKA_PRODUCER_TOPIC);
		String topic = "test_123";// props.getProperty(Constants.KAFKA_PRODUCER_TOPIC);
		String key = "1";
		String value = "今天的姑娘们很美111";
		ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topic, key, value);
		producer.send(producerRecord);
		producer.close();
	}

}
