package com.foxconn.core.pro.server.rule.engine.front.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.foxconn.core.pro.server.rule.engine.front.dto.InputMap;
import com.foxconn.core.pro.server.rule.engine.front.entity.UserInfo;
import com.foxconn.core.pro.server.rule.engine.front.thirdparty.clound.AccountDbService;
import com.foxconn.core.pro.server.rule.engine.front.thirdparty.config.ServerFrontParamConfig;
import com.foxconn.core.pro.server.rule.engine.front.thirdparty.constant.CoreproCommonConstant;
import com.foxconn.core.pro.server.rule.engine.front.thirdparty.entity.CloundMap;
import com.foxconn.core.pro.server.rule.engine.front.thirdparty.entity.TenantDb;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统工具类 ClassName: SystemUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2018年8月31日 下午2:24:41 <br/>
 *
 * @author liupingan
 * @version
 * @since JDK 1.8
 */
@Component
@Slf4j
public class SystemUtil
{

	@Autowired
	private AccountDbService accountDbService;
	
	@Autowired
	private ServerFrontParamConfig serverParamConfig;

	public UserInfo getCurrentUser()
	{

		ServletRequestAttributes requestAttr = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
		if (requestAttr == null)
		{
			return null;
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		if (request != null)
		{
			return (UserInfo) request.getAttribute("currentUser");
		}
		return null;
	}

	public void setCurrentUser(InputMap<? extends Object> inputMap)
	{

		ServletRequestAttributes requestAttr = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
		if (requestAttr == null)
		{
			return;
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		if (request != null)
		{
			request.setAttribute("currentUser", inputMap.getConfig());
		}
	}

	public void setTenantDb(CloundMap<TenantDb> inputMap)
	{

		ServletRequestAttributes requestAttr = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
		if (requestAttr == null)
		{
			return;
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		if (request != null)
		{
			if (inputMap != null && inputMap.getPayload() != null && inputMap.getPayload().size() > 0)
			{
				// 获取第一个值
				request.setAttribute("tenantDb", inputMap.getPayload().get(0));
			} else
			{
				request.setAttribute("tenantDb", null);
			}
		}
	}
	
	public void setTenantDb(TenantDb bean)
	{

		ServletRequestAttributes requestAttr = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
		if (requestAttr == null)
		{
			return;
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		if (request != null)
		{
			request.setAttribute("tenantDb", bean);
		}
	}

	public void setTenantDb(String userId)
	{
		Map<String,Object> requestMap = new HashMap<>(2);
		requestMap.put("X-NameSpace-Code", serverParamConfig.getXNameSpaceCode());
		requestMap.put("X-MicroService-Name", serverParamConfig.getXMicroServiceName());
		CloundMap<TenantDb> result = accountDbService.getTenantDb(userId,requestMap);
		// 如果为成功，则进行处理
		if (result != null && result.getStatus() != null
				&& CoreproCommonConstant.SUCCESS == result.getStatus().intValue())
		{
			if (result.getPayload() != null && result.getPayload() != null && result.getPayload().size() > 0
					&& result.getPayload().get(0).getDbName() != null
					&& StringUtils.isNotBlank(result.getPayload().get(0).getDbName()))
			{
				setTenantDb(result);
			} else
			{
				setTenantDb(result);
			}
		}
	}

	public void setTenantDb(UserInfo userInfo)
	{

		if (userInfo != null && userInfo.getUserId() != null && StringUtils.isNotBlank(userInfo.getUserId()))
		{
			String userId = userInfo.getUserId().trim();
			setTenantDb(userId);
		}
	}

	public TenantDb getTenantDb()
	{

		ServletRequestAttributes requestAttr = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
		if (requestAttr == null)
		{
			return null;
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		if (request != null)
		{
			return (TenantDb) request.getAttribute("tenantDb");
		}
		return null;
	}

	/**
	 * 设置创建人和修改人
	 * 
	 * @param record
	 * @param isSetCreate
	 */
	public <T> void setCreaterAndModifier(T record, UserInfo currentUser, boolean isSetCreate)
	{
		// UserInfo currentUser = this.getCurrentUser();
		Method method = null;
		try
		{
			if (record != null)
			{
				if (currentUser != null)
				{

					method = record.getClass().getMethod("setModifier", String.class);

					if (method != null)
					{
						method.invoke(record, currentUser.getUserId());
					}
					if (isSetCreate)
					{
						method = record.getClass().getMethod("setCreator", String.class);
						if (method != null)
						{
							method.invoke(record, currentUser.getUserId());
						}

						method = record.getClass().getMethod("setCreateTime", Date.class);
						if (method != null)
						{
							method.invoke(record, new Date());
						}
					}

				}
				method = record.getClass().getMethod("setModifyTime", Date.class);
				if (method != null)
				{
					method.invoke(record, new Date());
				}
			}
		} catch (NoSuchMethodException e)
		{
			log.error(e.getMessage());
		} catch (SecurityException e)
		{
			log.error(e.getMessage());
		} catch (IllegalAccessException e)
		{
			log.error(e.getMessage());
		} catch (IllegalArgumentException e)
		{
			log.error(e.getMessage());
		} catch (InvocationTargetException e)
		{
			log.error(e.getMessage());
		}
	}

	/**
	 * 获取服务器IP地址
	 * 
	 * @return
	 */
	public static String getServerIp()
	{
		String serviceIp = null;
		try
		{
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (netInterfaces.hasMoreElements())
			{
				NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
				ip = (InetAddress) ni.getInetAddresses().nextElement();
				serviceIp = ip.getHostAddress();
				if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
				{
					serviceIp = ip.getHostAddress();
					break;
				} else
				{
					ip = null;
				}
			}
		} catch (SocketException e)
		{
			log.error(e.getMessage());
		}
		return serviceIp;
	}

	public static String getServerLocalIp()
	{
		String serviceIp = null;
		InetAddress address;
		try
		{
			address = InetAddress.getLocalHost();// 获取的是本地的IP地址
													// //PC-20140317PXKX/192.168.0.121
			serviceIp = address.getHostAddress();// 192.168.0.121
		} catch (UnknownHostException e)
		{
			log.error(e.getMessage());
		}

		return serviceIp;
	}
}
