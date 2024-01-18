package com.imst.event.map.web.vo;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SidebarAllWrapper {

	private List<SidebarEventItemWrapper> eventWrapperList;
	private Map<String, Integer> lastEventIdMap;
	
	private Long lastScrollDate;
	private Long firstScrollDate;
	

	public SidebarAllWrapper() {
		
	}

	public SidebarAllWrapper(List<SidebarEventItemWrapper> eventWrapperList, Map<String, Integer> lastEventIdMap) {
		super();
		this.eventWrapperList = eventWrapperList;
		this.lastEventIdMap = lastEventIdMap;
	}
	
}
