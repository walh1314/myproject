package com.foxconn.core.pro.server.rule.engine.front.config.ds;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.foxconn.core.pro.server.rule.engine.front.interceptor.MyInterceptor;

@Configuration
// 扫描 Mapper 接口并容器管理
@MapperScan(basePackages = DataSourceConfig.PACKAGE, sqlSessionFactoryRef = "ruleSqlSessionFactory")
public class DataSourceConfig
{

	// 精确到 master 目录，以便跟其他数据源隔离
	static final String PACKAGE = "com.foxconn.core.pro.server.rule.engine.front.mapper";
	static final String MAPPER_LOCATION = "classpath:mapper/rule/*.xml";

	@Value("${rule.datasource.url}")
	private String url;

	@Value("${rule.datasource.username}")
	private String user;

	@Value("${rule.datasource.password}")
	private String password;

	@Value("${rule.datasource.driverClassName}")
	private String driverClass;

	@Bean(name = "ruleDataSource")
	@Primary
	public DataSource ruleDataSource()
	{
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		return dataSource;
	}

	@Bean(name = "ruleTransactionManager")
	@Primary
	public DataSourceTransactionManager ruleTransactionManager()
	{
		return new DataSourceTransactionManager(ruleDataSource());
	}

	@Bean(name = "ruleSqlSessionFactory")
	@Primary
	public SqlSessionFactory ruleSqlSessionFactory(@Qualifier("ruleDataSource") DataSource ruleDataSource)
			throws Exception
	{
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(ruleDataSource);
		sessionFactory.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources(DataSourceConfig.MAPPER_LOCATION));
		sessionFactory.setPlugins(new Interceptor[]{myInterceptor()});
		return sessionFactory.getObject();
	}

	@Bean
	public Interceptor myInterceptor()
	{
		return new MyInterceptor();
	}

}