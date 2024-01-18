package com.imst.event.map.web.vo;

import javax.persistence.Column;

import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserSettings;
import com.imst.event.map.hibernate.entity.UserSettingsType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettingsItem {
	private Integer id;
	private String settingsKey;
	@Column(name = "userSettingsType")
	private UserSettingsType userSettingsType;
	@Column(name = "layer")
	private Layer layer;
	@Column(name = "layer.name")
	private String layerName;
	@Column(name = "layer.id")
	private Integer layerId;
	@Column(name = "user")
	private User user;
	@Column(name = "user.id")
	private Integer userId;
	private String settingsValue;
	
	public UserSettingsItem(UserSettings userSettings) {
		
		this.id = userSettings.getId();
		this.settingsKey = userSettings.getUserSettingsType().getSettingsKey();
		this.settingsValue = userSettings.getSettingsValue();
		this.layerName = userSettings.getLayer() != null ? userSettings.getLayer().getName() : null;
		this.layerId = userSettings.getLayer() != null ? userSettings.getLayer().getId() : null;
		this.userId = userSettings.getUser().getId();
	}
	
	public static UserSettingsItem newInstanceForLog(UserSettings userSettings) {
		
		UserSettingsItem userSettingsItem = new UserSettingsItem(userSettings);
		
		return userSettingsItem;
	}
	
	
}
