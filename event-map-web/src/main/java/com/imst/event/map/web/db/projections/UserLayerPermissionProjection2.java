package com.imst.event.map.web.db.projections;

import org.springframework.beans.factory.annotation.Value;

public interface UserLayerPermissionProjection2 {

	Integer getId();
	
	@Value("#{target.user.id}")
	Integer getUserId();
	
	@Value("#{target.user.name}")
	String getUserName();
	
	@Value("#{target.layer.id}")
	Integer getLayerId();
	
	@Value("#{target.layer.name}")
	String getLayerName();
	
	
	@Value("#{target.layer.guid}")
	String getLayerGuid();
	
	
	@Value("#{target.layer.isTemp}")
	Boolean getLayerIsTemp();
}
