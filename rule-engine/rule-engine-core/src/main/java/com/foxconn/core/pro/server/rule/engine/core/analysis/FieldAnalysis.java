/**
 * Project Name:rule-engine-core
 * File Name:FieldAnalysis.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.analysis
 * Date:2018年8月23日下午5:31:35
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.core.analysis;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.core.pro.server.rule.engine.core.entity.Rule;
import com.foxconn.core.pro.server.rule.engine.core.entity.data.MqttData;
import com.foxconn.core.pro.server.rule.engine.core.express.SpringBeanRunner;
import com.foxconn.core.pro.server.rule.engine.core.sql.BaseSingleParser;

import lombok.Getter;
import lombok.Setter;

/**
 * ClassName:FieldAnalysis <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月23日 下午5:31:35 <br/>
 * @author   liupingan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
@Service
@Setter
@Getter
public class FieldAnalysis
{
	private List<Rule> rules;
	
	private MqttData data;
	
	@Resource
	private SpringBeanRunner runner;
	
	@Autowired
	@Qualifier("fieldParser")
	private BaseSingleParser baseSingleParser;
	
	public JSONObject excute(MqttData data, Map<String, Object>  context, String condition) throws Exception
	{
		JSONObject result = null;
		
		baseSingleParser.setOriginalValue(condition);
		//baseSingleParser.getQLExpress();
		
		String content = baseSingleParser.getQLExpress();
		result = (JSONObject)runner.executeExpress(content, context);
		return result;
		
	}
	
	public JSONObject excute(Map<String, Object>  context, String condition) throws Exception
	{
		JSONObject result = null;
		
		baseSingleParser.setOriginalValue(condition);
		//baseSingleParser.getQLExpress();
		
		String content = baseSingleParser.getQLExpress();
		
		result = (JSONObject)runner.executeExpress(content, context);
		return result;
		
	}
}
