/**
 * Project Name:rule-engine-action
 * File Name:HttpListener.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.action.listener
 * Date:2018年8月31日上午11:42:32
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.action.listener;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.core.pro.server.rule.engine.action.common.util.HttpClientUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName:HttpListener <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月31日 上午11:42:32 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@Component("httpListener")
@Slf4j
public class HttpListener implements BaseListener
{

	@Override
	public void action(JSONObject parameter, JSONObject bean,JSONObject systemData)
	{
		try
		{
			// HttpClientUtil
			Map<String, Object> headers = init();
			String url = parameter.getString("address");
			// String url = parameter.getString("address");
			// 进行post请求
			HttpClientUtil.httpPost(url, headers, bean.toJSONString());
		} catch (Exception e)
		{
			log.error("HTTP Listener Exception:" + (parameter != null ? parameter.toJSONString() : "") + ","
					+ (bean != null ? bean.toJSONString() : ""), e);
		}
	}

	public Map<String, Object> init()
	{
		Map<String, Object> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		return headers;
	}
}
