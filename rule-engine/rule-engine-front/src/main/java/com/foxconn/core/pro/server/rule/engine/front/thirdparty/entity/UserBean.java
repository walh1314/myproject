/**
 * Project Name:rule-engine-core
 * File Name:UserBean.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.front.thirdparty.entity
 * Date:2018年8月30日上午10:42:06
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.front.thirdparty.entity;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

/**
 * ClassName:UserBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月30日 上午10:42:06 <br/>
 * @author   liupingan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
@Setter
@Getter
public class UserBean implements Serializable
{
	/**
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么).
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = 2798974185258480040L;

	@JSONField(name="tenantId")
	private String tenantId;
	
	@JSONField(name="userId")
	private String userId;
	
	@JSONField(name="db")
	private String db;
	
	@JSONField(name="productId")
	private String productId;
}

