/**
 * Project Name:rule-engine-action
 * File Name:ActionBean.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.action.entity
 * Date:2018年8月29日上午8:26:24
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.action.entity;
/**
 * ClassName:ActionBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月29日 上午8:26:24 <br/>
 * @author   liupingan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

/**
 * 接收到Object对象 ClassName: ActionBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2018年8月29日 上午8:27:35 <br/>
 *
 * @author liupingan
 * @version
 * @since JDK 1.8
 */
@Setter
@Getter
public class ActionBean
{
	private String ruleId;
	private String dbName;
	private JSONObject data;
	private String dataId;
	private String type;

	@JSONField(name = "product_id")
	private String productId;

	@JSONField(name = "devicename")
	private String deviceName;

	@JSONField(name = "timestamp")
	private Long timestamp;
}
