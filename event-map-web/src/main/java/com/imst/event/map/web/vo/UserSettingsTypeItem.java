
package com.imst.event.map.web.vo;

import java.util.List;

import javax.persistence.Column;

import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.imst.event.map.hibernate.entity.UserSettings;
import com.imst.event.map.hibernate.entity.UserSettingsType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettingsTypeItem {
	private Integer id;
	private Integer order;
	private String settingsKey;
	private String description;
	private String groupName;
	private String type;
	private Boolean isLayer;
	private String settingsValue;

	@Column(name = "userSettingsType")
	private UserSettingsType userSettingsType;
	
	@Column(name = "userSettingsType.id")
	private Integer userSettingsTypeId;
	
//	@Column(name = "layer")
//	private Layer layer;
	@Column(name = "layer.name")
	private String layerName;
	@Column(name = "layer.id")
	private Integer layerId;
//	@Column(name = "user")
//	private User user;
	@Column(name = "user.id")
	private Integer userId;
	
	private String layerGuid;
	
	private List<JSONObject> settingsListValue;
	
	public UserSettingsTypeItem() {
	
	}
	
	public UserSettingsTypeItem(UserSettings userSettings) {
		
		this.id = userSettings.getId();
		this.order = userSettings.getUserSettingsType().getOrder() != null ? userSettings.getUserSettingsType().getOrder() : null;
		this.settingsKey = userSettings.getUserSettingsType().getSettingsKey();
		this.description = userSettings.getUserSettingsType().getDescription();
		this.groupName = userSettings.getUserSettingsType().getGroupName();
		this.type = userSettings.getUserSettingsType().getType();
		this.isLayer = userSettings.getUserSettingsType().getIsLayer();
		this.userSettingsTypeId = userSettings.getUserSettingsType().getId();
		this.settingsValue = userSettings.getSettingsValue();
		//this.userSettingsType = userSettings.getUserSettingsType();
		//this.layer = userSettings.getLayer();
		this.layerName = userSettings.getLayer() != null ? userSettings.getLayer().getName() : null;
		this.layerId = userSettings.getLayer() != null ? userSettings.getLayer().getId() : null;
		//this.user = userSettings.getUser();
		this.userId = userSettings.getUser().getId();

	}
	
	public static UserSettingsTypeItem newInstanceForLog(UserSettings userSettings) {
		
		UserSettingsTypeItem userSettingsTypeItem = new UserSettingsTypeItem(userSettings);
		
		return userSettingsTypeItem;
	}
	
	
}
