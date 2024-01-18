package com.imst.event.map.web.vo;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventRequestItem {
	
	private String firstScrollDate;
	private String time;
	private String layerId;
	private Map<String, Integer> lastEventIdMap;
	private String eventSearchText;
	private List<Integer> eventTypeIdSearch;	
	private String eventSearchCity;
	private String eventSearchCountry;
	private List<String> eventGroupdbNameIdList;
	private String startDateStr;
	private String endDateStr;
	
	public EventRequestItem() {
	}
	
}
