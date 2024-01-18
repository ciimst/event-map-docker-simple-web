package com.imst.event.map.web.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {
	
	@Value("${javax.net.ssl.trustStore}")
	private String trustStore;
	
	@Value("${javax.net.ssl.trustStorePassword}")
	private String trustStorePassword;
	
	//tomcat servlet timeoutu override ediyor
	@Bean
	@ConfigurationProperties(prefix = "custom.servlet.session")
	public CustomHttpSessionListener customHttpSessionListener() {
		return new CustomHttpSessionListener();
	}
	
	
	@Bean
	public void trustedCertificates() throws IOException {
	    
	    System.setProperty("javax.net.ssl.trustStore", trustStore);
	    System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
	}
}
