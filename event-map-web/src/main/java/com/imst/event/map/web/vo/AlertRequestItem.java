package com.imst.event.map.web.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlertRequestItem {
	
	private Integer id;
	private String layerId;

	private LatLongItem[] latLongItemArr;
	private Boolean isCircle;
	
	private String name;
	private String query;
	private String reservedId;
	private String reservedLink;
	private String reservedType;
	private String reservedKey;	
	private Integer eventGroupId;
	private Integer eventTypeId;
	private String eventGroupDbName; 
	private String color;
	
	public AlertRequestItem() {
	}
	
}
