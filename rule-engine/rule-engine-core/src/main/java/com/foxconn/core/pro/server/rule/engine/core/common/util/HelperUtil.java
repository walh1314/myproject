/**
 * Project Name:rule-engine-core
 * File Name:HelperUtil.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.common.util
 * Date:2018年8月27日上午10:32:25
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.core.common.util;

import java.util.Stack;

/**
 * ClassName:HelperUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年8月27日 上午10:32:25 <br/>
 * 
 * @author liupingan
 * @version
 * @since JDK 1.8
 * @see
 */
public class HelperUtil
{

	public static void floatCloud(String toDeal)
	{

		Stack<Integer> stack = new Stack<Integer>();

		stack.add(0);
		for (int i = 0; i < toDeal.length(); ++i)
		{

			char c = toDeal.charAt(i);
			if (c == '(')
			{

				stack.add(i + 1);
			} else if (c == ')')
			{
				int index = stack.pop();
				if (index > 0)
				{
					System.out.println("左边第" + index + "位括号和左边第" + (i + 1) + "位括号对应");

				} else if (index == 0)
				{

					System.out.println("左边第" + (i + 1) + "位括号没对应左括号");

					stack.add(0);

				}

			}

		}

		if (stack != null)
		{

			while (!stack.isEmpty())
			{

				int index = stack.pop();

				if (index != 0)

					System.out.println("左边第" + index + "位括号没对应右括号");

			}

		}

	}

	public static void main(String[] args)
	{

		floatCloud("(a+b))(");

		System.out.println();

		floatCloud("(a+(b))))(");

		System.out.println();

		floatCloud("(a+b))())(");

		System.out.println();

	}
}
