package com.foxconn.core.pro.server.rule.engine.action.common.exception;

public enum ErrorCodes
{
	SCUUESS("1", "Successful"), 
	FAILED("-1", "Failed"),
	
	COREPRO_COMMON_USERID_EMPTY("corepro-common-1000-01", "corepro.common.userid.empty"),
	COREPRO_COMMON_TOPIC_EMPTY("corepro-common-1000-02", "corepro.common.topic.empty"),
	
	COREPRO_COMMON_EMAIL_FAIL("corepro-common-1000-03", "corepro.common.email.fail"),
	
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
