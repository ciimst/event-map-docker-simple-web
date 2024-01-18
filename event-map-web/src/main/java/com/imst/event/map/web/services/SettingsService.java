package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.repositories.SettingsRepository;
import com.imst.event.map.web.utils.SettingsUtil;
import com.imst.event.map.hibernate.entity.Settings;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SettingsService {
	
	@Autowired
	private SettingsRepository settingsRepository;
	
	public void updateSettingsCache() {
		
		try {
			
			List<Settings> all = settingsRepository.findAll();
			for (Settings settings : all) {
				
				SettingsUtil.settings.put(settings.getSettingsKey(), settings.getSettingsValue());
			}
		}
		catch (Exception e) {
			
			log.debug(e);
		}
		
	}
	
	
}
