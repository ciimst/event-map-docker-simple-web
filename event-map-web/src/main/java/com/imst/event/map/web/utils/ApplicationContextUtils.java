package com.imst.event.map.web.utils;



import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;

import com.imst.event.map.web.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.web.security.UserItemDetails;

import java.util.Locale;


public class ApplicationContextUtils {
	
	private static Integer TABLE_LENGTH_MAX;
	private static ApplicationContext applicationContext;
	private static MessageSource messageSource;
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public static void setApplicationContext(ApplicationContext applicationContext) {
		ApplicationContextUtils.applicationContext = applicationContext;
	}
	
	public static void setMessageSource(MessageSource messageSource) {
		ApplicationContextUtils.messageSource = messageSource;
	}
	
	public static <T> T getBean(Class<T> clazz){
		
		return applicationContext.getBean(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz, String beanName){
		
		return (T) applicationContext.getBean(beanName);
	}
	
	
	public static String getMessage(String key){
		return getMessage(key, LocaleContextHolder.getLocale());
	}
	
	public static String getMessage(String key, Locale locale) {
		String message = key;
		try {
			message = messageSource.getMessage(key, new Object [] {}, locale);
		}
		catch (NoSuchMessageException e) {}
		return message;
	}
	
	public static UserItemDetails getUser() {
		return (UserItemDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	public static String getProperty(String propertyName) {
		return applicationContext.getEnvironment().getProperty(propertyName);
	}
	
	public static Integer getTableLength(DatatablesCriterias criterias) {
		return (criterias.getLength()  > TABLE_LENGTH_MAX  || criterias.getLength() < 0) ? TABLE_LENGTH_MAX : criterias.getLength();
	}
	
	public static void setTableLength(Integer length) {
		TABLE_LENGTH_MAX = length;
	}
	
	
}
