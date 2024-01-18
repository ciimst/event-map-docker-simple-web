package com.imst.event.map.web.vo;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
public class UserLayerPermissionItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5397802896556024925L;
	private Integer id;
	@Column(name = "layer.name")
	private String layerName;
	@Column(name = "layer.id")
	private Integer layerId;
	@Column(name = "user.id")
	private Integer userId;
	@Column(name = "layer.isTemp")
	private Boolean isTemp;
	@Column(name = "layer.guid")
	private String layerGuid;
	
	private Boolean hasFullPermission;
	
//	@Column(name = "user.name")
//	private String userName;
	
	public UserLayerPermissionItem() {
	}
	
	public UserLayerPermissionItem(Integer id, String layerName, Integer layerId, Integer userId, Boolean isTemp,
			String layerGuid) {
		
		this.id = id;
		this.layerName = layerName;
		this.layerId = layerId;
		this.userId = userId;
		this.isTemp = isTemp;
		this.layerGuid = layerGuid;
		this.hasFullPermission = true;
	}
	
//	public UserLayerPermissionItem(Integer id,  Integer layerId, String layerName, Integer userId, String userName) {
//		
//		this.id = id;
//		this.layerName = layerName;
//		this.layerId = layerId;
//		this.userId = userId;
//		this.userName = userName;
//	}
	
	
	
}
