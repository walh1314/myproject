package com.foxconn.core.pro.server.rule.engine.core.thirdparty.clound;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.core.pro.server.rule.engine.core.thirdparty.entity.NoticeRedisBean;

@FeignClient(name = "${com.server.front}", url = "${com.server.front-url}", configuration = FeignConfiguration.class)
public interface FrontService
{
	@RequestMapping(value = "/ruleEngine//notice/redis", method = RequestMethod.POST)
	JSONObject noticeRedis(NoticeRedisBean bean);
}
