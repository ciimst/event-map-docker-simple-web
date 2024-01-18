package com.imst.event.map.web.vo;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventGroupItem {
	
	private Integer id;
	private String name;
	private String color;
	private String description;
	private Integer parentId;
	@Column(name = "layer.id")
	private Integer layerId;
	
	@Column(name = "name")
	private String dbName;
}
