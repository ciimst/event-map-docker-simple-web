package com.imst.event.map.web.vo;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventTableItem {

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

	private String reservedKey;
	private String reservedType;
	private String reservedId;
	private String reservedLink;
	
	@Column(name = "spot")
	private String eventTypeName;
	
	// Veri tabanı sorgusuna dahil değil, Çünkü constructorda yer almıyor
	private List<SidebarEventMediaItem> eventMediaItemList;
	private String dbName;
	@JsonProperty("DT_RowId")
	private String DT_RowId;
	
	public EventTableItem(Integer id, String spot, String title, String description, String country, String city,
			Double latitude, Double longitude, String color, String eventTypeImage, Integer eventTypeId, Date eventDate,
			Date createDate, String groupName, Integer groupId, Integer layerId, String reservedKey,
			String reservedType, String reservedId, String reservedLink, String eventTypeName) {

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
		this.eventDate = eventDate;
		this.createDate = createDate;
		this.groupName = groupName;
		this.groupId = groupId;
		this.layerId = layerId;
		this.reservedKey = reservedKey;
		this.reservedType = reservedType;
		this.reservedId = reservedId;
		this.reservedLink = reservedLink;
		this.eventTypeName = eventTypeName;
		
		this.DT_RowId = "rowId" + id;
	}
	
	
	
}
