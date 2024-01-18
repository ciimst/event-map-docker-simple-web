package com.imst.event.map.web.vo;

import com.imst.event.map.hibernate.entity.Settings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsItem {
	private Integer id;
	private String settingsKey;
	private String settingsValue;
	
	public SettingsItem(Settings settings) {
		
		this.id = settings.getId();
		this.settingsKey = settings.getSettingsKey();
		this.settingsValue = settings.getSettingsValue();
	}
	
	public static SettingsItem newInstanceForLog(Settings settings) {
		
		SettingsItem settingsItem = new SettingsItem(settings);
		
		return settingsItem;
	}
	
	
}
