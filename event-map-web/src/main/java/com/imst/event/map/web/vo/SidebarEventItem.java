package com.imst.event.map.web.vo;

import java.util.Date;

import javax.persistence.Column;

import com.imst.event.map.web.constant.StateE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SidebarEventItem {

	private Integer id;
	private String spot;
	private String title;
	private String description;
	private String country;
	private String city;
	private Double latitude;
	private Double longitude;
	@Column(name = "eventGroup.color")
	private String color;
	@Column(name = "spot")// daha sonra değiştiriliyor
	private String eventTypeImage;
	@Column(name = "eventType.id")
	private Integer eventTypeId;
	private Date eventDate;
	private Date createDate;
	
	@Column(name = "eventGroup.name")
	private String groupName;
	@Column(name = "eventGroup.id")
	private Integer groupId;
	@Column(name = "eventGroup.layer.id")
	private Integer layerId;
	
	private String reservedLink;
	
	@Column(name = "spot")
	private String eventTypeName;
	
	private String reserved1;
	private String reserved2;
	private String reserved3;
	private String reserved4;
	private String reserved5;
	
	@Column(name="state.id")
	private Integer stateId;
	
	@Column(name = "eventGroup.layer.isTemp")
	private Boolean state = StateE.getIntegerStateToBoolean(stateId);

	
}
