package com.imst.event.map.web.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.imst.event.map.web.constant.MultitenantDatabaseE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.utils.DatabasePasswordEncryptUtils;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MultitenantConfiguration {
	
	private static MultitenantConfiguration multitenantConfiguration;

	private MultitenantConfiguration() {
		
		dataSourceFromFile();
	}
	
	public static synchronized MultitenantConfiguration getInstance() {
		
		if(multitenantConfiguration == null) {
			multitenantConfiguration = new MultitenantConfiguration();
		}
		
		return multitenantConfiguration;
	}
	
	@SuppressWarnings("unchecked")
	private void dataSourceFromFile() {
		
		log.info("MultitenantConfiguration called");
		
	    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	    Resource[] resources;
	    List<InputStream> configInputStreamList = new ArrayList<>();
	    try {
	        resources = resolver.getResources("classpath:/tenants/*");
	        for (Resource resource : resources) {
	        	
	        	log.info("File found for multitenant datasource config : " + resource.getFilename());
	        	
				InputStream inputStream = resource.getInputStream();
				configInputStreamList.add(inputStream);
			}
	    }catch (FileNotFoundException e) {
	    	log.warn(e);
		}catch (IOException e) {
	        log.catching(e);
	    }
		
		for(InputStream configInputStream : configInputStreamList) {
			
			Properties tenantProperties = new Properties();
			DataSourceBuilder<HikariDataSource> dataSourceBuilder = (DataSourceBuilder<HikariDataSource>) DataSourceBuilder.create();
			
			
			try {
				
				tenantProperties.load(configInputStream);
				
				String password = tenantProperties.getProperty("spring.datasource.password");
				password = DatabasePasswordEncryptUtils.decriptedPasswordFromApplicationProperties(password);	
				
				
				String databaseName = tenantProperties.getProperty("database");
				String tenantId = tenantProperties.getProperty("name");			

				dataSourceBuilder
					.driverClassName(tenantProperties.getProperty("spring.datasource.driver-class-name"))
					.url(tenantProperties.getProperty("spring.datasource.jdbc-url"))
					.username(tenantProperties.getProperty("spring.datasource.username"))
					.password(password)
					.type(HikariDataSource.class)
					;


				Integer minimumPoolSize = 1;
				try {
					minimumPoolSize = Integer.parseInt(tenantProperties.getProperty("spring.datasource.minimum-idle"));
				} catch (Exception e) {
				}

				Integer maximumPoolSize = 2;
				try {
					maximumPoolSize = Integer.parseInt(tenantProperties.getProperty("spring.datasource.maximum-pool-size"));
				} catch (Exception e) {
				}

				HikariDataSource ds = dataSourceBuilder.build();

				ds.setMinimumIdle(minimumPoolSize);
				ds.setMaximumPoolSize(maximumPoolSize);

				Statics.tenantDataSourceInfoMap.put(tenantId, new DataSourceInfo(tenantId, MultitenantDatabaseE.getMultitenantDatabaseE(databaseName), ds));
				
			} catch (IOException e) {
				
				log.debug(e.getMessage());
			}
		}
	}
	
}
