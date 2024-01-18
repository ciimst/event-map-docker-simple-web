package com.imst.event.map.web.db.projections;

public interface EventsTimeCountProjection {
	
	
	Integer getId();
	Long getEventCount();
	Integer getEventDay();
	Integer getEventMonth();
	Integer getEventYear();
	Integer getEventGroupId();
}
