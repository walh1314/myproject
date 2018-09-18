/**
 * Project Name:rule-engine-action
 * File Name:KafkaListener.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.action.listener
 * Date:2018年8月29日上午8:36:21
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.action.listener;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName:KafkaListener <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月29日 上午8:36:21 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@Component("kafkaListener")
@Slf4j
public class KafkaListener implements BaseListener
{
	@Override
	public void action(JSONObject parameter, JSONObject bean, JSONObject systemData)
	{
		// producer.send(record)
		KafkaProducer<String, String> producer = null;
		log.info("-------------- kafka start --------------");
		log.info("-------------- kafka parameter --------------" + parameter);
		log.info("-------------- kafka bean --------------" + bean);
		try
		{

			producer = init(parameter);
			ProducerRecord<String, String> msg = null;
			if (systemData != null && systemData.containsKey("way") && "foxconn".equals(systemData.getString("way")))
			{
				msg = new ProducerRecord<String, String>(parameter.getString("topic"),
						getFxoconnData(systemData, bean).toJSONString());
			} else
			{
				msg = new ProducerRecord<String, String>(parameter.getString("topic"), bean.toJSONString());
			}
			// 进行数据处理

			producer.send(msg);
			log.info("-------------- kafka normal end --------------");
		} catch (Exception e)
		{
			log.error("Kafaka Listener Exception:" + (parameter != null ? parameter.toJSONString() : "") + ","
					+ (bean != null ? bean.toJSONString() : ""), e);
		} finally
		{
			log.info("-------------- kafka end --------------");
			if (producer != null)
			{
				// 进行关闭
				producer.close(100, TimeUnit.MILLISECONDS);
			}
		}
	}

	private JSONObject getFxoconnData(JSONObject systemData, JSONObject data)
	{
		JSONObject result = new JSONObject();
		// result = new JSONObject();
		result.put("_product_id", systemData.get("product_id"));
		result.put("_devicename", systemData.get("devicename"));
		result.put("_timestamp", systemData.get("timestamp"));
		result.put("_topic", systemData.get("topic"));
		result.put("params", data);
		return result;
	}

	private KafkaProducer<String, String> init(JSONObject parameter)
	{
		Properties props = new Properties();
		// 初始化默认值
		props.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
		props.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		props.setProperty(ProducerConfig.RETRIES_CONFIG, "0");
		if (parameter != null)
		{
			for (Entry<String, Object> entry : parameter.entrySet())
			{
				// 排除topic字段
				if (entry.getValue() != null || !"topic".equals(entry.getKey()))
				{
					props.put(entry.getKey(), String.valueOf(entry.getValue()).trim());
				}
			}
		}
		// 测试数据配置
		/*
		 * parameter.put("topic", "sys.corepro.action");
		 * props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
		 * "192.168.1.233:9092"); props.put("key.serializer",
		 * "org.apache.kafka.common.serialization.StringSerializer");
		 * props.put("value.serializer",
		 * "org.apache.kafka.common.serialization.StringSerializer");
		 */ // 设置分区类,根据key进行数据分区
		return new KafkaProducer<String, String>(props);
	}

}
