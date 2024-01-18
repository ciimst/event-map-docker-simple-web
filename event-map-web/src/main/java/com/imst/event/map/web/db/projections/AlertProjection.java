package com.imst.event.map.web.db.projections;

import java.util.Date;

import com.vividsolutions.jts.geom.Polygon;

public interface AlertProjection {
	
	Integer getId();
	String getName();
	Date getCreateDate();
	Date getUpdateDate();
	String getQuery();
	
	
	Integer getEventTypeId();
	
	
	Integer getEventGroupId();
	
	
	String getReservedKey();
	String getReservedType();
	String getReservedId();
	String getReservedLink();
	String getEventGroupDbName();
	String getlayerId();
	String getSharedBy();
	String getColor();
	
	Polygon getPolygonCoordinate();
	
	
	
	
}
