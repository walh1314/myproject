/**
 * Project Name:rule-engine-front
 * File Name:RuleRedisService.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.front.common
 * Date:2018年9月1日下午1:54:00
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.front.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.core.pro.server.rule.engine.front.common.cache.entity.ActionCache;
import com.foxconn.core.pro.server.rule.engine.front.common.cache.entity.RuleCache;
import com.foxconn.core.pro.server.rule.engine.front.common.constant.RedisConstant;
import com.foxconn.core.pro.server.rule.engine.front.common.entity.ResultMap;
import com.foxconn.core.pro.server.rule.engine.front.common.util.SystemUtil;
import com.foxconn.core.pro.server.rule.engine.front.dto.InputMap;
import com.foxconn.core.pro.server.rule.engine.front.dto.NoticeDto;
import com.foxconn.core.pro.server.rule.engine.front.entity.ActionType;
import com.foxconn.core.pro.server.rule.engine.front.entity.Actions;
import com.foxconn.core.pro.server.rule.engine.front.entity.RuleEngine;
import com.foxconn.core.pro.server.rule.engine.front.exception.BaseException;
import com.foxconn.core.pro.server.rule.engine.front.mapper.ActionTypeMapper;
import com.foxconn.core.pro.server.rule.engine.front.mapper.RuleEngineMapper;
import com.foxconn.core.pro.server.rule.engine.front.thirdparty.entity.TenantDb;

/**
 * 规则缓存服务 ClassName:RuleRedisService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年9月1日 下午1:54:00 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@Component
@Scope("singleton")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor =
{ Exception.class, BaseException.class, RuntimeException.class, Throwable.class })
public class RuleRedisService
{
	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	private ActionTypeMapper actionTypeMapper;

	@Autowired
	private RuleEngineMapper ruleEngineMapper;

	@Autowired
	private SystemUtil systemUtil;

	/**
	 * updateUserRule: 更新用户rule缓存. <br/>
	 * 
	 * @author liupingan
	 * @param userId
	 * @since JDK 1.8
	 */
	public void updateUserRule(String deleteRuleId)
	{
		
		Map<String, Object> map = new HashMap<>(1);
		map.put("status", 1);
		// 只查询有效的规则引擎
		List<RuleEngine> rules = ruleEngineMapper.selectByMapOrderbyCreator(map);
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		// 查询所有动作类型
		List<ActionType> actionTypes = actionTypeMapper.selectAll();
		Map<Integer, ActionType> actionTypeMap = new HashMap<>();
		if (actionTypes != null && actionTypes.size() > 0)
		{
			actionTypeMap = getNameAccountMap(actionTypes);
		}
		cacheRule(rules, opsForValue, actionTypeMap,deleteRuleId);
	}

	public ResultMap<? extends Object> updateUserRule(InputMap<NoticeDto> bean)
	{
		ResultMap<? extends Object> result = new ResultMap<>();
		/*
		 * if(bean.getConfig() == null || bean.getConfig().getUserId() == null
		 * || bean.getConfig().getUserId() == null ){
		 * result.setCode(ErrorCodes.FAILED.getCode());
		 * result.setMsg(ErrorCodes.FAILED.getDesc()); return result; }
		 */
		// 如果有传入db，则直接用此db，否则
		if (bean != null && bean.getData() != null && bean.getData().getDbName() != null
				&& StringUtils.isNotBlank(bean.getData().getDbName()))
		{
			TenantDb tenantDb = new TenantDb();
			tenantDb.setDbName(bean.getData().getDbName());
			tenantDb.setTenantId(bean.getData().getTenantId());
			systemUtil.setTenantDb(tenantDb);
		} else
		{
			systemUtil.setTenantDb(bean.getConfig());
		}
		Map<String, Object> map = new HashMap<>(1);
		map.put("status", 1);
		// map.put("creator", userId);
		// 只查询有效的规则引擎
		List<RuleEngine> rules = ruleEngineMapper.selectByMapOrderbyCreator(map);
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		// 查询所有动作类型
		List<ActionType> actionTypes = actionTypeMapper.selectAll();
		Map<Integer, ActionType> actionTypeMap = new HashMap<>();
		if (actionTypes != null && actionTypes.size() > 0)
		{
			actionTypeMap = getNameAccountMap(actionTypes);
		}
		cacheRule(rules, opsForValue, actionTypeMap,null);
		return result;
	}

	/**
	 * initRule: 初始化rule缓存. <br/>
	 *
	 * @author liupingan
	 * @since JDK 1.8
	 */
	// 删除初始化加载规则
	// @PostConstruct
	public void initRule()
	{
		Map<String, Object> map = new HashMap<>(1);
		map.put("status", 1);
		// 只查询有效的规则引擎
		List<RuleEngine> rules = ruleEngineMapper.selectByMapOrderbyCreator(map);
		ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
		// 查询所有动作类型
		List<ActionType> actionTypes = actionTypeMapper.selectAll();
		Map<Integer, ActionType> actionTypeMap = new HashMap<>();
		if (actionTypes != null && actionTypes.size() > 0)
		{
			actionTypeMap = getNameAccountMap(actionTypes);
		}
		cacheRule(rules, null, opsForValue, actionTypeMap);
	}

	/**
	 * cacheRule:(设置当前库所有缓存). <br/>
	 * 
	 * @author liupingan
	 * @param rules
	 * @param opsForValue
	 * @param actionTypeMap
	 * @since JDK 1.8
	 */
	private void cacheRule(List<RuleEngine> rules, ValueOperations<String, String> opsForValue,
			Map<Integer, ActionType> actionTypeMap,String deleteRuleId)
	{
		List<RuleCache> listRule = null;
		TenantDb tenantDb = systemUtil.getTenantDb();
		String dbName = "";
		if (tenantDb != null && tenantDb.getDbName() != null && StringUtils.isNotBlank(tenantDb.getDbName()))
		{
			dbName =tenantDb.getDbName().trim();
		}
		if (rules != null && rules.size() > 0)
		{
			// 获取规则List
			RuleCache ruleCache = null;

			if (listRule == null)
			{
				listRule = new ArrayList<>(rules.size());
			}

			for (RuleEngine rule : rules)
			{
				setActionCache(rule.getActions(), rule.getId(), actionTypeMap, opsForValue,dbName);

				ruleCache = ruleEngineToRuleCache(rule);
				ruleCache.setModifyTime(rule.getModifyTime());
				listRule.add(ruleCache);
				// 进行缓存处理
			}
		}
		if(deleteRuleId != null && StringUtils.isNotBlank(deleteRuleId)){
			stringRedisTemplate.delete(stringRedisTemplate.keys(RedisConstant.RULE_ACTION_LIST +dbName+RedisConstant.RULE_ACTION_CONCAT+deleteRuleId));
		}
		if(listRule != null){
			opsForValue.set(RedisConstant.RULE_LIST + dbName, JSONObject.toJSONString(listRule));
		} else{
			stringRedisTemplate.delete(RedisConstant.RULE_LIST + dbName);
			//模糊删除掉所有的，如果为空的话
			stringRedisTemplate.delete(stringRedisTemplate.keys(RedisConstant.RULE_ACTION_LIST +dbName+RedisConstant.RULE_ACTION_CONCAT+"*"));
			//stringRedisTemplate.delete(RedisConstant.RULE_LIST + dbName);
		}
		
	}

	@SuppressWarnings("unused")
	private void cacheRule(List<RuleEngine> rules, String userId, ValueOperations<String, String> opsForValue,
			Map<Integer, ActionType> actionTypeMap)
	{
		List<RuleCache> listRule = null;
		if (rules != null && rules.size() > 0)
		{
			// 获取规则List
			RuleCache ruleCache = null;

			for (RuleEngine rule : rules)
			{
				setActionCache(rule.getActions(), rule.getId(), actionTypeMap, opsForValue);
				if (listRule == null)
				{
					listRule = new ArrayList<>(20);
				}
				if (userId == null)
				{
					userId = rule.getCreator().trim();
				}
				if (userId.equals(rule.getCreator()))
				{
					ruleCache = ruleEngineToRuleCache(rule);
					ruleCache.setModifyTime(rule.getModifyTime());
					listRule.add(ruleCache);
				} else
				{
					// 进行缓存处理
					opsForValue.set(RedisConstant.RULE_LIST + rule.getCreator().trim(),
							JSONObject.toJSONString(listRule));
					// 如果不同，则需要进行下次处理
					userId = rule.getCreator().trim();
					listRule = new ArrayList<>(20);

					ruleCache = ruleEngineToRuleCache(rule);
					ruleCache.setModifyTime(rule.getModifyTime());

					listRule.add(ruleCache);
				}
			}
		}

		// 最后进行缓存处理
		if (listRule != null)
		{
			opsForValue.set(RedisConstant.RULE_LIST + userId.trim(),JSONObject.toJSONString(listRule));
		} else if (userId != null)
		{
			if(listRule != null){
				opsForValue.set(RedisConstant.RULE_LIST + userId.trim(),JSONObject.toJSONString(listRule));
			} else{
				stringRedisTemplate.delete(RedisConstant.RULE_LIST + userId.trim());
			}
			
		}
	}
	
	private void setActionCache(List<Actions> actions, Integer ruleId, Map<Integer, ActionType> actionTypeMap,
			ValueOperations<String, String> opsForValue,String dbName)
	{
		List<ActionCache> result = null;
		if (actions != null)
		{
			ActionCache actionCache = null;
			ActionType actionType = null;
			JSONObject actionParams = null;
			JSONObject params = null;
			JSONObject defalutParams = null;
			result = new ArrayList<>(actions.size());
			for (Actions action : actions)
			{
				actionCache = new ActionCache();
				actionParams = action.getActionParam();
				if (actionTypeMap.containsKey(action.getActionTypeId()))
				{
					defalutParams = null;
					actionType = actionTypeMap.get(action.getActionTypeId());
					if (actionType != null)
					{
						actionCache.setClassType(actionType.getClassType());
						defalutParams = actionType.getDefaultParams();
					}
				}
				// 设置缓存时间
				actionCache.setModifyTime(action.getModifyTime());
				params = mergeJSONObject(defalutParams, actionParams);
				actionCache.setParams(params);
				actionCache.setId(action.getId());
				actionCache.setActionTypeId(action.getActionTypeId());
				result.add(actionCache);
			}
		}
		if(result != null){
			opsForValue.set(RedisConstant.RULE_ACTION_LIST +dbName+RedisConstant.RULE_ACTION_CONCAT+ruleId, JSONObject.toJSONString(result));
		} else {
			stringRedisTemplate.delete(RedisConstant.RULE_ACTION_LIST +dbName+RedisConstant.RULE_ACTION_CONCAT+ruleId);
		}
	}

	private void setActionCache(List<Actions> actions, Integer ruleId, Map<Integer, ActionType> actionTypeMap,
			ValueOperations<String, String> opsForValue)
	{
		List<ActionCache> result = null;
		if (actions != null)
		{
			ActionCache actionCache = null;
			ActionType actionType = null;
			JSONObject actionParams = null;
			JSONObject params = null;
			JSONObject defalutParams = null;
			result = new ArrayList<>(actions.size());
			for (Actions action : actions)
			{
				actionCache = new ActionCache();
				actionParams = action.getActionParam();
				if (actionTypeMap.containsKey(action.getActionTypeId()))
				{
					defalutParams = null;
					actionType = actionTypeMap.get(action.getActionTypeId());
					if (actionType != null)
					{
						actionCache.setClassType(actionType.getClassType());
						defalutParams = actionType.getDefaultParams();
					}
				}
				// 设置缓存时间
				actionCache.setModifyTime(action.getModifyTime());
				params = mergeJSONObject(defalutParams, actionParams);
				actionCache.setParams(params);
				actionCache.setId(action.getId());
				actionCache.setActionTypeId(action.getActionTypeId());
				result.add(actionCache);
			}
		}
		if(result != null){
			opsForValue.set(RedisConstant.RULE_ACTION_LIST + ruleId, JSONObject.toJSONString(result));
		} else {
			stringRedisTemplate.delete(RedisConstant.RULE_ACTION_LIST + ruleId);
		}
		
	}

	@SuppressWarnings("unchecked")
	private JSONObject mergeJSONObject(JSONObject bean01, JSONObject bean02)
	{
		if (bean01 == null)
		{
			return bean02;
		}
		if (bean02 == null)
		{
			return bean01;
		}
		Map<String, Object> map1 = JSONObject.parseObject(bean01.toJSONString(), Map.class);
		Map<String, Object> map2 = JSONObject.parseObject(bean02.toJSONString(), Map.class);
		Map<String, Object> map3 = new HashMap<>();
		map3.putAll(map1);
		map3.putAll(map2);
		return new JSONObject(map3);

	}

	/**
	 * 
	 * ruleEngineToRuleCache:进行属性转换. <br/>
	 *
	 * @author liupingan
	 * @param bean
	 * @return
	 * @since JDK 1.8
	 */
	private RuleCache ruleEngineToRuleCache(RuleEngine bean)
	{
		RuleCache result = new RuleCache();
		BeanUtils.copyProperties(bean, result);
		if (bean.getSql() != null)
		{
			result.setField(bean.getSql().getFields());
			result.setTopic(bean.getSql().getTopic());
			result.setCondition(bean.getSql().getCondition());
			result.setAccessMode(bean.getAccessMode());
			result.setVersion(bean.getVersion());
		}
		return result;
	}

	/**
	 * getNameAccountMap:( Action Type list转map集合). <br/>
	 * 
	 * @author liupingan
	 * @param accounts
	 * @return
	 * @since JDK 1.8
	 */
	private Map<Integer, ActionType> getNameAccountMap(List<ActionType> accounts)
	{
		return accounts.stream().collect(Collectors.toMap(ActionType::getId, Function.identity()));
	}

}
