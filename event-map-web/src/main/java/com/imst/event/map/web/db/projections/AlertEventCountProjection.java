package com.imst.event.map.web.db.projections;

import org.springframework.beans.factory.annotation.Value;

public interface AlertEventCountProjection {
	
	@Value("#{target.count}")
	long getCount();
	
	
	@Value("#{target.alertId}")
	Integer getAlertId();

}
