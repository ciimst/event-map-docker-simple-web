package com.imst.event.map.web.vo;

import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.web.db.projections.EventTypeProjection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventTypeItem {	
	
	private Integer id;
	private String name;
	private String image;
	private String code;
	
	public EventTypeItem(EventItemForEventTypeSelectBox eventType) {
		this.id = eventType.getId();
		this.name = eventType.getName();
		this.image = eventType.getImage();
		this.code = eventType.getCode();
	}
	
	public EventTypeItem(EventType eventType) {
		this.id = eventType.getId();
		this.name = eventType.getName();
		this.image = eventType.getImage();
		this.code = eventType.getCode();
	}
	
	public EventTypeItem(EventTypeProjection eventTypeProjection) {
		this.id = eventTypeProjection.getId();
		this.name = eventTypeProjection.getName();
//		this.image = eventTypeProjection.getImage();
		this.code = eventTypeProjection.getCode();
	}
	
	public EventTypeItem() {
	}
	

}
