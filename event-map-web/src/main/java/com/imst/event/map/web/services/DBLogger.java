package com.imst.event.map.web.services;

import java.sql.Timestamp;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imst.event.map.hibernate.entity.Log;
import com.imst.event.map.web.constant.LogTypeE;
import com.imst.event.map.web.db.repositories.LogRepository;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DBLogger {
	
	@Autowired
	private HeaderExtractorService headerExtractorService;
	@Autowired
	private LogRepository logRepository;
	
	private ExtendedLogger fileLogger;
	
	public DBLogger() {
		
		fileLogger  = LogManager.getContext(false).getLogger("com.imst.event.map.admin");
	}
	
	public ExtendedLogger getLogger() {
		
		return fileLogger;
	}
	
	public void logWithUser(UserItemDetails user, String description, LogTypeE logTypeE) {
		
		try {
			
			String username = "unknown";
			Integer userId = null;

			if (user != null && !StringUtils.isEmpty(user.getUsername())) {
				username = user.getUsername();
				userId = user.getUserId();
			}
			
			String searchable = "";
			if (!StringUtils.isBlank(description)) {
				
				searchable = description
						.replaceAll("\\{", "")
						.replaceAll("}", "")
						.replaceAll("]", "")
						.replaceAll("\\[", "")
						.replaceAll("\\(", "")
						.replaceAll("\\)", "")
						.toLowerCase(Locale.ENGLISH)
						.toUpperCase(Locale.ENGLISH)
				;
			}
			
			Timestamp nowT = DateUtils.nowT();
			Log dbLog = new Log();
			dbLog.setUsername(username);
			dbLog.setUserId(userId);
			dbLog.setDescription(description);
			dbLog.setSearchableDescription(searchable);
			dbLog.setFkLogTypeId(logTypeE.getId());
			dbLog.setIp(headerExtractorService.getClientIpAddress());
			dbLog.setCreateDate(nowT);
			
			log(dbLog);
			
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void logWithUser(UserItemDetails user, Object description, LogTypeE logTypeE) {
		
		String jsonDescription = new Gson().toJson(description);
		logWithUser(user, jsonDescription, logTypeE);
	}
	
	public void log(Object description, LogTypeE logTypeE) {
		
		String jsonDescription = new Gson().toJson(description);
		log(jsonDescription, logTypeE);
	}
	
	public void log(String description, LogTypeE logTypeE) {
		
		UserItemDetails user = ApplicationContextUtils.getUser();
		logWithUser(user, description, logTypeE);
	}
	
	public void log(Log dbLog) {
		
		if (dbLog == null) {
			return;
		}
		
		getLogger().log(Level.getLevel("USER"), new Gson().toJson(dbLog));
		logRepository.save(dbLog);
	}
	
}
