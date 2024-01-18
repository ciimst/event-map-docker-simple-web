package com.imst.event.map.hibernate.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Settings generated by hbm2java
 */
@Entity
@Table(name = "user_settings_type", schema = "public")
public class UserSettingsType implements java.io.Serializable {

	private Integer id;
	private Integer order;
	private String settingsKey;
	private String settingsValue;
	private String description;
	private String groupName;
	private String type;
	private Boolean islayer;

	public UserSettingsType() {
	}

	public UserSettingsType(String settingsKey) {
		this.settingsKey = settingsKey;
	}

	public UserSettingsType(Integer order, String settingsKey, String settingsValue, String description, String groupName, String type, Boolean islayer) {
		this.order = order;
		this.settingsKey = settingsKey;
		this.settingsValue = settingsValue;
		this.description = description;
		this.groupName = groupName;
		this.type = type;
		this.islayer = islayer;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "\"order\"")
	public Integer getOrder() {
		return this.order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Column(name = "settings_key", unique = true, nullable = false, length = 64)
	public String getSettingsKey() {
		return this.settingsKey;
	}

	public void setSettingsKey(String settingsKey) {
		this.settingsKey = settingsKey;
	}

	@Column(name = "settings_value", length = 2048)
	public String getSettingsValue() {
		return this.settingsValue;
	}

	public void setSettingsValue(String settingsValue) {
		this.settingsValue = settingsValue;
	}

	@Column(name = "description", length = 2048)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "group_name", length = 64)
	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Column(name = "type", length = 32)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "is_layer")
	@org.hibernate.annotations.ColumnDefault("true")
	public Boolean getIsLayer() {
		return this.islayer;
	}

	public void setIsLayer(Boolean islayer) {
		this.islayer = islayer;
	}

}
