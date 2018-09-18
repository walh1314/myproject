/**
 * Project Name:rule-engine-core
 * File Name:Test.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.test
 * Date:2018年8月23日下午3:03:31
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.rabbitmq.test;

import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.core.pro.server.rule.engine.core.sql.ConditionParser;
import com.foxconn.core.pro.server.rule.engine.core.sql.FieldParser;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

/**
 * ClassName:Test <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月23日 下午3:03:31 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
public class Test
{
	public static void main(String[] args)
	{
		ExpressRunner runner = new ExpressRunner();
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("a", 1);
		context.put("b", 2);
		context.put("c", 3);
		String express = "a+b*c";
		Object r = null;
		try
		{
			r = runner.execute(express, context, null, true, false);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(r);
		try
		{
			test();
		} catch (Exception e)
		{

			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		String source = "111111/sys/aaaa/";
		String str = "/sys/+";
		str = "^"+str;
		String pattern = str.replace("+", "[^\\s\\/]+");
		pattern = pattern.replace("#", "[\\S]{0,}");
		str = str + "$";
		System.out.println(pattern);

		boolean isMatch = Pattern.matches(pattern, source);
		System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);

	}

	@org.junit.Test
	public static void test() throws Exception
	{
		String exp = "import com.foxconn.core.pro.server.rule.engine.core.test.CustBean; "
				+ "import com.alibaba.fastjson.JSONObject; " + "CustBean cust = new CustBean(1); "
				+ "cust.setName(\"11\"); cust.setAge(10); "
				+ "if cust.getAge() == 10 then  {System.out.println(\"1\");} else {System.out.println(\"0\");}"
				+ "if (cust.getName() == 11 ) then  {System.out.println(\"111\");} else {System.out.println(\"000\");}"
				+ "if (cust.getName() == 11 ) then  {System.out.println(\"6666\");}" + ""
				+ "System.out.println(JSONObject.toJSONString(cust)); " + "return cust.getName(); ";
		ExpressRunner runner = new ExpressRunner();
		// 执行表达式，并将结果赋给r
		String r = (String) runner.execute(exp, null, null, false, false);
		System.out.println(r);
		// Assert.assertTrue("操作符执行错误","小强".equals(r));
	}
	
	@org.junit.Test
	public static void testCondition(){
		String JsonString = "{\"name\":\"123\",\"aaora\":\"5555\",\"data\":{\"test02\":\"aaa\",\"test03\":44}}";
		String sqlCondition = "(aaora=\"555\")or((data.test02 = 'aaa')and(data.test03 = 44))";
		ConditionParser conditionParser = new ConditionParser(sqlCondition);

		System.out.println(conditionParser.getQLExpress());
		ExpressRunner runner = new ExpressRunner();
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		System.out.println(JsonString);

		JSONObject jsonObject = JSONObject.parseObject(JsonString);
		context.put("root", jsonObject);

		context.put("b", 2);

		context.put("c", 3);

		String content = "";

		content = "import com.alibaba.fastjson.JSONObject; import com.foxconn.core.pro.server.rule.engine.core.common.util.JSONObjectUtil;";

		content += conditionParser.getQLExpress();
		try
		{
			System.out.println(content);
			Object runnerResult = runner.execute(content, context, null, true, false);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println(runnerResult);
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@org.junit.Test
	public static void testField(){
		JSONObject jsonObject = JSONObject.parseObject("{\"name\":\"123\",\"data\":{\"test02\":\"aaa\"}}");

		ExpressRunner runner = new ExpressRunner();
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();

		jsonObject = JSONObject.parseObject("{\"name\":\"123\",\"data\":{\"test02\":\"aaa\"}}");
		context.put("root", jsonObject);
		FieldParser fieldParser = new FieldParser("*,data.test02 as uuuu");
		System.out.println(fieldParser.getQLExpress());
		String content = "import com.alibaba.fastjson.JSONObject; import com.foxconn.core.pro.server.rule.engine.core.common.util.JSONObjectUtil;";
		content += fieldParser.getQLExpress();
		Object r = null;
		try
		{
			r = runner.execute(content, context, null, true, false);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>>>>>>>>1111111>>>>>>>>>>>>>>");
		System.out.println(r);
	}
}
