package com.imst.event.map.web.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventItemForBlackList {
	
	private Integer id;
	private Integer eventTypeId;
	private List<Integer> eventGroupIdList;
	private Integer layerId;
	private String blackListTag;
	private Integer blackListId;
	private Boolean state;
	
	
	public EventItemForBlackList() {
	
	}
	
	//bunu silme
	public EventItemForBlackList(Integer id, Integer eventTypeId, List<Integer> eventGroupIdList, Integer layerId, String blackListTag, Boolean state) {
		
		this.id = id;
		this.eventTypeId = eventTypeId;
		this.eventGroupIdList = eventGroupIdList;
		this.layerId = layerId;
		this.blackListTag = blackListTag;
		this.state = state;
	}
	
	
}
