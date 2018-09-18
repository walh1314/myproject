/**
 * Project Name:rule-engine-core
 * File Name:JSONObjectUtil.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.common.util
 * Date:2018年8月24日下午3:46:44
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.core.common.util;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * ClassName:JSONObjectUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月24日 下午3:46:44 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
public class JSONObjectUtil
{
	public static boolean containsKey(JSONObject source, String[] keys)
	{
		if (keys == null)
		{
			return false;
		}
		JSONObject temp = source;
		for (int i = 0; i < keys.length; i++)
		{
			if (i != 0)
			{
				if (temp != null && temp.containsKey(keys[i]))
				{
					temp = temp.getJSONObject(keys[i - 1]);
					continue;
				} else
				{
					return false;
				}
			} else
			{
				if (temp != null && temp.containsKey(keys[i]))
				{
					temp = temp.getJSONObject(keys[i]);
					continue;
				} else
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 动态处理数据类型 get:(这里用一句话描述这个方法的作用). <br/>
	 * TODO(这里描述这个方法适用条件 – 可选).<br/>
	 * TODO(这里描述这个方法的执行流程 – 可选).<br/>
	 * 
	 * @author liupingan
	 * @param keys
	 * @return
	 * @since JDK 1.8
	 */
	public static Object get(JSONObject source, String[] keys)
	{
		Object result = null;
		if (keys == null)
		{
			return result;
		}
		JSONObject temp = source;
		for (int i = 0; i < keys.length; i++)
		{
			if (i != 0)
			{
				if (temp != null && temp.containsKey(keys[i]))
				{
					if (i == keys.length - 1)
					{
						result = temp.get(keys[i]);
					}
					temp = temp.getJSONObject(keys[i - 1]);
					continue;
				} else
				{
					return null;
				}
			} else
			{
				if (temp != null && temp.containsKey(keys[i]))
				{
					if (i == keys.length - 1)
					{
						result = temp.get(keys[i]);
					} else
					{
						temp = temp.getJSONObject(keys[i]);
					}
				} else
				{
					return null;
				}
			}
		}
		return result;
	}

	public static Object get(JSONObject source, String keys)
	{
		if (StringUtils.isEmpty(keys))
		{
			return null;
		}
		return get(source, keys.trim().split("\\."));
	}

	public static void main(String[] args)
	{
		JSONObject jsonObject = JSONObject.parseObject("{\"name\":\"123\",\"data\":{\"test02\":\"aaa\"}}");
		String[] aaa =
		{ "data", "test02" };
		System.out.println(JSONObjectUtil.containsKey(jsonObject, aaa));
		String[] bbbb =
		{ "data", "test02" };
		System.out.println(JSONObjectUtil.get(jsonObject, bbbb));
		String[] ccc =
		{ "name" };
		System.out.println(JSONObjectUtil.get(jsonObject, ccc));
	}
}
