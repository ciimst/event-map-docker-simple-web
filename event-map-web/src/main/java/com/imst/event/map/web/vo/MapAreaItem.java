package com.imst.event.map.web.vo;

import javax.persistence.Column;

import com.imst.event.map.hibernate.entity.MapArea;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MapAreaItem {
	
	private Integer id;
	private String title;
	private String coordinateInfo;
	@Column(name = "mapAreaGroup.color")
	private String color;
	@Column(name = "mapAreaGroup.layer.id")
	private Integer layerId;
	
	public MapAreaItem(MapArea mapArea) {
		
		this.id = mapArea.getId();
		this.title = mapArea.getTitle();
		this.coordinateInfo = mapArea.getCoordinateInfo();
		this.color = mapArea.getMapAreaGroup().getColor();
		this.layerId = mapArea.getMapAreaGroup().getLayer().getId();
		
	}
}
