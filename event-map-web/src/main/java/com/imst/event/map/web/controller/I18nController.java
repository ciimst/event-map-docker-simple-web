package com.imst.event.map.web.controller;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/i18n")
public class I18nController {
	
	private static ResourceLoader resourceLoader = new DefaultResourceLoader();
	private static PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
	
	@PostMapping("/all")
	public Properties getMessages() {
		
		return getMessages(LocaleContextHolder.getLocale().getLanguage());
	}
	
	private Properties getMessages(String locale)  {
		
		try {
			
			String filename = String.format("/i18n/messages_%s.properties", locale);
			Properties props = getProps(filename);
			
			return props;
			
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	
	private static Properties getProps(String filename) throws Exception {
		
		Properties props = null;
		Resource resource = resourceLoader.getResource(filename);
		if (resource.exists()) {
			
			InputStream is = resource.getInputStream();
			props = new Properties();
			propertiesPersister.load(props, new InputStreamReader(is, StandardCharsets.UTF_8));
		}
		
		return props;
	}
}
