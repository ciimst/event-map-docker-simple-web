package com.imst.event.map.web.vo;

import java.util.Date;

import javax.persistence.Column;

import com.imst.event.map.web.constant.StateE;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventTableViewItem {

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
	@Column(name = "spot")
	private String eventTypeCode;
	private Date eventDate;
	private Date createDate;
	
	@Column(name = "eventGroup.name")
	private String groupName;
	@Column(name = "eventGroup.id")
	private Integer groupId;
	@Column(name = "eventGroup.layer.id")
	private Integer layerId;
	
	private String reservedLink;
	private String reservedType;
	private String reservedId;
	private String reservedKey;
	
	@Column(name = "spot")
	private String eventTypeName;
	
	private String reserved1;
	private String reserved2;
	private String reserved3;
	private String reserved4;
	private String reserved5;
	
	private String blackListTag;
	private Boolean state;
	
	@Column(name = "state.id")
	private Integer stateId;
	
	//Datatable arama işlemi için
	@Column(name = "spot")
	private String startDateStr;
	@Column(name = "spot")
	private String endDateStr;
	@Column(name = "eventGroup.id")
	private Integer eventGroupId;
	
	//bunu silme
	public EventTableViewItem(Integer id, String spot, String title, String description, String country, String city,
			Double latitude, Double longitude, String color, String eventTypeImage, Integer eventTypeId, String eventTypeCode, Date eventDate,
			Date createDate, String groupName, Integer groupId, Integer layerId, String reservedLink,
			String reservedType, String reservedId, String reservedKey, String eventTypeName, String reserved1,
			String reserved2, String reserved3, String reserved4, String reserved5, String blackListTag, Integer stateId,
			String startDateStr, String endDateStr, Integer eventGroupId) {
		
		this.id = id;
		this.spot = spot;
		this.title = title;
		this.description = description;
		this.country = country;
		this.city = city;
		this.latitude = latitude;
		this.longitude = longitude;
		this.color = color;
		this.eventTypeImage = eventTypeImage;
		this.eventTypeId = eventTypeId;
		this.eventTypeCode = eventTypeCode;
		this.eventDate = eventDate;
		this.createDate = createDate;
		this.groupName = groupName;
		this.groupId = groupId;
		this.layerId = layerId;
		this.reservedLink = reservedLink;
		this.reservedType = reservedType;
		this.reservedId = reservedId;
		this.reservedKey = reservedKey;
		this.eventTypeName = eventTypeName;
		this.reserved1 = reserved1;
		this.reserved2 = reserved2;
		this.reserved3 = reserved3;
		this.reserved4 = reserved4;
		this.reserved5 = reserved5;
		this.blackListTag = blackListTag;
		this.stateId = stateId;
		this.state = stateId != null ? StateE.getIntegerStateToBoolean(stateId) : null; //toplu state değiştirmede null geldiğinde false olmaması gerekiyor değerinin. 
		this.startDateStr = startDateStr;
		this.endDateStr = endDateStr;
		this.eventGroupId = eventGroupId;
	}
	

	
	
}
