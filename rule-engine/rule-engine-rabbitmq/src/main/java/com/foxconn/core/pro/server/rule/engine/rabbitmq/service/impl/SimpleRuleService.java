/**
 * Project Name:rule-engine-core
 * File Name:SimpleRuleService.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.service.impl
 * Date:2018年8月23日下午5:22:33
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.rabbitmq.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.core.pro.server.rule.engine.core.analysis.ConditionAnalysis;
import com.foxconn.core.pro.server.rule.engine.core.analysis.FieldAnalysis;
import com.foxconn.core.pro.server.rule.engine.core.analysis.TopicAnalysis;
import com.foxconn.core.pro.server.rule.engine.core.common.entity.ResultMap;
import com.foxconn.core.pro.server.rule.engine.core.common.exception.ErrorCodes;
import com.foxconn.core.pro.server.rule.engine.core.constant.RedisConstant;
import com.foxconn.core.pro.server.rule.engine.core.entity.Rule;
import com.foxconn.core.pro.server.rule.engine.core.entity.data.MqttData;
import com.foxconn.core.pro.server.rule.engine.core.entity.data.PayloadBean;
import com.foxconn.core.pro.server.rule.engine.core.express.SpringBeanRunner;
import com.foxconn.core.pro.server.rule.engine.core.thirdparty.clound.FrontService;
import com.foxconn.core.pro.server.rule.engine.core.thirdparty.common.service.CoreproCommonService;
import com.foxconn.core.pro.server.rule.engine.core.thirdparty.config.ServerParamConfig;
import com.foxconn.core.pro.server.rule.engine.core.thirdparty.entity.ConfigBean;
import com.foxconn.core.pro.server.rule.engine.core.thirdparty.entity.DataBean;
import com.foxconn.core.pro.server.rule.engine.core.thirdparty.entity.NoticeRedisBean;
import com.foxconn.core.pro.server.rule.engine.core.thirdparty.entity.UserBean;
import com.foxconn.core.pro.server.rule.engine.rabbitmq.rabbitmq.RabbitmqProducer;
import com.foxconn.core.pro.server.rule.engine.rabbitmq.service.IRuleService;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName:SimpleRuleService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月23日 下午5:22:33 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@Service
@Slf4j
public class SimpleRuleService implements IRuleService
{

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Resource
	private SpringBeanRunner runner;

	@Resource
	private ConditionAnalysis conditionAnalysis;

	@Resource
	private FieldAnalysis fieldAnalysis;

	@Resource
	private RabbitmqProducer rabbitmqProducer;

	@Resource
	private FrontService frontService;

	@Resource
	private ServerParamConfig serverParamConfig;

	// 用户信息
	/*
	 * @Autowired private AccountService accountService;
	 */

	@Autowired
	private CoreproCommonService coreproCommonService;

	public void excute(MqttData data) throws Exception
	{
		log.info("----------- rule excute start-----" + JSONObject.toJSONString(data));
		TopicAnalysis topicAnalysis = new TopicAnalysis();
		if (data == null || data.getPayload() == null || StringUtils.isEmpty(data.getPayload().getProductkey()))
		{
			log.info("rule engine is illege:" + JSONObject.toJSONString(data));
			return;
		}

		log.info("-----------getProductkey-----" + data.getPayload().getProductkey());
		UserBean userBean = this.getUserByProductKey(data.getPayload().getProductkey());
		log.info("-----------userBean-----" + userBean != null ? JSONObject.toJSONString(userBean) : "");
		if (userBean == null)
		{
			log.info("rule engine is illege:" + JSONObject.toJSONString(data));
			return;
		}
		// TODO 测试用例
		List<Rule> rules = getRuleListByUser(userBean, data);// 根据userId获取规则
		log.info("-----------rulesList-----" + rules != null ? JSONObject.toJSONString(rules) : null);
		// rules = getTestList();

		List<Rule> rulesTopicMatch = null;
		List<Rule> rulesDataMatch = null;
		try
		{
			rulesTopicMatch = topicAnalysis.getMatchRuleList(data.getTopic(), rules);

			log.info("-----------rulesTopicMatchrulesList-----" + rulesTopicMatch != null
					? JSONObject.toJSONString(rulesTopicMatch) : null);
			// 如果匹配成功,则查看条件是否符合
			Map<String, Object> context = null;
			if (rulesTopicMatch != null && rulesTopicMatch.size() > 0)
			{
				rulesDataMatch = new ArrayList<>(rulesTopicMatch.size() / 2);
				context = new HashMap<>(2);
				context.put("topic", data.getTopic());
				context.put("root", data.getPayload().getParams());// 根据协议去解析
				conditionAnalysis.setData(data);
				conditionAnalysis.setRules(rulesTopicMatch);
				rulesDataMatch = conditionAnalysis.getMatchRuleList(context);
			}
			// 如果有匹配成功的，则继续处理,进行数据筛选,数据筛选采用模板引擎处理
			String condition = null;
			JSONObject message = null;
			JSONObject dataAction = null;
			log.info("------------topic rule result-----");
			log.info("------------topic rule result-----" + rulesDataMatch != null
					? JSONObject.toJSONString(rulesDataMatch) : "");

			MqttData fieldData = null;
			PayloadBean payloadBean = null;
			JSONObject filedParams = null;
			JSONArray jsonArrayParams = null;
			if (rulesDataMatch != null && rulesDataMatch.size() > 0)
			{
				for (int i = 0; i < rulesDataMatch.size(); i++)
				{
					fieldData = new MqttData();
					payloadBean = new PayloadBean();
					if (data.getPayload().getParams() instanceof JSONObject)
					{
						filedParams = (JSONObject) data.getPayload().getParams();
						filedParams.put("_timestamp", data.getPayload().getTimestamp());
						filedParams.put("_productkey", data.getPayload().getProductkey());
						filedParams.put("_devicename", data.getPayload().getDeviceName());
						filedParams.put("_type", data.getType());
						filedParams.put("_accessMode", data.getAccessMode());
						payloadBean.setParams(filedParams);

						payloadBean.setDeviceName(data.getPayload().getDeviceName());
						payloadBean.setProductkey(data.getPayload().getProductkey());
						payloadBean.setTimestamp(data.getPayload().getTimestamp());

						fieldData.setPayload(payloadBean);

						fieldData.setId(data.getId());
						fieldData.setAccessMode(data.getAccessMode());

						fieldData.setTimestamp(data.getTimestamp());
						fieldData.setTopic(data.getTopic());
						fieldData.setType(data.getType());

						condition = rulesDataMatch.get(i).getField();
						context = new HashMap<>(2);
						context.put("topic", context);

						context.put("root", filedParams);
						dataAction = fieldAnalysis.excute(fieldData, context, condition);
						log.info("------------dataAction result-----" + i + "-------" + dataAction != null
								? JSONObject.toJSONString(dataAction) : "");
						if (dataAction != null)
						{
							message = new JSONObject();
							message.put("data", dataAction);
							message.put("ruleId", rulesDataMatch.get(i).getId());
							message.put("dbName", userBean.getDb());
							message.put("dataId", data.getDataId());
							message.put("type", data.getType());
							message.put("product_id", data.getPayload().getProductkey());
							message.put("devicename", data.getPayload().getDeviceName());
							message.put("product_id", data.getPayload().getProductkey());
							message.put("timestamp", data.getPayload().getTimestamp());
							rabbitmqProducer.publish(message.toJSONString());
						}
					} else if (data.getPayload().getParams() instanceof JSONArray)
					{
						jsonArrayParams = (JSONArray) data.getPayload().getParams();
						if (jsonArrayParams != null && jsonArrayParams.size() > 0)
						{
							for (int m = 0; m < jsonArrayParams.size(); m++)
							{
								filedParams = jsonArrayParams.getJSONObject(m);
								filedParams.put("_timestamp", data.getPayload().getTimestamp());
								filedParams.put("_productkey", data.getPayload().getProductkey());
								filedParams.put("_devicename", data.getPayload().getDeviceName());
								filedParams.put("_type", data.getType());
								filedParams.put("_accessMode", data.getAccessMode());
								payloadBean.setParams(filedParams);

								payloadBean.setDeviceName(data.getPayload().getDeviceName());
								payloadBean.setProductkey(data.getPayload().getProductkey());
								payloadBean.setTimestamp(data.getPayload().getTimestamp());

								fieldData.setPayload(payloadBean);

								fieldData.setId(data.getId());
								fieldData.setAccessMode(data.getAccessMode());

								fieldData.setTimestamp(data.getTimestamp());
								fieldData.setTopic(data.getTopic());
								fieldData.setType(data.getType());

								condition = rulesDataMatch.get(i).getField();
								context = new HashMap<>(2);
								context.put("topic", context);

								context.put("root", filedParams);
								dataAction = fieldAnalysis.excute(fieldData, context, condition);
								log.info("------------dataAction result-----" + i + "-------" + dataAction != null
										? JSONObject.toJSONString(dataAction) : "");
								if (dataAction != null)
								{
									message = new JSONObject();
									message.put("data", dataAction);
									message.put("ruleId", rulesDataMatch.get(i).getId());
									message.put("dbName", userBean.getDb());
									message.put("dataId", data.getDataId());
									message.put("type", data.getType());
									message.put("product_id", data.getPayload().getProductkey());
									message.put("devicename", data.getPayload().getDeviceName());
									message.put("product_id", data.getPayload().getProductkey());
									message.put("timestamp", data.getPayload().getTimestamp());
									rabbitmqProducer.publish(message.toJSONString());
								}
							}
						}

					}
				}
			} else
			{
				log.info("Match failure:", JSONObject.toJSONString(data));
			}
			log.info("----------- rule excute end-----");
		} catch (Exception e)
		{
			log.info("----------- Match exception-----" + e);
			log.error("Match exception:", e);
		}
	}

	private UserBean getUserByProductKey(String productKey)
	{
		List<UserBean> beans = coreproCommonService.getUserInfo(productKey);
		if (beans != null && beans.size() > 0)
		{
			UserBean bean = beans.get(0);
			if (bean != null)
			{
				return bean;
			}
		}
		return null;
	}

	private List<Rule> getRuleListByUser(UserBean bean, MqttData mqttData)
	{
		if (bean == null || ((bean.getDb() == null || StringUtils.isBlank(bean.getDb()))
				&& (bean.getUserId() == null || StringUtils.isBlank(bean.getUserId()))))
		{
			return null;
		}
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		// 获取规则List
		String ruleListStr = opsForValue.get(RedisConstant.RULE_LIST + bean.getDb());
		if (ruleListStr == null || StringUtils.isBlank(ruleListStr))
		{
			// 重新去获取数据
			NoticeRedisBean noticeRedisBean = new NoticeRedisBean();
			ConfigBean config = new ConfigBean();
			config.setUserId(bean.getUserId());
			DataBean data = new DataBean();
			data.setDbName(bean.getDb());
			noticeRedisBean.setConfig(config);
			noticeRedisBean.setData(data);

			JSONObject jsonObject = frontService.noticeRedis(noticeRedisBean);
			if (jsonObject != null)
			{
				log.info("redisResult is success" + jsonObject.toJSONString());
			}
		}
		// 不管成功还是失败，直接获取缓存
		if (ruleListStr == null || StringUtils.isBlank(ruleListStr))
		{
			return null;
		} else
		{
			ruleListStr = opsForValue.get(RedisConstant.RULE_LIST + bean.getDb());
		}
		List<Rule> rules = JSONObject.parseArray(ruleListStr, Rule.class);
		if (rules == null)
			return null;
		Rule rule = null;
		String topic = null;
		// 进行值替换
		for (int i = 0; i < rules.size(); i++)
		{
			rule = rules.get(i);
			topic = rule.getTopic();
			//如果接入方式存在，并且不一样，则直接返回
			if (mqttData.getAccessMode() != null && !mqttData.getAccessMode().equals(rule.getAccessMode())
					&& mqttData.getVersion() != null && !mqttData.getVersion().equals(rule.getVersion()))
			{
				continue;
			}
			// topic处理
			if (StringUtils.isNotEmpty(topic))
			{
				// 替换topic表达式
				topic = "^" + topic;
				topic = topic.replaceAll("\\+", "[^\\\\s\\/]+").replace("#", "[\\S]{0,}");
				topic = topic + "$";
			}
			rule.setTopic(topic);// 替换topic值
		}
		return rules;
	}

	public ResultMap<? extends Object> debug(JSONObject data)
	{
		ResultMap<JSONObject> result = new ResultMap<>();
		if (data == null || !data.containsKey("fields") || !data.containsKey("topic") || !data.containsKey("data"))
		{
			result.setCode(ErrorCodes.RULE_PARAM_EMPT.getCode());
			result.setMsg(ErrorCodes.RULE_PARAM_EMPT.getDesc());
			return result;
		}

		JSONObject sourceData = data.getJSONObject("data");

		String dataTopic = null;
		JSONObject dataParams = null;
		if (sourceData != null)
		{
			dataTopic = sourceData.getString("topic");
			dataParams = sourceData.getJSONObject("params");
		}
		if (StringUtils.isEmpty(dataTopic) || dataParams == null)
		{
			result.setCode(ErrorCodes.RULE_PARAM_EMPT.getCode());
			result.setMsg(ErrorCodes.RULE_PARAM_EMPT.getDesc());
			return result;
		}

		// topi处理
		String topic = data.getString("topic");
		// topic处理
		if (StringUtils.isNotEmpty(topic))
		{
			// 替换topic表达式
			topic = "^" + topic;
			topic = topic.replaceAll("\\+", "[^\\s\\/]+").replace("#", "[\\S]{0,}");
			topic = topic + "$";
		} else
		{
			result.setCode(ErrorCodes.RULE_PARAM_EMPT.getCode());
			result.setMsg(ErrorCodes.RULE_PARAM_EMPT.getDesc());
		}
		TopicAnalysis topicAnalysis = new TopicAnalysis();
		boolean isMatch = topicAnalysis.isMatchRule(dataTopic, topic);
		if (!isMatch)
		{
			return result;
		}
		// 进行数据处理

		String condition = null;
		if (data.containsKey("condition"))
		{
			condition = data.getString("condition");
		}

		Map<String, Object> context = null;
		if (StringUtils.isEmpty(condition))
		{

		} else
		{
			context = new HashMap<>(2);
			context.put("topic", context);
			context.put("root", dataParams);// 根据协议去解析
			isMatch = conditionAnalysis.isMatchRule(context, condition);
		}

		if (!isMatch)
		{
			return result;
		}

		String fileds = data.getString("fields");
		context = new HashMap<>(2);
		context.put("topic", context);
		context.put("root", dataParams);// 根据协议去解析
		JSONObject dataAction = null;
		try
		{
			dataAction = fieldAnalysis.excute(context, fileds);
			result.setData(dataAction);
		} catch (Exception e)
		{
			log.error("rule debug Exception", e);
		}
		log.info("result");
		log.info(dataAction == null ? null : dataAction.toString());
		return result;
	}
}
