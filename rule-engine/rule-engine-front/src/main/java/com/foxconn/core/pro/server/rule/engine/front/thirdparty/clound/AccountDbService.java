package com.foxconn.core.pro.server.rule.engine.front.thirdparty.clound;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.foxconn.core.pro.server.rule.engine.front.thirdparty.entity.CloundMap;
import com.foxconn.core.pro.server.rule.engine.front.thirdparty.entity.TenantDb;

@FeignClient(name = "${com.server.tenant}", url = "${com.server.tenant-url}", configuration = FeignFrontConfiguration.class)
public interface AccountDbService
{
	@RequestMapping(value = "/userAccount/tenant_db/{userId}", method = RequestMethod.GET)
	CloundMap<TenantDb> getTenantDb(@PathVariable("userId") String userId,@RequestParam Map<String, Object> map);
}
