package com.foxconn.core.pro.server.rule.engine.core.sql;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.foxconn.core.pro.server.rule.engine.core.config.template.RuleTemplateProperties;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** */
/**
 *
 * 单句查询语句解析器
 * 
 * @author 赵朝峰
 *
 * @since 2013-6-10
 * @version 1.00
 */
@Getter
@Slf4j
@Service("fieldParser")
public class FieldParser extends BaseSingleParser
{

	protected final static String splitPattern = ",";

	@Autowired
	private RuleTemplateProperties ruleTemplateProperties;

	public FieldParser(String originalValue)
	{
		super(originalValue);
	}

	public FieldParser()
	{
		super();
	}

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * 
	 * @see com.foxconn.core.pro.server.rule.engine.core.sql.BaseSingleParser#getQLExpress()
	 */
	@Override
	public String getQLExpress(String splitPattern)
	{
		if (StringUtils.isEmpty(originalValue))
		{
			return null;
		}
		String temp = originalValue.trim();
		temp = temp.replaceAll("[\\t ]{1,}", " ");
		String[] listStr = temp.split(splitPattern);
		// 进行分解
		StringBuffer result = new StringBuffer();
		// 进行分解解析
		String[] filed = null;
		result.append(getHeaders());
		result.append("\r\n");
		result.append(" JSONObject __result__ = new JSONObject();\r\n ");
		String filedName = null;
		for (int i = 0; i < listStr.length; i++)
		{
			temp = listStr[i];
			filed = temp.split(" ");
			if (filed.length == 1)
			{
				if ("*".equals(temp))
				{
					result.append("__result__ = (JSONObject) root.clone();\r\n");
					continue;
				}
				filedName = temp;
				//result.append(" __temp_keys__ = null;\r\n");
				result.append(" __temp_keys__ = \"");
				result.append(temp);
				result.append("\";\r\n");
				result.append(" __temp__ = JSONObjectUtil.get(root, __temp_keys__);\r\n");
				result.append("__result__.put(\"");
				result.append(filedName);
				result.append("\",");
				result.append(" __temp__");
				result.append(");\r\n");
			} else
			{
				Integer filedLength = filed.length;
				if (filed[filed.length - 2].equalsIgnoreCase("as"))// 获得别名
				{
					filedName = filed[filed.length - 1];
					filedLength = filed.length - 2;
					temp = filed[0];
				} else
				{

				}
				// 考虑一层情况
				result.append(" __temp__ = null;\r\n");
				result.append(" __temp_keys__ = null;\r\n");
				for (int j = 0; j < filedLength; j++)
				{
					result.append(" __temp_keys__ = \"");
					result.append(temp);
					result.append("\";\r\n");
					result.append(" __temp__ = JSONObjectUtil.get(root, __temp_keys__);\r\n");
				}
				result.append("__result__.put(\"");
				result.append(filedName);
				result.append("\",");
				result.append(" __temp__ ");
				result.append(");\r\n");
			}
		}
		result.append(" return __result__;");
		return result.toString();
	}

	@Override
	public String getQLExpress()
	{
		return getQLExpress(splitPattern);
	}

	@Override
	public String getHeaders()
	{
		String result = "";
		InputStream stencilsetStream = ConditionParser.class.getClassLoader()
				.getResourceAsStream(ruleTemplateProperties.getField().getHeaderFile());
		try
		{
			result = IOUtils.toString(stencilsetStream, StandardCharsets.UTF_8);
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
		return result;
	}
}