package com.imst.event.map.web.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KeyItemWrapper {

	private List<EventTypeItem> eventTypeList;
	private List<EventGroupItem> eventGroupList;
	private List<MapAreaGroupItem> mapAreaGroupList;

}
