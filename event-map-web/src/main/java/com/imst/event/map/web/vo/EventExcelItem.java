package com.imst.event.map.web.vo;


import java.util.Date;

import javax.persistence.Column;

import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
public class EventExcelItem {
	
	
	private Integer id;
	private String city;
	private String country;
	
	@Column(name = "eventType.id")
	private Integer eventTypeId;
	
	@Column(name = "eventType.name")
	private String eventTypeName;
	
	@Column(name = "eventType.code")
	private String eventTypeCode;
	
	@Column(name = "eventType.image")
	private String eventTypeImage;
	
	@Column(name = "eventGroup.id")
	private Integer eventGroupId;
	
	@Column(name = "eventGroup.name")
	private String eventGroupName;
	
	@Column(name = "eventGroup.color")
	private String eventGroupColor;
	
	@Column(name = "eventGroup.layer.id")
	private Integer layerId;
	
	@Column(name = "eventGroup.layer.name")
	private String layerName;
	
	private String title;
	private String spot;
	private String description;
	private Date eventDate;
	private Double latitude;
	private Double longitude;
	private String blackListTag;
	private String createUser;
	private Date createDate;
	private Date updateDate;
	
	@Column(name = "eventGroup.layer.isTemp")
	private Boolean state;//datatable aramaları için
	
	@Column(name = "state.id")
	private Integer stateId;//değer kontrolü için 
	
	private String reservedKey;
	private String reservedType;
	private String reservedId;
	private String reservedLink;
	
	private Integer userId;
	private Integer groupId;
	
	private String createDateStr;
	private String updateDateStr;
	private String eventDateStr;
	
	private String reserved1;
	private String reserved2;
	private String reserved3;
	private String reserved4;
	private String reserved5;
	
	private String startDateStr;
	private String endDateStr;
	
	
	public EventExcelItem(Integer id, String city, String country, Integer eventTypeId, String eventTypeName,
			String eventTypeCode, String eventTypeImage, Integer eventGroupId, String eventGroupName,
			String eventGroupColor, Integer layerId, String layerName, String title, String spot, String description,
			Date eventDate, Double latitude, Double longitude,String blackListTag, String createUser, Date createDate, Date updateDate,
			Integer stateId, String reservedKey, String reservedType, String reservedId, String reservedLink,
			Integer userId, Integer groupId, String reserved1, String reserved2, String reserved3, String reserved4, String reserved5) {
		super();
		this.id = id;
		this.city = city;
		this.country = country;
		this.eventTypeId = eventTypeId;
		this.eventTypeName = eventTypeName;
		this.eventTypeCode = eventTypeCode;
		this.eventTypeImage = eventTypeImage;
		this.eventGroupId = eventGroupId;
		this.eventGroupName = eventGroupName;
		this.eventGroupColor = eventGroupColor;
		this.layerId = layerId;
		this.layerName = layerName;
		this.title = title;
		this.spot = spot;
		this.description = description;
		this.eventDate = eventDate;
		this.latitude = latitude;
		this.longitude = longitude;
		this.blackListTag = blackListTag;
		this.createUser = createUser;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.stateId = stateId;
		this.state = StateE.getIntegerStateToBoolean(stateId); 
		this.reservedKey = reservedKey;
		this.reservedType = reservedType;
		this.reservedId = reservedId;
		this.reservedLink = reservedLink;
		this.userId = userId;
		this.groupId = groupId;
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.updateDateStr = DateUtils.formatWithCurrentLocale(this.updateDate);
		this.eventDateStr = DateUtils.formatWithCurrentLocale(this.eventDate);
		
		this.reserved1 = reserved1;
		this.reserved2 = reserved2;
		this.reserved3 = reserved3;
		this.reserved4 = reserved4;
		this.reserved5 = reserved5;
	}
	

}
