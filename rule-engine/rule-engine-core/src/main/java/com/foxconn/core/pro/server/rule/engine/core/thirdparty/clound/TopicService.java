package com.foxconn.core.pro.server.rule.engine.core.thirdparty.clound;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@FeignClient(name = "${com.server.topic}")
public interface TopicService
{
	//@RequestMapping(value = "/getTopic/{dataId}", method = RequestMethod.GET)
	String getTopic(@PathVariable("dataId") String dataId);
}