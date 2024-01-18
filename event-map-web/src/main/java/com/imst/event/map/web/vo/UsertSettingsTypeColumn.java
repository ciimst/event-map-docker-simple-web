package com.imst.event.map.web.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsertSettingsTypeColumn {

	private String city;
	private String country;
	private String titleAndSpotAndDescription;
	private String eventTypeId;
	private String alertEvent;
	private String startDate;
	private String endDate;
	private String mapCoordinate;
	private String mapZoom;
	private boolean existEventGroups;
	private boolean anyExistField;
	
	private List<UserSettingsEventGroupItem> eventGroupList;
	
	public UsertSettingsTypeColumn() {
		
	}
}
