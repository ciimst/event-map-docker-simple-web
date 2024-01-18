package com.imst.event.map.web.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class EventTableViewAllWrapper {

	private List<EventTableViewItemWrapper> eventWrapperList;
	

	public EventTableViewAllWrapper() {
		
	}

	public EventTableViewAllWrapper(List<EventTableViewItemWrapper> eventWrapperList) {
		
		this.eventWrapperList = eventWrapperList;
	}
	
}
