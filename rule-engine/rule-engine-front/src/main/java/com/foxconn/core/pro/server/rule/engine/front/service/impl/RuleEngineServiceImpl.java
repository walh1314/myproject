/**
 * Project Name:rule-engine-front
 * File Name:InsertRuleServiceImpl.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.front.service.impl
 * Date:2018年8月24日下午2:47:18
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.front.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.foxconn.core.pro.server.rule.engine.core.analysis.ConditionAnalysis;
import com.foxconn.core.pro.server.rule.engine.core.analysis.FieldAnalysis;
import com.foxconn.core.pro.server.rule.engine.core.analysis.TopicAnalysis;
import com.foxconn.core.pro.server.rule.engine.front.common.RuleRedisService;
import com.foxconn.core.pro.server.rule.engine.front.common.entity.FrontPage;
import com.foxconn.core.pro.server.rule.engine.front.common.entity.PageResult;
import com.foxconn.core.pro.server.rule.engine.front.common.entity.ResultMap;
import com.foxconn.core.pro.server.rule.engine.front.common.util.CodeUtil;
import com.foxconn.core.pro.server.rule.engine.front.common.util.SystemUtil;
import com.foxconn.core.pro.server.rule.engine.front.dto.ActionsDto;
import com.foxconn.core.pro.server.rule.engine.front.dto.InputMap;
import com.foxconn.core.pro.server.rule.engine.front.dto.RuleDebugDto;
import com.foxconn.core.pro.server.rule.engine.front.dto.RuleEngineDto;
import com.foxconn.core.pro.server.rule.engine.front.entity.Actions;
import com.foxconn.core.pro.server.rule.engine.front.entity.RuleEngine;
import com.foxconn.core.pro.server.rule.engine.front.entity.Sql;
import com.foxconn.core.pro.server.rule.engine.front.entity.UserInfo;
import com.foxconn.core.pro.server.rule.engine.front.exception.BaseException;
import com.foxconn.core.pro.server.rule.engine.front.exception.ErrorCodes;
import com.foxconn.core.pro.server.rule.engine.front.mapper.ActionsMapper;
import com.foxconn.core.pro.server.rule.engine.front.mapper.RuleEngineMapper;
import com.foxconn.core.pro.server.rule.engine.front.service.ActionTypeService;
import com.foxconn.core.pro.server.rule.engine.front.service.RuleEngineService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.jdbc.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName:InsertRuleServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月24日 下午2:47:18 <br/>
 * 
 * @author hewanwan
 * @version
 * @since JDK 1.8
 * @see
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor =
{ Exception.class, BaseException.class, RuntimeException.class, Throwable.class })
@Slf4j
public class RuleEngineServiceImpl implements RuleEngineService
{
	@Autowired
	private SystemUtil systemUtil;

	@Autowired
	private RuleEngineMapper ruleEngineAddMapper;
	@Autowired
	private ActionsMapper actionsMapper;

	@Autowired
	private ActionTypeService actionTypeService;


	@Autowired
	private RuleRedisService ruleRedisService;
	
	@Resource
	private ConditionAnalysis conditionAnalysis;
	
	@Resource
	private FieldAnalysis fieldAnalysis;

	/** 分页查询 */
	@Override
	public ResultMap<PageResult<RuleEngine>> selectPage(FrontPage<RuleEngine> page, InputMap<RuleEngineDto> inputMap)
			throws Exception, JsonMappingException, IOException
	{
		ResultMap<PageResult<RuleEngine>> result = new ResultMap<>();
		// 判断是否合法 userId与创建人是否一致
		boolean flag = this.isExitsUser(inputMap);//用户是否存在，如果不存在，直接报错
		
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		
		Map<String, Object> map = new HashMap<>(3);
		RuleEngineDto bean = inputMap.getData();
		
		if (bean.getPageSize()==null)
		{
			result.setCode(ErrorCodes.RULE_ENGINE_PAGESIZE_EMPTY.getCode());
			result.setMsg(ErrorCodes.RULE_ENGINE_PAGESIZE_EMPTY.getDesc());
			return result;
		}
		if (bean.getCurrentPage()==null)
		{
			result.setCode(ErrorCodes.RULE_ENGINE_CURRENTPAGE_EMPTY.getCode());
			result.setMsg(ErrorCodes.RULE_ENGINE_CURRENTPAGE_EMPTY.getDesc());
			return result;
		}
		/*if (bean.getStatus()==null)
		{
			result.setCode(ErrorCodes.RULE_ENGINE_STATUS_EMPTY.getCode());
			result.setMsg(ErrorCodes.RULE_ENGINE_STATUS_EMPTY.getDesc());
			return result;
		}*/
		
		if(bean != null){
			BeanUtils.copyProperties(bean, page);
			if (!StringUtils.isNullOrEmpty(bean.getName()))
			{
				map.put("name", bean.getName());
			}
			if (!StringUtils.isNullOrEmpty(String.valueOf(bean.getDataType())))
			{
				map.put("status", bean.getStatus());
			}
			if (!StringUtils.isNullOrEmpty(String.valueOf(bean.getDataType())))
			{
				map.put("dataType", bean.getDataType());
			}
		}
		//设置创建人
		map.put("creator", inputMap.getConfig().getUserId());
		PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
		List<RuleEngine> pageList = ruleEngineAddMapper.selectByMap(map);
		PageInfo<RuleEngine> pageInfo = new PageInfo<>(pageList);
		PageResult<RuleEngine> pageResult = new PageResult<>(pageInfo);
		result.setData(pageResult);
		return result;
	}

	private boolean isExitsUser(InputMap<? extends Object> input){
		if(input == null ){
			return false;
		} 
		
		UserInfo currentUser = input.getConfig();
		if(currentUser == null ) {
			return false;
		}
		if(StringUtils.isNullOrEmpty(currentUser.getUserId())){
			return false;
		}
		return true;
	}
	/** 判断用户和创建者是否一致 */
	public boolean validationUser(UserInfo currentUser,String user)
	{
		if(currentUser == null || StringUtils.isNullOrEmpty(currentUser.getUserId()) ||  StringUtils.isNullOrEmpty(user)){
			return false;
		} else if(!currentUser.getUserId().trim().equals(user.trim())){
			return false;
		}
		return true;
	}

	/** 增加功能引擎 */
	@Override
	public ResultMap<? extends Object> insert(InputMap<RuleEngineDto> inputMap)
			throws Exception, JsonMappingException, IOException
	{
		boolean flag = this.isExitsUser(inputMap);//用户是否存在，如果不存在，直接报错
		
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		ResultMap<? extends Object> result = new ResultMap<>();
		RuleEngineDto ruleEngineDto = inputMap.getData();
		// 设置错误编码和描述
		validationBooleanIsNull(ruleEngineDto);

		RuleEngine insertRule = new RuleEngine();
		BeanUtils.copyProperties(ruleEngineDto, insertRule);
		Sql sql = ruleEngineDto.getSql();
		insertRule.setSqlString(JSON.toJSONString(sql));
		List<ActionsDto> actionsDtolist = ruleEngineDto.getActions();
		// 设置用户信息
		systemUtil.setCreaterAndModifier(insertRule, inputMap.getConfig(), true);
		// CodeUtil 产生随机编码，加入规则引擎
		insertRule.setCode(CodeUtil.getCode());
		Integer updateResult = ruleEngineAddMapper.insert(insertRule);
		if (updateResult != 1)
		{
			throw new BaseException(ErrorCodes.RULE_ENGINE_ADD_FAIL);
		}
		if(actionsDtolist != null){
			for (ActionsDto actionsDto : actionsDtolist)
			{
				if (actionsDto.getActionTypeId()==null)
				{
					result.setCode(ErrorCodes.RULE_ENGINE_ACTIONTYPEID_EMPTY.getCode());
					result.setMsg(ErrorCodes.RULE_ENGINE_ACTIONTYPEID_EMPTY.getDesc());
					return result;
				}
				/*if (actionsDto.getActionParam().isEmpty())
				{
					result.setCode(ErrorCodes.RULE_ENGINE_ACTIONPARAM_EMPTY.getCode());
					result.setMsg(ErrorCodes.RULE_ENGINE_ACTIONPARAM_EMPTY.getDesc());
					return result;
				}*/
				Actions actions = new Actions();
				actions.setActionTypeId(actionsDto.getActionTypeId());
				actions.setActionParamString(actionsDto.getActionParam() != null ?JSON.toJSONString(actionsDto.getActionParam()):null);
				actions.setRuleId(insertRule.getId());
				// 检查是否合法
				if (actionTypeService.selectById(actions.getActionTypeId()) == null)
				{
					throw new BaseException(ErrorCodes.RULE_TYPE_NOT_EXIST);
				}
				// 设置用户信息
				systemUtil.setCreaterAndModifier(actions, inputMap.getConfig(), true);
				actionsMapper.insert(actions);
			}
		}
		
		UserInfo currentUser = inputMap.getConfig();
		if (currentUser != null)
		{
			String deleteRuleId = null;
			ruleRedisService.updateUserRule(deleteRuleId);
		}
		return result;
	}
	
	public boolean validationBooleanIsNull(RuleEngineDto ruleEngineDto) {
		
		if (StringUtils.isNullOrEmpty(ruleEngineDto.getName()))
		{
			throw new BaseException(ErrorCodes.RULE_ENGINE_NAME_EMPTY.getCode(),ErrorCodes.RULE_ENGINE_NAME_EMPTY.getDesc());
		}
		if (ruleEngineDto.getDataType() == null)
		{
			throw new BaseException(ErrorCodes.RULE_ENGINE_DATATYPE_EMPTY.getCode(),ErrorCodes.RULE_ENGINE_DATATYPE_EMPTY.getDesc());
		}
		/*if(ruleEngineDto.getVersion() == null) {
			throw new BaseException(ErrorCodes.RULE_ENGINE_VERSION_EMPTY.getCode(),ErrorCodes.RULE_ENGINE_VERSION_EMPTY.getDesc());
		}	
		if(ruleEngineDto.getAccessMode() == null) {
			throw new BaseException(ErrorCodes.RULE_ENGINE_ACCESSMODE_EMPTY.getCode(),ErrorCodes.RULE_ENGINE_ACCESSMODE_EMPTY.getDesc());
		}
		*/
		if (StringUtils.isNullOrEmpty(ruleEngineDto.getSql().getFields()))
		{
			throw new BaseException(ErrorCodes.RULE_ENGINE_FIELDS_EMPTY.getCode(),ErrorCodes.RULE_ENGINE_FIELDS_EMPTY.getDesc());
		}
		/*if (StringUtils.isNullOrEmpty(ruleEngineDto.getSql().getCondition()))
		{
			throw new BaseException(ErrorCodes.RULE_ENGINE_CONDITION_EMPTY.getCode(),ErrorCodes.RULE_ENGINE_CONDITION_EMPTY.getDesc());
		}*/
		if (StringUtils.isNullOrEmpty(ruleEngineDto.getSql().getTopic()))
		{
			throw new BaseException(ErrorCodes.RULE_ENGINE_TOPIC_EMPTY.getCode(),ErrorCodes.RULE_ENGINE_TOPIC_EMPTY.getDesc());
		}
		/*if (ruleEngineDto.getActions().size()==0)
		{
			throw new BaseException(ErrorCodes.RULE_ENGINE_ACTIONS_EMPTY.getCode(),ErrorCodes.RULE_ENGINE_ACTIONS_EMPTY.getDesc());
		}*/
		return true;
	}

	/** 删除功能引擎 */
	@Override
	public ResultMap<? extends Object> deleteById(InputMap<RuleEngineDto> inputMap)
			throws Exception, JsonMappingException, IOException
	{
		boolean flag = this.isExitsUser(inputMap);//用户是否存在，如果不存在，直接报错
		
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		
		if(inputMap.getData() == null || inputMap.getData().getId() == null){
			throw new BaseException(ErrorCodes.FAILED);
		}
		ResultMap<? extends Object> result = new ResultMap<>();
		Integer id = null;
		
		if (inputMap != null && inputMap.getData() != null)
		{
			id = inputMap.getData().getId();
		}

		if (id == null || StringUtils.isNullOrEmpty(String.valueOf(id)))
		{
			result.setCode(ErrorCodes.RULE_ENGINE_ID_ERROR.getCode());
			result.setMsg(ErrorCodes.RULE_ENGINE_ID_ERROR.getDesc());
			return result;
		} 
		
		RuleEngine ruleEngine = ruleEngineAddMapper.selectById(id);
		flag = validationUser(inputMap.getConfig(),ruleEngine == null ? "":ruleEngine.getCreator());
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		
		actionsMapper.deleteByRuleId(id);
		Integer count = ruleEngineAddMapper.deleteById(id);
		if (count != 1)
		{
			throw new BaseException(ErrorCodes.RULE_ENGINE_DELETE_FAIL);
		}
		UserInfo currentUser = inputMap.getConfig();
		if (currentUser != null)
		{
			ruleRedisService.updateUserRule(String.valueOf(id));
		}
		return result;
	}

	/** 查询功能引擎 */
	@Override
	public ResultMap<? extends Object> selectById(InputMap<RuleEngineDto> inputMap)
			throws Exception, JsonMappingException, IOException
	{
		boolean flag = this.isExitsUser(inputMap);//用户是否存在，如果不存在，直接报错
		
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		
		if(inputMap.getData() == null || inputMap.getData().getId() == null){
			throw new BaseException(ErrorCodes.FAILED);
		}
		ResultMap<RuleEngineDto> result = new ResultMap<>();
		Integer id = null;
		if (inputMap != null && inputMap.getData() != null)
		{
			id = inputMap.getData().getId();
		}

		if (id == null || StringUtils.isNullOrEmpty(String.valueOf(id)))
		{
			result.setCode(ErrorCodes.RULE_ENGINE_ID_EMPTY.getCode());
			result.setMsg(ErrorCodes.RULE_ENGINE_ID_EMPTY.getDesc());
			return result;
		}

		RuleEngine ruleEngine = ruleEngineAddMapper.selectById(id);
		RuleEngineDto ruleEngineDto = new RuleEngineDto();
		// 如果为空，则直接返回
		if (ruleEngine == null)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		flag = validationUser(inputMap.getConfig(),ruleEngine == null ? "":ruleEngine.getCreator());
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		BeanUtils.copyProperties(ruleEngine, ruleEngineDto);
		List<ActionsDto> actionsDtolist = new ArrayList<ActionsDto>();
		Map<String,Object> queryParam = new HashMap<>(1);
		queryParam.put("ruleId", id);
		List<Actions> actionslist = actionsMapper.selectByRuleId(queryParam);
		for (Actions actions : actionslist)
		{
			ActionsDto actionsDto = new ActionsDto();
			BeanUtils.copyProperties(actions, actionsDto);
			actionsDtolist.add(actionsDto);
		}
		ruleEngineDto.setActions(actionsDtolist);
		result.setData(ruleEngineDto);
		return result;
	}

	/** 更新功能引擎 */
	@Override
	public ResultMap<? extends Object> update(InputMap<RuleEngineDto> inputMap)
			throws Exception, JsonMappingException, IOException
	{
		ResultMap<RuleEngineDto> result = new ResultMap<>();
		
		boolean flag = this.isExitsUser(inputMap);//用户是否存在，如果不存在，直接报错
		
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		
		if(inputMap.getData() == null || inputMap.getData().getId() == null){
			throw new BaseException(ErrorCodes.FAILED);
		}
		
		RuleEngine ruleEngine = ruleEngineAddMapper.selectById(inputMap.getData().getId());
		flag = validationUser(inputMap.getConfig(),ruleEngine == null ? "":ruleEngine.getCreator());
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		
		RuleEngineDto ruleEngineDto = inputMap.getData();
		//检查是否合法
		validationBooleanIsNull(ruleEngineDto);
		
		List<ActionsDto> actionslist = ruleEngineDto.getActions();
		Map<String,Object> deleteMap = new HashMap<>();
		deleteMap.put("ruleId", inputMap.getData().getId());
		List<Integer> extandList = new ArrayList<>();
		if (actionslist != null)
		{
			for (ActionsDto actionsDto : actionslist)
			{
				Actions actions = new Actions();
				BeanUtils.copyProperties(actionsDto, actions);
				actions.setRuleId(inputMap.getData().getId());
				actions.setActionParamString(JSON.toJSONString(actionsDto.getActionParam()));

				// 检查是否合法
				if (actionsDto.getActionTypeId()==null)
				{
					result.setCode(ErrorCodes.RULE_ENGINE_ACTIONTYPEID_EMPTY.getCode());
					result.setMsg(ErrorCodes.RULE_ENGINE_ACTIONTYPEID_EMPTY.getDesc());
					return result;
				}
				if (actionsDto.getActionParam().isEmpty())
				{
					result.setCode(ErrorCodes.RULE_ENGINE_ACTIONPARAM_EMPTY.getCode());
					result.setMsg(ErrorCodes.RULE_ENGINE_ACTIONPARAM_EMPTY.getDesc());
					return result;
				}
				if (actionTypeService.selectById(actions.getActionTypeId()) == null)
				{
					throw new BaseException(ErrorCodes.RULE_TYPE_NOT_EXIST);
				}
				
				if (actions.getId() != null)
				{
					// 设置用户信息
					systemUtil.setCreaterAndModifier(actions, inputMap.getConfig(), false);
					actionsMapper.updateById(actions);
					
				} else
				{
					// 设置用户信息
					systemUtil.setCreaterAndModifier(actions, inputMap.getConfig(), true);
					actionsMapper.insert(actions);
				}
				extandList.add(actions.getId());
			}
		}
		deleteMap.put("extandIds", extandList);
		actionsMapper.deleteByMap(deleteMap);
		RuleEngine insertRule = new RuleEngine();
		BeanUtils.copyProperties(ruleEngineDto, insertRule);
		Sql sql = ruleEngineDto.getSql();
		insertRule.setSqlString(JSON.toJSONString(sql));
		// 设置用户信息
		systemUtil.setCreaterAndModifier(insertRule, inputMap.getConfig(), false);
		Integer updateResult = ruleEngineAddMapper.updateById(insertRule);
		if (updateResult != 1)
		{
			throw new BaseException(ErrorCodes.RULE_ENGINE_UPDATE_FAIL);
		}
		UserInfo currentUser = inputMap.getConfig();
		if (currentUser != null)
		{
			String deleteRuleId = null;
			if(insertRule.getStatus() != null && insertRule.getStatus().intValue() ==0){
				deleteRuleId =String.valueOf(insertRule.getId());
			}
			ruleRedisService.updateUserRule(deleteRuleId);
		}
		return result;
	}

	/**
	 * 
	 * TODO 测试规则引擎配置是否正确,采用微服务调用ruleEngine类
	 * 
	 * @see com.foxconn.core.pro.server.rule.engine.front.service.RuleEngineService#debug(com.foxconn.core.pro.server.rule.engine.front.dto.RuleDebugDto)
	 */
	@Override
	public ResultMap<? extends Object> debug(InputMap<RuleDebugDto> inputMap)
			throws Exception, JsonMappingException, IOException
	{
		ResultMap<JSON> result = new ResultMap<>();
		RuleDebugDto bean = inputMap.getData();
		
		// 判断是否合法 userId与创建人是否一致
		boolean flag = this.isExitsUser(inputMap);//用户是否存在，如果不存在，直接报错
		
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		
		if (StringUtils.isNullOrEmpty(bean.getFields()))
		{
			result.setCode(ErrorCodes.RULE_ENGINE_DEBUG_FIELD_EMPTY.getCode());
			result.setMsg(ErrorCodes.RULE_ENGINE_DEBUG_FIELD_EMPTY.getDesc());
			return result;
		}

		if (StringUtils.isNullOrEmpty(bean.getTopic()))
		{
			result.setCode(ErrorCodes.RULE_ENGINE_DEBUG_TOPIC_EMPTY.getCode());
			result.setMsg(ErrorCodes.RULE_ENGINE_DEBUG_TOPIC_EMPTY.getDesc());
			return result;
		}
		if (bean.getData() == null)
		{
			result.setCode(ErrorCodes.RULE_ENGINE_DEBUG_DATA_EMPTY.getCode());
			result.setMsg(ErrorCodes.RULE_ENGINE_DEBUG_DATA_EMPTY.getDesc());
			return result;
		}
		JSONObject sourceData =  bean.getData();
		String dataTopic = null;
		JSONObject dataParams = null;
		String topic =bean.getTopic();
		String condition = bean.getCondition();
		String fileds = bean.getFields();
		JSONObject dataAction = null;
		if(sourceData.get("params") instanceof JSONObject){
			dataTopic = sourceData.getString("topic");
			dataParams = sourceData.getJSONObject("params");
			result.setData(getDataAction(dataTopic,topic,condition,fileds,dataParams));
		} else if(sourceData.get("params") instanceof JSONArray){
			JSONArray jsonArray = sourceData.getJSONArray("params");
			dataTopic = sourceData.getString("topic");
			if(jsonArray != null && jsonArray.size()>0){
				JSONArray jsonActionArray = new JSONArray(jsonArray.size());
				for(int i = 0; i < jsonArray.size(); i++){
					dataParams = jsonArray.getJSONObject(i);
					dataAction = getDataAction(dataTopic,topic,condition,fileds,dataParams);
					log.debug("==========dataAction======"+dataAction);
					if(dataAction != null){
						jsonActionArray.add(dataAction);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * getDataAction:获取单条规则数据. <br/>
	 * @author liupingan
	 * @return
	 * @since JDK 1.8
	 */
	private JSONObject getDataAction(String dataTopic,String topic,String condition,String fileds,JSONObject dataParams ){
		
		if (StringUtils.isNullOrEmpty(dataTopic) || dataParams == null)
		{
			return null;
		}

		// topic处理
		if (!StringUtils.isNullOrEmpty(topic))
		{
			// 替换topic表达式
			topic = "^" + topic;
			topic = topic.replaceAll("\\+", "[^\\s\\/]+").replace("#", "[\\S]{0,}");
			topic = topic + "$";
		} else
		{
			return null;
		}
		TopicAnalysis topicAnalysis = new TopicAnalysis();
		boolean isMatch = topicAnalysis.isMatchRule(dataTopic, topic);
		if (!isMatch)
		{
			return null;
		}
		// 进行数据处理
		
		Map<String,Object> context = null;
		if(StringUtils.isNullOrEmpty(condition)){
			
		} else {
			context = new HashMap<>(2);
			context.put("topic", context);
			context.put("root", dataParams);// 根据协议去解析
			isMatch = conditionAnalysis.isMatchRule(context,condition);
		}
		
		if (!isMatch)
		{
			return null;
		}
		
		context = new HashMap<>(2);
		context.put("topic", context);
		context.put("root", dataParams);// 根据协议去解析
		JSONObject dataAction = null;
		try{
			dataAction = fieldAnalysis.excute(context, fileds);
			return dataAction;
		} catch(Exception e){
			return null;
		}
	}

	@Override
	public ResultMap<? extends Object> updateStatus(InputMap<RuleEngineDto> inputMap)
			throws Exception, JsonMappingException, IOException
	{
		ResultMap<RuleEngineDto> result = new ResultMap<>();
		// 判断是否合法 userId与创建人是否一致
		boolean flag = this.isExitsUser(inputMap);//用户是否存在，如果不存在，直接报错
		
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		
		if(inputMap.getData() == null || inputMap.getData().getId() == null){
			throw new BaseException(ErrorCodes.FAILED);
		}
		
		
		RuleEngine ruleEngine = ruleEngineAddMapper.selectById(inputMap.getData().getId());
		flag = validationUser(inputMap.getConfig(),ruleEngine == null ? "":ruleEngine.getCreator());
		if (flag == false)
		{
			throw new BaseException(ErrorCodes.FAILED);
		}
		Integer ruleId = inputMap.getData().getId();
		RuleEngineDto ruleEngineDto = inputMap.getData();

		if (ruleEngineDto.getStatus() == null)
		{
			result.setCode(ErrorCodes.RULE_ENGINE_NAME_EMPTY.getCode());
			result.setMsg(ErrorCodes.RULE_ENGINE_NAME_EMPTY.getDesc());
			return result;
		}
		if (ruleEngineDto.getId() == null)
		{
			result.setCode(ErrorCodes.RULE_ENGINE_ID_EMPTY.getCode());
			result.setMsg(ErrorCodes.RULE_ENGINE_ID_EMPTY.getDesc());
			return result;
		}

		RuleEngine insertRule = new RuleEngine();
		// BeanUtils.copyProperties(ruleEngineDto, insertRule);
		insertRule.setId(ruleEngineDto.getId());
		insertRule.setStatus(ruleEngineDto.getStatus());
		// 设置用户信息
		systemUtil.setCreaterAndModifier(insertRule, inputMap.getConfig(), false);
		Integer updateResult = ruleEngineAddMapper.updateById(insertRule);
		if (updateResult != 1)
		{
			throw new BaseException(ErrorCodes.RULE_ENGINE_UPDATE_FAIL);
		}
		UserInfo currentUser = inputMap.getConfig();
		if (currentUser != null)
		{
			ruleRedisService.updateUserRule(ruleEngineDto.getStatus().intValue() == 0 ? String.valueOf(ruleId):null);
		}
		return result;
	}
}