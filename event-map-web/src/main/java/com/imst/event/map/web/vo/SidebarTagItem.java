package com.imst.event.map.web.vo;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SidebarTagItem {
	
	@Column(name = "event.id")
	private Integer eventId;
	@Column(name = "tag.name")
	private String tagName;
	
	public SidebarTagItem(Integer eventId, String tagName) {
		super();
		this.eventId = eventId;
		this.tagName = tagName;
	}
	
	public Integer getEventId() {
		return eventId;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	
}
