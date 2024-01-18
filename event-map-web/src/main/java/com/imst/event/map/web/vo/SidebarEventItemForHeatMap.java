package com.imst.event.map.web.vo;

import java.util.Date;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SidebarEventItemForHeatMap {

	private Integer id;
	@Column(name = "eventGroup.name")
	private String groupName;
	@Column(name = "eventGroup.id")
	private Integer groupId;
	@Column(name = "eventGroup.layer.id")
	private Integer layerId;
	@Column(name = "eventGroup.color")
	private String color;
	private Date eventDate;
	private Double latitude;
	private Double longitude;
	@Column(name = "eventType.id")
	private Integer eventTypeId;
}
