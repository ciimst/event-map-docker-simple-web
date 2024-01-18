package com.imst.event.map.web.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SidebarEventItemWrapperForHeatMap {

	private SidebarEventItemForHeatMap event;
	private String dbName;
	private String eventIdDbName;
	private List<SidebarAlertEventItem> alertList;
	
	
	public SidebarEventItemWrapperForHeatMap(SidebarEventItemForHeatMap event, String dbName) {
		super();
		this.event = event;
		this.dbName = dbName;
		this.eventIdDbName = prepareEventIdDbName(event.getId(), dbName);
	}
	
	private String prepareEventIdDbName(Integer eventId,String dbName) {
		String eventIdDbName = String.format("%s_%s", eventId, dbName);
		
		return eventIdDbName;
	}
	
	
}
