/**
 * Project Name:rule-engine-core
 * File Name:MqttData.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.mqtt
 * Date:2018年8月23日下午2:07:23
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.core.entity.data;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

/**
 * ClassName:MqttData <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月23日 下午2:07:23 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@Setter
@Getter
public class MqttData implements Serializable
{
	/**
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = 5141894496011700041L;

	private PayloadBean payload;

	private String topic;

	@JSONField(name = "dataid")
	private String dataId;

	private String id;

	private Long timestamp;

	private String type;

	//0 mqtt 1http
	private Integer accessMode;
	
	//0基础    1加强
	private Integer version;
}
