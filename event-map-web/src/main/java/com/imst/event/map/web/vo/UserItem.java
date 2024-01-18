package com.imst.event.map.web.vo;

import com.imst.event.map.hibernate.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserItem {
	
	private Integer id;
	private String name;
	private String username;
	private Integer profileId;
	private String profileName;
	
	public UserItem() {
	
	}
	
	public UserItem(Integer id, String name, String username, Integer profileId, String profileName) {
		
		this.id = id;
		this.name = name;
		this.username = username;
		this.profileId = profileId;
		this.profileName = profileName;
	}
	
	public UserItem(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.username = user.getUsername();
		this.profileId = user.getProfile().getId();
		this.profileName = user.getProfile().getName();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
}
