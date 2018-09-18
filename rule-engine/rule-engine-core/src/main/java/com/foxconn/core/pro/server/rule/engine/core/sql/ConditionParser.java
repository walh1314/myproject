package com.foxconn.core.pro.server.rule.engine.core.sql;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
@Service("conditionParser")
public class ConditionParser extends BaseSingleParser
{

	@Autowired
	private RuleTemplateProperties ruleTemplateProperties;

	public ConditionParser()
	{
		super();
	}

	public ConditionParser(String originalValue)
	{
		super(originalValue);
	}

	/**
	 * 
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
		String sqlCondition = formatterString(originalValue);

		String[] sqlCondtionArray = sqlCondition.split(" ");
		List<String> listString = new ArrayList<String>();
		StringBuffer temp = new StringBuffer();
		String matchs = "^([oO][rR])|([aA][nN][dD])$";
		String tempString = null;
		for (int k = 0; k < sqlCondtionArray.length; k++)
		{
			tempString = sqlCondtionArray[k];
			if (Pattern.matches(matchs, tempString))
			{
				listString.add(temp.toString());
				listString.add(tempString);
				temp = new StringBuffer();
			} else
			{
				temp.append(tempString);
				if (k != sqlCondtionArray.length - 1)
				{
					temp.append(" ");
				}
			}
			if (k == sqlCondtionArray.length - 1)
			{
				listString.add(temp.toString());
			}
		}
		StringBuffer buffer = new StringBuffer();
		StringBuffer bufferValue = new StringBuffer();
		String varStr = null;
		String varKeysStr = null;
		String regEx = "(<>)|(!=)|(<=)|(>=)|(<)|(>)|(=)|(\\()|(\\))";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = null;
		String[] words = null;
		int count = 0;
		for (int i = 0; i < listString.size(); i++)
		{
			tempString = listString.get(i);

			if (Pattern.matches(matchs, tempString))
			{
				buffer.append(" " + tempString + " ");
				continue;
			}

			matcher = pattern.matcher(tempString);
			words = pattern.split(tempString);

			if (words != null && words.length > 0)
			{
				count = 0;
				while (count < words.length)
				{
					varStr = "";
					if (matcher.find())
					{
						if ("!=".equals(matcher.group()) || "<>".equals(matcher.group()) || "<=".equals(matcher.group())
								|| ">=".equals(matcher.group()) || "<".equals(matcher.group())
								|| ">".equals(matcher.group()) || "=".equals(matcher.group()))
						{
							varStr = " __temp_" + i + "_" + count + "__";
							bufferValue.append(" " + varStr + " = null;\r\n");
							varKeysStr = " __temp_keys_" + i + "_" + count + "__";
							bufferValue.append(" " + varKeysStr + "= null;\r\n");

							bufferValue.append(" " + varKeysStr + "=\"" + words[count].trim() + "\";\r\n");
							bufferValue.append(" " + varStr + " = JSONObjectUtil.get(root," + varKeysStr + ");\r\n");
							buffer.append(varStr);
							buffer.append(" " + ("=".equals(matcher.group()) ? "==" : matcher.group()) + " ");
						} else
						{
							if (org.apache.commons.lang.StringUtils.isNumericSpace(words[count]))
							{// 如果为数字，则直接拼接
								buffer.append(words[count]);
							} else if (words[count].trim().startsWith("\"") && words[count].trim().endsWith("\"")
									|| (words[count].trim().startsWith("'") && words[count].trim().endsWith("'")))
							{// 是否为字符串
								buffer.append(words[count].trim());
							} else if (!StringUtils.isEmpty(words[count]) && !words[count].equals("(")
									&& !words[count].equals(")"))
							{
								varStr = " __temp_" + i + "_" + count + "__";
								varKeysStr = " __temp_keys_" + i + "_" + count + "__";

								bufferValue.append(" " + varKeysStr + "=\"" + words[count].trim() + "\";\r\n");
								bufferValue
										.append(" " + varStr + " = JSONObjectUtil.get(root," + varKeysStr + ");\r\n");
								buffer.append(varStr);
							} else
							{
								buffer.append(words[count]);
							}
							buffer.append(" " + ("=".equals(matcher.group()) ? "==" : matcher.group()) + " ");
						}
					} else
					{
						// 判断是否为数字
						if (org.apache.commons.lang.StringUtils.isNumericSpace(words[count]))
						{// 如果为数字，则直接拼接
							buffer.append(words[count]);
						} else if (words[count].trim().startsWith("\"") && words[count].trim().endsWith("\"")
								|| (words[count].trim().startsWith("'") && words[count].trim().endsWith("'")))
						{// 是否为字符串
							buffer.append(words[count].trim());
						} else if (!StringUtils.isEmpty(words[count]))
						{
							varStr = " __temp_" + i + "_" + count + "__";
							varKeysStr = " __temp_keys_" + i + "_" + count + "__";
							bufferValue.append(" " + varKeysStr + "=\"" + words[count].trim() + "\";\r\n");
							bufferValue.append(" " + varStr + " = JSONObjectUtil.get(root," + varKeysStr + ");\r\n");

							buffer.append(varStr);
						}
					}
					count++;
				}
			}
		}
		StringBuffer result = new StringBuffer();
		result.append(getHeaders());
		result.append("\r\n");
		result.append(bufferValue.toString());
		result.append("\r\n");
		result.append(" return ");
		result.append(buffer);
		result.append(" ;");
		return result.toString();
	}

	@Override
	public String getQLExpress()
	{
		return getQLExpress(" ");
	}

	private static String formatterString(String str)
	{
		String regEx = "(<>)|(!=)|(<=)|(>=)|(<)|(>)|(=)|(\\()|(\\))";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(str);
		String[] words = pattern.split(str);
		if (words.length > 0)
		{
			int count = 0;
			while (count < words.length)
			{
				if (matcher.find())
				{
					words[count] += " " + matcher.group() + " ";
				}
				count++;
			}
		}
		char[] strArray = str.toCharArray();
		char rightBrackets = ')';
		int count = 0;
		for (int i = strArray.length - 1; i >= 0; i--)
		{
			if (rightBrackets == strArray[i])
			{
				count++;
			} else
			{
				break;
			}
		}
		if (count > 1)
		{
			for (int j = 0; j < count - 1; j++)
			{
				words[words.length - 1] += " " + rightBrackets + " ";
			}
		}
		String result = StringUtils.arrayToDelimitedString(words, "");
		result = result.replaceAll("[\\t\\s]{1,}", " ");
		return result.trim();
	}

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * 
	 * @see com.foxconn.core.pro.server.rule.engine.core.sql.BaseSingleParser#getHeaders()
	 */
	@Override
	public String getHeaders()
	{
		String result ="";
		InputStream stencilsetStream = ConditionParser.class.getClassLoader().getResourceAsStream(ruleTemplateProperties.getCondition().getHeaderFile());
		try
		{
			result = IOUtils.toString(stencilsetStream, StandardCharsets.UTF_8);
		} catch (Exception e)
		{
			log.error(e.getMessage(),e);
		}
		return result;
	}
}