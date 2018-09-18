/**
 * Project Name:rule-engine-core
 * File Name:RabbitmqSubscribe.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.rabbit
 * Date:2018年8月28日上午8:53:18
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.action.rabbitmq;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.core.pro.server.rule.engine.action.common.exception.BaseException;
import com.foxconn.core.pro.server.rule.engine.action.constant.RedisConstant;
import com.foxconn.core.pro.server.rule.engine.action.entity.ActionBean;
import com.foxconn.core.pro.server.rule.engine.action.listener.BaseListener;
import com.foxconn.core.pro.server.rule.engine.action.thirdparty.common.service.CoreproCommonService;
import com.foxconn.core.pro.server.rule.engine.action.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitmqSubscribe <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月28日 上午8:53:18 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@Component
// @RabbitListener(queues = "${com.rule.engine.action.rabbitmq.queue}")
@Slf4j
public class RabbitmqConsumer
{

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	private CoreproCommonService coreproCommonService;

	/**
	 * action处理 action:(这里用一句话描述这个方法的作用). <br/>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 *
	 * @author liupingan
	 * @param msg
	 * @since JDK 1.8
	 */
	@RabbitListener(containerFactory = "pointTaskContainerFactory",bindings =
	{ @QueueBinding(value = @Queue(value = "${com.rule.engine.action.rabbitmq.queue}", autoDelete = "${com.rule.engine.action.rabbitmq.queue-auto-delete}", durable = "${com.rule.engine.action.rabbitmq.queue-durable}", exclusive = "${com.rule.engine.action.rabbitmq.exclusive}"), exchange = @Exchange(value = "${com.rule.engine.action.rabbitmq.exchange}", durable = "${com.rule.engine.action.rabbitmq.exchange-durable}", autoDelete = "${com.rule.engine.action.rabbitmq.exchange-auto-delete}", type = ExchangeTypes.TOPIC)) })

	public void action(@Header("amqp_receivedRoutingKey") String routeKey, org.springframework.amqp.core.Message data)
	{
		// 接收规则引擎传送的action数据
		try
		{
			// if(action)
			// 如果不为空,则进行处理
			// 查找redis缓存action应用
			// 进行分批处理，采用监听器模式
			log.info("-------------- action start --------------");
			log.info("-------------- action data --------------" + data != null ? JSONObject.toJSONString(data) : null);
			ActionBean actionBean = null;
			BaseListener listener = null;
			JSONArray actions = null;
			String msg = null;
			Object actionTypeId = null;
			Integer actionTypeIdI = null;
			if (data != null && StringUtils.isNotEmpty(new String(data.getBody())))
			{
				msg = new String(data.getBody());
				log.info("-------------- action msg --------------" + msg);
				actionBean = JSONObject.parseObject(msg, ActionBean.class);
				JSONObject systemData = null;
				String topic = null;
				if (actionBean != null)
				{
					actions = getActions(actionBean.getRuleId(), actionBean.getDbName());
					if (actions != null)
					{
						systemData = getSystemData(actionBean);
						// systemData.put("", value)
						for (int i = 0; i < actions.size(); i++)
						{
							log.info("-------------- action " + i + " --classtype------------"
									+ actions.getJSONObject(i).getString("classType"));
							// 监听事件
							listener = SpringUtil.getBean(actions.getJSONObject(i).getString("classType"),
									BaseListener.class);
							if (actions.getJSONObject(i).containsKey("actionTypeId"))
							{
								actionTypeId = actions.getJSONObject(i).get("actionTypeId");
								if (actionTypeId != null && actionTypeId instanceof Integer)
								{
									actionTypeIdI = (Integer) actionTypeId;
									// 如果为本地自己的kafaka，需要做特殊处理
									if (actionTypeIdI == 1)
									{
										topic = getTopic(actionBean.getDataId());
										actions.getJSONObject(i).getJSONObject("params").put("topic", topic);
										systemData.put("topic", topic);
										systemData.put("way", "foxconn");
									}
								}
							}
							// 获取参数类型
							listener.action(actions.getJSONObject(i).getJSONObject("params"), actionBean.getData(),
									systemData);
						}
					}
				}
			}
			log.info("-------------- action end --------------");
		} catch (BaseException e)
		{
			log.error("action recive base exception:" + data != null ? JSONObject.toJSONString(data) : null, e);
		} catch (Exception e)
		{
			log.error("action recive exception:" + data != null ? JSONObject.toJSONString(data) : null, e);
		}

	}

	/**
	 * 获取product的值 getSystemData:(这里用一句话描述这个方法的作用). <br/>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 *
	 * @author liupingan
	 * @param actionBean
	 * @return
	 * @since JDK 1.8
	 */
	private JSONObject getSystemData(ActionBean actionBean)
	{
		JSONObject systemData = new JSONObject();
		systemData.put("product_id", actionBean.getProductId());
		systemData.put("devicename", actionBean.getDeviceName());
		systemData.put("timestamp", actionBean.getTimestamp());
		return systemData;
	}

	/**
	 * getActions: 获取动作. <br/>
	 *
	 * @author liupingan
	 * @param ruleId
	 * @return
	 * @since JDK 1.8
	 */
	private JSONArray getActions(String ruleId, String dbName)
	{
		JSONArray result = null;
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		// 获取规则List
		if (dbName != null && StringUtils.isNotBlank(dbName))
		{
			String ruleActionStr = opsForValue
					.get(RedisConstant.RULE_ACTION_LIST + dbName.trim() + RedisConstant.RULE_ACTION_CONCAT + ruleId);
			if (ruleActionStr != null && StringUtils.isNotBlank(ruleActionStr))
			{
				result = JSONObject.parseArray(ruleActionStr);
			}
		}

		// 获取缓存的Action参数
		return result;
	}

	private String getTopic(String dataId)
	{
		if (dataId == null || StringUtils.isEmpty(dataId))
		{
			return null;
		}
		List<String> list = coreproCommonService.getTopic(dataId);
		if (list != null && list.size() == 1)
		{
			return list.get(0);
		}
		return null;
	}

}
