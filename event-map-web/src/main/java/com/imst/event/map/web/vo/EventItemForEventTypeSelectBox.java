package com.imst.event.map.web.vo;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventItemForEventTypeSelectBox {
	

	@Column(name = "eventType.id")
	private Integer id;
	@Column(name = "eventType.name")
	private String name;
	@Column(name = "eventType.image")
	private String image;
	@Column(name = "eventType.code")
	private String code;
	
	
}
