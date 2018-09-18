package com.foxconn.core.pro.server.rule.engine.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * kafka 接入引擎入口
 * @author lxy
 *
 */
@SpringBootApplication
public class KafkaApplication {
	
	static {
		System.setProperty("java.security.auth.login.config", KafkaApplication.class.getResource("kafka_client_jaas.conf").getPath());
	}

	/**
	 * 启动 kafka 接入引擎
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

}
