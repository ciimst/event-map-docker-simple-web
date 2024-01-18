package com.imst.event.map.web.db.projections;

public interface EventGroupProjection {
	
	Integer getId();
	String getName();
	String getColor();
	
	Integer getLayerId();
	String getLayerName();
	
	Integer getParentId();
	
	String getDescription();
	
}