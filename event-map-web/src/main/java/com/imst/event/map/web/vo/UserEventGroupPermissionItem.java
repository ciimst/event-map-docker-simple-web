package com.imst.event.map.web.vo;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserEventGroupPermissionItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6670461622448887457L;
	private Integer id;
	@Column(name = "eventGroup.name")
	private String eventGroupName;
	@Column(name = "eventGroup.id")
	private Integer eventGroupId;
	
	@Column(name = "eventGroup.layer.id")
	private Integer eventGroupLayerId;
	@Column(name = "eventGroup.layer.name")
	private String eventGroupLayerName;
	
	@Column(name = "user.id")
	private Integer userId;
	
	@Column(name = "user.name")
	private String userName;
	

	
	

	
//	public UserEventGroupPermissionItem(Integer id, String eventGroupName, Integer eventGroupId,  Integer layerId, String layerName, Integer userId, String userName) {
//		this.id = id;
//		this.eventGroupName = eventGroupName;
//		this.eventGroupId = eventGroupId;
//		this.eventGroupLayerId = layerId;
//		this.eventGroupLayerName = layerName;
//		this.userId = userId;
//		this.userName = userName;
//	}
	
	
//	public UserEventGroupPermissionItem(Integer id, String eventGroupName, Integer eventGroupId, Integer eventGroupLayerId, Integer userId) {
//		super();
//		this.id = id;
//		this.eventGroupName = eventGroupName;
//		this.eventGroupId = eventGroupId;
//		this.eventGroupLayerId = eventGroupLayerId;
//		this.userId = userId;
//	}
	
	
//	public UserEventGroupPermissionItem(Integer id, Integer layerId, String layerName, Integer eventGroupId, String eventGroupName, Integer userId, String userName) {
//		this.id = id;
//		this.userId = userId;
//		this.userName = userName;
//		this.eventGroupLayerId = layerId;
//		this.eventGroupLayerName = layerName;
//		this.eventGroupId = eventGroupId;
//		this.eventGroupName = eventGroupName;
//	}
	
//	public UserEventGroupPermissionItem() {
//		
//	}




	
}
