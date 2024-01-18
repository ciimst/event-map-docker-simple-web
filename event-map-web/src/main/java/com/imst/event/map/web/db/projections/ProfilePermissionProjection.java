package com.imst.event.map.web.db.projections;

import org.springframework.beans.factory.annotation.Value;

public interface ProfilePermissionProjection {
	
	Integer getId();
	@Value("#{target.profile.name}")
	String getProfileName();
	@Value("#{target.profile.description}")
	String getProfileDescription();
	@Value("#{target.permission.description}")
	String getPermissionDescription();
	@Value("#{target.profile.groupName}")
	String getPermissionGroupName();
}
