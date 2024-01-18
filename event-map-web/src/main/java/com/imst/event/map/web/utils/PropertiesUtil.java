package com.imst.event.map.web.utils;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.imst.event.map.web.constant.Config;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PropertiesUtil {
	
	public static Resource getResource(String fileLocation) {
	    
		Resource applicationPropertiesResource = null;
		
		try {

			File applicationPropertiesFile = new File(fileLocation);
			applicationPropertiesResource = new FileSystemResource(applicationPropertiesFile);
			
			if (!applicationPropertiesFile.exists()) {
				applicationPropertiesResource = new DefaultResourceLoader().getResource(fileLocation);
			}
			
		} catch (Exception e) {
			applicationPropertiesResource = new DefaultResourceLoader().getResource(fileLocation);
		}

	    return applicationPropertiesResource;
	}
	
	private static Properties prop = null;
	
	public static Properties readPropertiesFile() {
	    
		if (prop == null) {

			try(InputStream inputStream = getResource(Config.applicationPropertiesLocation).getInputStream()) {
		    	
		        prop = new Properties();
		        prop.load(inputStream);
		    } catch(Exception e) {
		    	log.catching(e);
		    }	
		}
			
	    return prop;
	}

}
