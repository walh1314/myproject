/**
 * Project Name:rule-engine-core
 * File Name:PayloadBean.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.mqtt
 * Date:2018年8月29日下午3:01:31
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.core.entity.data;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

/**
 * ClassName:PayloadBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月29日 下午3:01:31 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@Setter
@Getter
public class PayloadBean implements Serializable
{
	/**
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = -7374153082576916101L;
	
	private String timestamp;
	
	@JSONField(name = "productKey")
	private String productkey;
	
	@JSONField(name = "devicename")
	private String deviceName;
	
	private JSON params;
}
