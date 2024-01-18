package com.imst.event.map.web.db.projections;

import org.springframework.beans.factory.annotation.Value;

public interface UserEventGroupPermissionProjection2 {

	Integer getId();	
	
	@Value("#{target.user.id}")
	Integer getUserId();
	
	@Value("#{target.eventGroup.id}")
	Integer getEventGroupId();
	
	@Value("#{target.eventGroup.name}")
	String getEventGroupName();
	
	@Value("#{target.user.name}")
	String getUserName();
	
	@Value("#{target.eventGroup.layer.id}")
	Integer getLayerId();
	
	@Value("#{target.eventGroup.layer.name}")
	String getLayerName();
}
