package com.imst.event.map.web.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SidebarEventItemWrapper {

	private SidebarEventItem event;
	private List<SidebarEventMediaItem> mediaList;
	private List<SidebarTagItem> tagList;
	private String dbName;
	private String eventIdDbName;
	private List<SidebarAlertEventItem> alertList;
	
	
	public SidebarEventItemWrapper(SidebarEventItem event, List<SidebarEventMediaItem> mediaList,
			List<SidebarTagItem> tagList, String dbName) {
		super();
		this.event = event;
		this.mediaList = mediaList;
		this.tagList = tagList;
		this.dbName = dbName;
		this.eventIdDbName = prepareEventIdDbName(event.getId(), dbName);
	}
	
	private String prepareEventIdDbName(Integer eventId,String dbName) {
		String eventIdDbName = String.format("%s_%s", eventId, dbName);
		
		return eventIdDbName;
	}
	
	
}
