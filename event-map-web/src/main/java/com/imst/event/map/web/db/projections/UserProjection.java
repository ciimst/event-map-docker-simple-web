package com.imst.event.map.web.db.projections;

import org.springframework.beans.factory.annotation.Value;

public interface UserProjection {
	
	Integer getId();
	String getUsername();
	String getName();
	@Value("#{target.profile.name}")
	String getProfileName();
}
