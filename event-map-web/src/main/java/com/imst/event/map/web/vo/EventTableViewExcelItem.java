package com.imst.event.map.web.vo;

import java.util.Date;

import javax.persistence.Column;

import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.datatables.EntitySortKey;
import com.imst.event.map.web.utils.DateUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventTableViewExcelItem {
	
	private Integer id;
	private String city;
	private String country;
	
	private Integer eventTypeId;
	@EntitySortKey("eventTypeId")
	private String eventTypeName;
	private String eventTypeCode;
	private String eventTypeImage;
	
	private Integer eventGroupId;
	@EntitySortKey("eventGroupId")
	private String eventGroupName;
	private String eventGroupColor;
	
	private Integer layerId;
	private String layerName;
	
	private String title;
	private String spot;
	private String description;
	private Date eventDate;
	private String eventDateStr;
	private Double latitude;
	private Double longitude;
	private String blackListTag;
	private String sourceUser;
	private String createUser;
	private Date createDate;
	private String createDateStr;
	private Date updateDate;
	private String updateDateStr;
	
	@Column(name = "eventGroup.layer.isTemp")
	private Boolean state;//datatable aramaları için
	
	@Column(name = "state.id")
	private Integer stateId;//değer kontrolü için 
	
	private String reservedKey;
	private String reservedType;
	private String reservedId;
	private String reservedLink;
	
	private String reserved1;
	private String reserved2;
	private String reserved3;
	private String reserved4;
	private String reserved5;
	
	private String startDateStr;
	private String endDateStr;
	
	private Integer userId;
	private Integer groupId;
	
	@Column(name="spot")
	private String mediaPath;
	
	@Column(name="spot")
	private String tagId;
	
	@Column(name="spot")
	private String deleteImageId;
	
	@Column(name="spot")
	private String deleteTagId;
	
	public EventTableViewExcelItem() {
	
	}
	
	//bunu silme
	public EventTableViewExcelItem(Integer id, String title, String spot, String description, 
			Date eventDate, Integer eventTypeId, String eventTypeName, String eventTypeImage,  String layerName,
			String countryName,String cityName, Double latitude, Double longitude, String blackListTag, String createUser, Date createDate, Date updateDate,
			Integer eventGroupId, String eventGroupName, Integer stateId,
			String reservedKey, String reservedType, String reservedId, String reservedLink, Integer userId, Integer groupId, String eventGroupColor, Integer layerId, String eventTypeCode,
			String reserved1, String reserved2, String reserved3, String reserved4, String reserved5) {
		
		this.id = id;
		this.city = cityName;
		this.country = countryName;
		this.eventTypeId = eventTypeId;
		this.eventTypeName = eventTypeName;	
		this.eventTypeImage =  eventTypeImage;
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
		this.eventGroupId = eventGroupId;
		this.eventGroupName = eventGroupName;
		this.stateId = stateId;
		this.state = stateId.equals(StateE.TRUE.getValue()) ?  true : false; 
		this.reservedKey = reservedKey;
		this.reservedType = reservedType;
		this.reservedId = reservedId;
		this.reservedLink = reservedLink;
		this.eventDateStr = DateUtils.formatWithCurrentLocale(this.eventDate);
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.updateDateStr = DateUtils.formatWithCurrentLocale(this.updateDate);
		this.userId = userId;
		this.groupId = groupId;
		this.eventGroupColor = eventGroupColor;
		
		this.layerId = layerId;
		this.eventTypeCode = eventTypeCode;
		
		this.reserved1 = reserved1;
		this.reserved2 = reserved2;
		this.reserved3 = reserved3;
		this.reserved4 = reserved4;
		this.reserved5 = reserved5;
	}
	
	
	public EventTableViewExcelItem(Event event) {
		
		this.id = event.getId();
		this.city = event.getCity();
		
		this.country = event.getCountry();
		this.eventTypeId = event.getEventType().getId();
		this.eventTypeName = event.getEventType().getName();
		this.eventTypeImage = event.getEventType().getImage();

		this.eventGroupId = event.getEventGroup().getId();
		this.eventGroupName = event.getEventGroup().getName();
		this.eventGroupColor = event.getEventGroup().getColor();
		
		this.layerId = event.getEventGroup().getLayer().getId();
		this.layerName = event.getEventGroup().getLayer().getName();
		this.title = event.getTitle();
		this.spot = event.getSpot();
		this.description = event.getDescription();
		this.eventDate = event.getEventDate();
		this.latitude = event.getLatitude();
		this.longitude = event.getLongitude();
		this.blackListTag = event.getBlackListTag();
		this.createUser = event.getCreateUser();
		this.createDate = event.getCreateDate();
		this.updateDate = event.getUpdateDate();
		
		this.reservedKey = event.getReservedKey();
		this.reservedType = event.getReservedType();
		this.reservedId = event.getReservedId();
		this.reservedLink = event.getReservedLink();
		this.eventDateStr = DateUtils.formatWithCurrentLocale(this.eventDate);
		this.createDateStr = DateUtils.formatWithCurrentLocale(this.createDate);
		this.updateDateStr = DateUtils.formatWithCurrentLocale(this.updateDate);
		
		this.stateId = event.getState().getId();
		this.state = StateE.getIntegerStateToBoolean(event.getState().getId());
		
		this.userId = event.getUserId();
		this.groupId = event.getGroupId();
		
		this.layerId = event.getEventGroup().getLayer().getId();
		this.eventTypeCode = event.getEventType().getCode();
	}
	

}