package com.imst.event.map.web.constant;

public class Config {
	public static final int DB_COUNT = 1;
	
	public static String applicationPropertiesLocation = "classpath:application.properties";
	
	public static void getApplicationPropertiesLocation(String[] args) {
		
		for (String arg : args) {
			if(arg.startsWith("--spring.config.location=")) {
				String appPropertiesLocation = arg.replace("--spring.config.location=", "");
				if(!appPropertiesLocation.trim().equals("")) {
					Config.applicationPropertiesLocation = appPropertiesLocation.trim().replace("file://", "");
				}
			}
		}
	}
}
