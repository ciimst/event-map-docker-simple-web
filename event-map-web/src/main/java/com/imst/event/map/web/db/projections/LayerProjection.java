package com.imst.event.map.web.db.projections;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public interface LayerProjection {
	
	Integer getId();
	String getName();
	
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getCreateDate();
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	Timestamp getUpdateDate();
	
}



