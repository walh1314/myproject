package com.foxconn.core.pro.server.rule.engine.core.config.template;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "rule.template")
@Setter
@Getter
public class RuleTemplateProperties
{
	RuleConditionTemplateProperties condition;
	
	RuleFieldTemplateProperties field;
}