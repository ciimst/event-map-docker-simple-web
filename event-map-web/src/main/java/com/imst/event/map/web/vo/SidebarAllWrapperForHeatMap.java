package com.imst.event.map.web.vo;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SidebarAllWrapperForHeatMap {

	private List<SidebarEventItemWrapperForHeatMap> eventWrapperList;
	private Map<String, Integer> lastEventIdMap;
	
	private Long lastScrollDate;
	private Long firstScrollDate;
	
	public SidebarAllWrapperForHeatMap() {
		
	}

	public SidebarAllWrapperForHeatMap(List<SidebarEventItemWrapperForHeatMap> eventWrapperList, Map<String, Integer> lastEventIdMap) {
		super();
		this.eventWrapperList = eventWrapperList;
		this.lastEventIdMap = lastEventIdMap;
	}
	
}
