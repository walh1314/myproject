package com.foxconn.core.pro.server.rule.engine.front.exception;

public enum ErrorCodes
{
	SCUUESS("1", "Successful"), 
	FAILED("-1", "Failed"),
	
	RULE_TYPE_NOT_EXIST("ruleEngine-1000-01", "rule.type.not.exist"),
	/**增加规则引擎*/
	RULE_ENGINE_NAME_EMPTY("ruleEngine-1000-06", "ruleEngine.name.empty"),
	RULE_ENGINE_ADD_FAIL("ruleEngine-1000-07", "add.ruleEngine.fail"),
	
	/*RULE_ENGINE_SQL_EMPTY("ruleEngine-1000-09", "ruleEngine.sql.empty"),*/
	/*RULE_ENGINE_DATA_EMPTY("ruleEngine-1000-10", "ruleEngine.data.empty"),*/
	RULE_ENGINE_FIELDS_EMPTY("ruleEngine-1000-11", "ruleEngine.fields.empty"),
	RULE_ENGINE_TOPIC_EMPTY("ruleEngine-1000-12", "ruleEngine.topic.empty"),
	RULE_ENGINE_CONDITION_EMPTY("ruleEngine-1000-13", "ruleEngine.condition.empty"),
	RULE_ENGINE_ACTIONS_EMPTY("ruleEngine-1000-14", "ruleEngine.actions.empty"),
	RULE_ENGINE_ACTIONTYPEID_EMPTY("ruleEngine-1000-15", "ruleEngine.actionTypeId.empty"),
	RULE_ENGINE_ACTIONPARAM_EMPTY("ruleEngine-1000-16", "ruleEngine.actionParam.empty"),
	RULE_ENGINE_DATATYPE_EMPTY("ruleEngine-1000-17", "ruleEngine.datatype.empty"),
	RULE_ENGINE_VERSION_EMPTY("ruleEngine-1000-18", "ruleEngine.version.empty"),
	RULE_ENGINE_ACCESSMODE_EMPTY("ruleEngine-1000-19", "ruleEngine.accessMode.empty"),
	
	/**分页查询*/
	RULE_ENGINE_PAGESIZE_EMPTY("ruleEngine-1000-18", "ruleEngine.pageSize.empty"),
	RULE_ENGINE_CURRENTPAGE_EMPTY("ruleEngine-1000-19", "ruleEngine.currentPage.empty"),
	RULE_ENGINE_STATUS_EMPTY("ruleEngine-1000-08", "ruleEngine.status.empty"),
	
	/**删除规则引擎*/
	RULE_ENGINE_ID_EMPTY("ruleEngine-1000-02", "ruleEngine.id.empty"),
	RULE_ENGINE_ID_ERROR("ruleEngine-1000-03", "ruleEngine.id.error"),
	RULE_ENGINE_DELETE_FAIL("ruleEngine-1000-15", "delete.ruleEngine.fail"),
	

	
	/**更新规则引擎*/
	RULE_ENGINE_UPDATE_FAIL("ruleEngine-1000-14", "update.ruleEngine.fail"),
	
	/**规则引擎测试*/
	RULE_ENGINE_DEBUG_FIELD_EMPTY("ruleEngine-1001-01", "ruleEngine.field.empty"),
	RULE_ENGINE_DEBUG_TOPIC_EMPTY("ruleEngine-1001-02", "ruleEngine.topic.empty"),
	RULE_ENGINE_DEBUG_DATA_EMPTY("ruleEngine-1001-03", "ruleEngine.data.empty"),
	RULE_ENGINE_DEBUG_DATA_PARAM_EMPTY("ruleEngine-1001-04", "ruleEngine.data.param.empty"),
	
	/**动作删除**/
	RULE_ACTION_DELETE_FAIL("ruleEngine-1002-01", "ruleEngine.action.delete.fail"),
	RULE_ACTION_ID_ERROR("ruleEngine-1002-02", "ruleEngine.action.id.error"),
	
	/**更新规则引擎*/
	RULE_ACTION_UPDATE_FAIL("ruleEngine-1002-3", "update.ruleEngine.action.fail"),
	
	COREPRO_COMMON_USERID_EMPTY("corepro-common-1000-01", "corepro.common.userid.empty"),
	COREPRO_COMMON_TOPIC_EMPTY("corepro-common-1000-01", "corepro.common.topic.empty"),
	
	/**获取db异常**/
	COREPRO_GET_TENANT_DB_FAIL("corepro-common-1001-01", "corepro.get.Tenant.db.fail"),
	
	SYSTEM_EXCEPTION("sys-1000-01", "system.exception")
	;
	private String code;
	private String desc;

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	private ErrorCodes(String code, String desc)
	{
		this.code = code;
		this.desc = desc;
	}
}
