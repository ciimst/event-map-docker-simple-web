package com.imst.event.map.web.constant;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.imst.event.map.web.vo.EventColumnItem;

public enum EventTableColumnE {
	
	Id(1),
	TITLE(2), 
	SPOT(3),
	DESCRIPTION(4),
	EVENTDATE(5),
	EVENTTYPE(6),
	COUNTRY(7),
	CITY(8),
	LATITUDE(9),
	EVENTGROUP(10),
	STATE(11),
	RESERVEDKEY(12),
	RESERVEDTYPE(13),
	RESERVEDID(14),
	RESERVEDLINK(15),
	BLACKLISTTAG(16),
	RESERVED1(17),
	RESERVED2(18),
	RESERVED3(19),
	RESERVED4(20),
	RESERVED5(21),
	LAYER(22),
	LONGITUDE(23)
	
	;
	
	private Integer value; 
	
	private EventTableColumnE(Integer value) {
		this.value = value;
	}
	

	private static HashMap<Integer, EventTableColumnE> map = new HashMap<>();
	private static List<EventColumnItem> list = new ArrayList<>();
	
	public Integer getValue() {
		return value;
	}
	
	public static EventTableColumnE getSettings(Integer key) {
		return map.get(key) == null ? null : map.get(key);
	}
	
	static {
		
		for (EventTableColumnE settingsE : EventTableColumnE.values()) {
			map.put(settingsE.getValue(), settingsE);
		}
	}
	
	public static List<EventColumnItem> getEventColumnList() {
		
		if(list.size() > 0) {
			return list;
		}
		
		for (EventTableColumnE settingsE : EventTableColumnE.values()) {
			list.add(new EventColumnItem(settingsE.getValue(), settingsE.name()) );
		}
		
		return list;
	}
	
	


	
}
