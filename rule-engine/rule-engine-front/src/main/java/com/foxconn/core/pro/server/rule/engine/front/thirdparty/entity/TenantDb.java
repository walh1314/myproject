/**
 * Project Name:provider-demo
 * File Name:TenantDb.java
 * Package Name:com.tsf.demo.provider
 * Date:2018年9月11日上午8:46:01
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.front.thirdparty.entity;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

/**
 * ClassName:TenantDb <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年9月11日 上午8:46:01 <br/>
 * @author   liupingan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
@Setter
@Getter
public class TenantDb
{
	@JSONField(name="tenant_id")
	private Integer tenantId;
	
	@JSONField(name="database_type")
	private String databaseType;
	
	@JSONField(name="db_name")
	private String dbName;

}

