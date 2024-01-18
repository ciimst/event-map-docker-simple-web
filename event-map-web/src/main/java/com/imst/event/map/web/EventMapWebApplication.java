package com.imst.event.map.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.imst.event.map.web.constant.Config;
import com.imst.event.map.web.utils.ApplicationContextUtils;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@SpringBootApplication
@EnableScheduling
@EnableRedisHttpSession(redisNamespace = "${spring.session.redis.namespace}", maxInactiveIntervalInSeconds = 1800)
public class EventMapWebApplication {

	public static void main(String[] args) {
		
		Config.getApplicationPropertiesLocation(args);
		SpringApplication.run(EventMapWebApplication.class, args);
	}
	
	@Bean
	LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}
	

	
	@Autowired
	public void context(ApplicationContext context) {
		
		ApplicationContextUtils.setApplicationContext(context);
	}
	
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		ApplicationContextUtils.setMessageSource(messageSource);
	}
	
	@Value("${datatable.page.length}")
	private void setTableLength(Integer length) {
		
		ApplicationContextUtils.setTableLength(length);
	}
	
}
