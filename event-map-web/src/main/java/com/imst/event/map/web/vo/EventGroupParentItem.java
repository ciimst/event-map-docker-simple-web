package com.imst.event.map.web.vo;

import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.web.db.projections.EventGroupProjection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventGroupParentItem {
	
	private Integer id;
	private String name;
	private String color;
	private Integer layerId;
	private String layerName;
	private Integer parentId;
	private String parentName;
	private String description;
	
	public EventGroupParentItem() {
	}
	
	public EventGroupParentItem(Integer id, String name, String color, Integer layerId, String layerName, Integer parentId, String description) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.layerId = layerId;
		this.layerName = layerName;
		this.parentId = parentId;
		this.description = description;
	}
	
	// Veritabanı kullanıyor değiştirilmemeli
	public EventGroupParentItem(EventGroup eventGroup) {
		
		this.id = eventGroup.getId();
		this.name = eventGroup.getName();
		this.color = eventGroup.getColor();
		this.layerId = eventGroup.getLayer() != null ? eventGroup.getLayer().getId() : null;
		this.parentId = eventGroup.getParentId();
		this.description = eventGroup.getDescription();
	}
	
	public EventGroupParentItem(EventGroup eventGroup, Boolean isNotDb) {
		
		this.id = eventGroup.getId();
		this.name = eventGroup.getName();
		this.color = eventGroup.getColor();
		this.layerId = eventGroup.getLayer().getId();
		this.layerName = eventGroup.getLayer().getName();
		this.parentId = eventGroup.getParentId();
		this.description = eventGroup.getDescription();
	}
	
	public static EventGroupParentItem newInstanceForLog(EventGroup eventGroup) {
		
		EventGroupParentItem eventGroupParentItem = new EventGroupParentItem(eventGroup, false);
		
		return eventGroupParentItem;
	}
	
	
	public EventGroupParentItem(EventGroupProjection eventGroupProjection) {
		
		this.id = eventGroupProjection.getId();
		this.name = eventGroupProjection.getName();
		this.color = eventGroupProjection.getColor();
		this.layerId = eventGroupProjection.getLayerId();
		this.layerName = eventGroupProjection.getLayerName();
		this.parentId = eventGroupProjection.getParentId();
		this.description = eventGroupProjection.getDescription();
		
		
	}
}
