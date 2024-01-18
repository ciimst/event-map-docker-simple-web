package com.imst.event.map.web.vo;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlertCriteriaEventItem {

	private Integer id;
	private String spot;
	private String title;
	private String description;
	
	private Double latitude;
	private Double longitude;
	
	@Column(name = "eventType.id")
	private Integer eventTypeId;
	
	@Column(name = "eventGroup.id")
	private Integer eventGroupId;
	
	@Column(name = "eventGroup.layer.id")
	private Integer layerId;
	
	
	private String reservedKey;
	private String reservedType;
	private String reservedId;
	private String reservedLink;
	
	
}
