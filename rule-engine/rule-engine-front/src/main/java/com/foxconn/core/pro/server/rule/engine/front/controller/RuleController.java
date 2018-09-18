/**
 * Project Name:rule-engine-front
 * File Name:RuleController.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.front.controller.base
 * Date:2018年8月24日上午8:51:24
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.front.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.foxconn.core.pro.server.rule.engine.front.common.RuleRedisService;
import com.foxconn.core.pro.server.rule.engine.front.common.constant.URLConstant;
import com.foxconn.core.pro.server.rule.engine.front.common.entity.FrontPage;
import com.foxconn.core.pro.server.rule.engine.front.common.entity.ResultMap;
import com.foxconn.core.pro.server.rule.engine.front.common.util.SystemUtil;
import com.foxconn.core.pro.server.rule.engine.front.controller.base.BaseController;
import com.foxconn.core.pro.server.rule.engine.front.dto.ActionTypeDto;
import com.foxconn.core.pro.server.rule.engine.front.dto.ActionsDto;
import com.foxconn.core.pro.server.rule.engine.front.dto.InputMap;
import com.foxconn.core.pro.server.rule.engine.front.dto.NoticeDto;
import com.foxconn.core.pro.server.rule.engine.front.dto.RuleDebugDto;
import com.foxconn.core.pro.server.rule.engine.front.dto.RuleEngineDto;
import com.foxconn.core.pro.server.rule.engine.front.entity.RuleEngine;
import com.foxconn.core.pro.server.rule.engine.front.service.ActionTypeService;
import com.foxconn.core.pro.server.rule.engine.front.service.ActionsService;
import com.foxconn.core.pro.server.rule.engine.front.service.RuleEngineService;
import com.foxconn.core.pro.server.rule.engine.front.thirdparty.clound.AccountDbService;

/**
 * ClassName:RuleController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月24日 上午8:51:24 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@RestController
@RequestMapping(URLConstant.RULE_BASE)
public class RuleController extends BaseController
{

	@Autowired
	private SystemUtil systemUtil;
	@Autowired
	private RuleEngineService ruleEngineService;

	@Autowired
	private ActionTypeService actionTypeService;
	
	@Autowired
	private ActionsService actionsService;
	
	@Autowired
	private AccountDbService accountDbService;
	
	@Autowired
	private RuleRedisService ruleRedisService;

	/** 分页查询 */
	@RequestMapping(value = URLConstant.RULE_PAGE_LIST, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> getPageList(@RequestBody InputMap<RuleEngineDto> bean, FrontPage<RuleEngine> page)throws JsonMappingException, IOException, Exception
	{
		systemUtil.setCurrentUser(bean);
		return getMessage(ruleEngineService.selectPage(page, bean));
	}

	/** 增加规则引擎 */
	@RequestMapping(value = URLConstant.RULE_ADD, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> insertRuleEngine(@RequestBody InputMap<RuleEngineDto> ruleEngineDto)throws JsonMappingException, IOException, Exception
	{
		systemUtil.setCurrentUser(ruleEngineDto);
		return ruleEngineService.insert(ruleEngineDto);
	}

	/** 删除规则引擎 */
	@RequestMapping(value = URLConstant.RULE_DEL, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> deleteById(@RequestBody InputMap<RuleEngineDto> ruleEngineDto)throws JsonMappingException, NumberFormatException, IOException, Exception 
	{
		systemUtil.setCurrentUser(ruleEngineDto);
		return ruleEngineService.deleteById(ruleEngineDto);
	}

	/** 查询规则引擎 */
	@RequestMapping(value = URLConstant.RULE_DETAIL, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> query(@RequestBody InputMap<RuleEngineDto> ruleEngineDto)throws JsonMappingException, NumberFormatException, IOException, Exception 
	{
		systemUtil.setCurrentUser(ruleEngineDto);
		return ruleEngineService.selectById(ruleEngineDto);
	}

	/** 更新规则引擎 */
	@RequestMapping(value = URLConstant.RULE_UPDATE, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> updateRuleEngine(@RequestBody InputMap<RuleEngineDto> ruleEngineDto)throws JsonMappingException, IOException, Exception
	{
		systemUtil.setCurrentUser(ruleEngineDto);
		return ruleEngineService.update(ruleEngineDto);
	}

	/** 查询规则引擎类型 */
	@RequestMapping(value = URLConstant.RULE_ACTION_TYPE_LIST, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> queryActionTypeList(@RequestBody InputMap<ActionTypeDto> bean)throws JsonMappingException, IOException, Exception
	{
		systemUtil.setCurrentUser(bean);
		return actionTypeService.selectByMaps(bean.getData());
	}

	/**
	 * 
	 * debug:(调试接口). <br/>
	 * @author liupingan
	 * @param bean
	 * @return
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws Exception
	 * @since JDK 1.8
	 */
	@RequestMapping(value = URLConstant.RULE_DEBUG, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> debug(@RequestBody InputMap<RuleDebugDto> bean)throws JsonMappingException, IOException, Exception
	{
		systemUtil.setCurrentUser(bean);
		return ruleEngineService.debug(bean);
	}

	/**
	 * updateStatus:更新规则引擎状态接口. <br/>
	 * @author liupingan
	 * @param bean
	 * @return
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws Exception
	 * @since JDK 1.8
	 */
	@RequestMapping(value = URLConstant.RULE_UPDATE_STATUS, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> updateStatus(@RequestBody InputMap<RuleEngineDto> bean) throws JsonMappingException, IOException, Exception
	{
		systemUtil.setCurrentUser(bean);
		return ruleEngineService.updateStatus(bean);
	}
	
	/**更新规则动作*/
	@RequestMapping(value = URLConstant.RULE_ACTION_UPDATE, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> updateAction(@RequestBody InputMap<ActionsDto> bean)throws JsonMappingException, IOException, Exception
	{
		systemUtil.setCurrentUser(bean);
		return actionsService.update(bean);
	}

	/**
	 * 
	 * delAction:删除规则动作. <br/>
	 *
	 * @author liupingan
	 * @param bean
	 * @return
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws Exception
	 * @since JDK 1.8
	 */
	@RequestMapping(value = URLConstant.RULE_ACTION_DEL, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> delAction(@RequestBody InputMap<ActionsDto> bean)throws JsonMappingException, IOException, Exception
	{
		systemUtil.setCurrentUser(bean);
		return actionsService.deleteById(bean);
	}
	
	/**
	 * quertActionDetail:(查询单个动作详情). <br/>
	 * @author liupingan
	 * @param ruleEngineDto
	 * @return
	 * @throws JsonMappingException
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws Exception
	 * @since JDK 1.8
	 */
	@RequestMapping(value = URLConstant.RULE_ACTION_DETAIL, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> quertActionDetail(@RequestBody InputMap<ActionsDto> bean )throws JsonMappingException, NumberFormatException, IOException, Exception 
	{
		systemUtil.setCurrentUser(bean);
		return actionsService.selectById(bean);
	}
	
	@RequestMapping(value = URLConstant.RULE_ACTION_ADD, method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> addAction(@RequestBody InputMap<ActionsDto> bean )throws JsonMappingException, NumberFormatException, IOException, Exception 
	{
		systemUtil.setCurrentUser(bean);
		return actionsService.insert(bean);
	}
	
	@RequestMapping(value = "/test", method =
	{ RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> accountDbService( )throws JsonMappingException, NumberFormatException, IOException, Exception 
	{
		Map<String,Object> requestMap = new HashMap<>(2);
		requestMap.put("X-NameSpace-Code", "default-code");
		requestMap.put("X-MicroService-Name", "beancon-mc-useraccount");
		accountDbService.getTenantDb("aaaaaa",requestMap);
		return null;
	}
	
	@RequestMapping(value = URLConstant.RULE_NOTICE_REDIS, method =	
	{RequestMethod.POST, RequestMethod.GET })
	public ResultMap<? extends Object> noticeRedis(@RequestBody InputMap<NoticeDto> bean)throws JsonMappingException, NumberFormatException, IOException, Exception 
	{
		systemUtil.setCurrentUser(bean);
		return ruleRedisService.updateUserRule(bean);
	}
	
	
}