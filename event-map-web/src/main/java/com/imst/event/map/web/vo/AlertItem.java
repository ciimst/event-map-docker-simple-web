package com.imst.event.map.web.vo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.imst.event.map.hibernate.entity.Alert;
import com.imst.event.map.web.db.projections.AlertEventCountProjection;
import com.imst.event.map.web.db.projections.AlertProjection;
import com.imst.event.map.web.utils.SpatialUtil;
import com.vividsolutions.jts.geom.Coordinate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlertItem {

	private Integer id;
	private String name;
	private Date createDate;
	private Date updateDate;

	List<LatLongItem> coordinateInfo;
	Coordinate[] coordinateArr;
	
	private String query;
	
	private Integer eventTypeId;
	private Integer eventGroupId;
	
	private String eventTypeName;	
	
	private String reservedKey;
	private String reservedType;
	private String reservedId;
	private String reservedLink;
	private String eventGroupDbName;
	private String layerId;

	private String sharedBy;
	private String color;
	
	private long alertEventCount;
	
	public AlertItem() {
	}
	
	public AlertItem(Alert alert, String layerGuid) {
		
		this.id = alert.getId();

		this.name = alert.getName();
		this.createDate = alert.getCreateDate();
		this.updateDate = alert.getUpdateDate();
		this.coordinateInfo = alert.getPolygonCoordinate() != null ? SpatialUtil.convertToLatLongItemList(alert.getPolygonCoordinate()) : null;
		this.coordinateArr = alert.getPolygonCoordinate() != null ? alert.getPolygonCoordinate().getCoordinates() : null;
		
		this.reservedId = alert.getReservedId();
		this.reservedKey = alert.getReservedKey();
		this.reservedLink = alert.getReservedLink();
		this.reservedType = alert.getReservedType();
		
		this.query = alert.getQuery();
		this.layerId = layerGuid;
		
		this.eventGroupId = alert.getEventGroup() == null ? null : alert.getEventGroup().getId();
		this.eventTypeId = alert.getEventType() == null ? null : alert.getEventType().getId();
		this.eventGroupDbName = alert.getEventGroupDbName();
		
		this.sharedBy = alert.getSharedBy();
		this.color = alert.getColor();
	}
	
	public AlertItem(Alert alert) {
		

		this.id = alert.getId();
		this.name = alert.getName();
		this.createDate = alert.getCreateDate();
		this.updateDate = alert.getUpdateDate();
		
		this.reservedId = alert.getReservedId();
		this.reservedKey = alert.getReservedKey();
		this.reservedLink = alert.getReservedLink();
		this.reservedType = alert.getReservedType();
		
		this.query = alert.getQuery();
		
		this.eventGroupId = alert.getEventGroup() == null ? null : alert.getEventGroup().getId();
		this.eventTypeId = alert.getEventType() == null ? null : alert.getEventType().getId();
		this.eventGroupDbName = alert.getEventGroupDbName();
		
		this.eventTypeName =  alert.getEventType() == null ? null : alert.getEventType().getName();
		this.sharedBy = alert.getSharedBy();
		this.color = alert.getColor();
		
	}

	public static AlertItem newInstanceForLog(Alert alert) {
		AlertItem alertItem = new AlertItem(alert);
		return alertItem;
	}

	public AlertItem(AlertProjection alert, String layerGuid) {
		
		this.id = alert.getId();
		this.name = alert.getName();
		this.createDate = alert.getCreateDate();
		this.updateDate = alert.getUpdateDate();
		this.coordinateInfo = alert.getPolygonCoordinate() != null ? SpatialUtil.convertToLatLongItemList(alert.getPolygonCoordinate()) : null;
		this.coordinateArr = alert.getPolygonCoordinate() != null ? alert.getPolygonCoordinate().getCoordinates() : null;
		
		this.reservedId = alert.getReservedId();
		this.reservedKey = alert.getReservedKey();
		this.reservedLink = alert.getReservedLink();
		this.reservedType = alert.getReservedType();
		
		this.query = alert.getQuery();
		this.layerId = layerGuid;
		
		this.eventGroupId = alert.getEventGroupId() == null ? null : alert.getEventGroupId();
		this.eventTypeId = alert.getEventTypeId() == null ? null : alert.getEventTypeId();
		this.eventGroupDbName = alert.getEventGroupDbName();
		
		this.sharedBy = alert.getSharedBy();
		this.color = alert.getColor();
	}
	
	public AlertItem(AlertProjection alert, String layerGuid, List<AlertEventCountProjection> alertEventCountList) {
		
		this.id = alert.getId();
		this.name = alert.getName();
		this.createDate = alert.getCreateDate();
		this.updateDate = alert.getUpdateDate();
		this.coordinateInfo = alert.getPolygonCoordinate() != null ? SpatialUtil.convertToLatLongItemList(alert.getPolygonCoordinate()) : null;
		this.coordinateArr = alert.getPolygonCoordinate() != null ? alert.getPolygonCoordinate().getCoordinates() : null;
		
		this.reservedId = alert.getReservedId();
		this.reservedKey = alert.getReservedKey();
		this.reservedLink = alert.getReservedLink();
		this.reservedType = alert.getReservedType();
		
		this.query = alert.getQuery();
		this.layerId = layerGuid;
		
		this.eventGroupId = alert.getEventGroupId() == null ? null : alert.getEventGroupId();
		this.eventTypeId = alert.getEventTypeId() == null ? null : alert.getEventTypeId();
		this.eventGroupDbName = alert.getEventGroupDbName();
		
		this.sharedBy = alert.getSharedBy();
		this.color = alert.getColor();
		
		Optional<Long> optAlertEventCount = alertEventCountList.stream().filter(f -> f.getAlertId().equals(alert.getId())).map(item -> item.getCount()).findAny();
		if(optAlertEventCount.isPresent()) {
			this.alertEventCount = optAlertEventCount.get();
		}
		
	}

}

