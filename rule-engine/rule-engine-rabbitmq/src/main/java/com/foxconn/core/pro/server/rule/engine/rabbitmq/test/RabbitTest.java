/**
 * Project Name:rule-engine-core
 * File Name:RabbitTest.java
 * Package Name:com.foxconn.core.pro.server.rule.engine.core.test
 * Date:2018年8月28日下午3:50:32
 * Copyright (c) 2018, Foxconn All Rights Reserved.
 *
*/

package com.foxconn.core.pro.server.rule.engine.rabbitmq.test;
/**
 * ClassName:RabbitTest <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年8月28日 下午3:50:32 <br/>
 * @author   liupingan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;


@RestController
@RequestMapping("/rabbit")
public class RabbitTest {
    
    @Autowired
    private HelloSender1 helloSender1;
    
    @Autowired
    private HelloSender1 helloSender2;
    
    @PostMapping("/hello")
    public void hello(@RequestBody JSONObject json) {
        helloSender1.send(json.toJSONString());
    }
    
    @PostMapping("/source")
    public void source(@RequestBody JSONObject json) {
        helloSender1.sendSource(json.toJSONString());
    }
    
    /**
     * 单生产者-多消费者
     */
    @PostMapping("/oneToMany")
    public void oneToMany(@RequestBody JSONObject json) {
        for(int i=0;i<10;i++){
        	JSONObject result = (JSONObject) json.clone();
        	result.put("NO.number", i);
            helloSender1.send(result.toJSONString());
        }
    }
}
