/**
 * Project Name:rule-engine-core
 * File Name:SqlTest.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.test
 * Date:2018年8月28日下午1:42:10
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.rabbitmq.test;
/**
 * ClassName:SqlTest <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月28日 下午1:42:10 <br/>
 * @author   liupingan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
import org.junit.Test;

import com.ql.util.express.parse.ExpressNode;
import com.ql.util.express.parse.ExpressParse;
import com.ql.util.express.parse.KeyWordDefine4SQL;
import com.ql.util.express.parse.NodeTypeManager;

public class SqlTest {
	
	public String[] testString ={
			"select id.  aaa. cc as id2 from upp_biz_order"/*,
			"select id as id2,name as name2 from upp_biz_order where a=1",
			"select id as id2,name from upp_biz_order where 1=1",*/
			};
	@Test
	public void testDefine() throws Exception {
		NodeTypeManager manager = new NodeTypeManager(new KeyWordDefine4SQL());
		ExpressParse parse = new ExpressParse(manager,null,false);
		for(String text : testString){
			ExpressNode result = parse.parse(null, text, true, null);
			System.out.print(result);
		}
		
	}
}

