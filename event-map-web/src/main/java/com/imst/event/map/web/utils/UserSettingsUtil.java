package com.imst.event.map.web.utils;

import java.util.HashMap;

import com.imst.event.map.web.constant.UserSettingsTypeE;

public class UserSettingsUtil {

	private HashMap<String, String> userSettings;
	
	public UserSettingsUtil() {
		userSettings = new HashMap<>();
	}

	public UserSettingsUtil(HashMap<String, String> userSettings) {
		this.userSettings = userSettings;
	}
	
	public boolean hasSettings(UserSettingsTypeE userSettingsTypeE) {

		return userSettings.get(userSettingsTypeE.getName()) != null;
	}
	
	public String getString(UserSettingsTypeE userSettingsTypeE) {
		
		return userSettings.get(userSettingsTypeE.getName());
	}

	public int getInteger(UserSettingsTypeE userSettingsTypeE) {

		return Integer.parseInt(userSettings.get(userSettingsTypeE.getName()));
	}
	
	public Boolean getBoolean(UserSettingsTypeE userSettingsTypeE) {
	    	
			return Boolean.parseBoolean(userSettings.get(userSettingsTypeE.getName()));
	}
}
