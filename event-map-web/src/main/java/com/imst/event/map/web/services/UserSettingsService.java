package com.imst.event.map.web.services;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.repositories.UserSettingsRepository;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.UserSettingsUtil;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserSettings;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UserSettingsService {
	
	@Autowired
	private UserSettingsRepository userSettingsRepository;

	public UserSettingsUtil updateUserSettingsCacheAndGet() {
		
		UserSettingsUtil userSettingsUtil = new UserSettingsUtil();
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		try {
			
			HashMap<String, String> userSettingsMap = new HashMap<>();;
			
			List<UserSettings> allUserSettingsByUser = userSettingsRepository.findAllByUser(user);
			for (UserSettings userSettings : allUserSettingsByUser) {
				
				userSettingsMap.put(userSettings.getUserSettingsType().getSettingsKey(), userSettings.getSettingsValue());
			}
			
			userSettingsUtil = new UserSettingsUtil(userSettingsMap);
		}
		catch (Exception e) {
			
			log.debug(e);
		}
		
		return userSettingsUtil;
	}
	
	
}
