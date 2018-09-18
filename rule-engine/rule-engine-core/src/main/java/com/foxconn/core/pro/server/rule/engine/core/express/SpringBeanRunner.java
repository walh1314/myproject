/**
 * Project Name:rule-engine-core
 * File Name:SpringBeanRunner.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.express
 * Date:2018年8月23日下午4:11:00
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.core.express;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.core.pro.server.rule.engine.core.express.macro.MacroDefine;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

import lombok.extern.slf4j.Slf4j;

/**
 * ClassName:SpringBeanRunner <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月23日 下午4:11:00 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
@Service
@Slf4j
public class SpringBeanRunner implements ApplicationContextAware
{
	private ApplicationContext applicationContext;
	private ExpressRunner runner;

	@Override
	public void setApplicationContext(ApplicationContext context)
	{
		this.applicationContext = context;
		runner = new ExpressRunner();
	}

	private void initRunner()
	{
		try
		{
			runner.addOperatorWithAlias("如果", "if", null);
			runner.addOperatorWithAlias("则", "then", null);
			runner.addOperatorWithAlias("否则", "else", null);
			runner.addOperatorWithAlias("并且", "and", null);
			runner.addOperatorWithAlias("或者", "or", null);
			runner.addFunctionOfClassMethod("productKey",MacroDefine.class.getName(),"productKey",new String[] {JSONObject.class.getName()},null);
			runner.addMacro("__productKey__", "productKey()");
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

	}

	public Object executeExpress(String text, Map<String, Object> context)
	{
		IExpressContext<String, Object> expressContext = new SpringBeanContext(context, this.applicationContext);
		try
		{
			return runner.execute(text, expressContext, null, true, false);
		} catch (Exception e)
		{
			log.error("qlExpress运行出错！", e);
		}
		return null;

	}
}
