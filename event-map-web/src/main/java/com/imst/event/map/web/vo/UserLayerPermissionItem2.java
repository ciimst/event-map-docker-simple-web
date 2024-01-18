package com.imst.event.map.web.vo;

import com.imst.event.map.hibernate.entity.UserLayerPermission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLayerPermissionItem2 {

	private Integer id;
	private Integer userId;
	private String userName;
	private Integer layerId;
	private String layerName;
	
	private Boolean hasFullPermission;

	
	public UserLayerPermissionItem2() {
		
	}
	
	public UserLayerPermissionItem2(Integer id,Integer layerId, String layerName,Integer userId,String userName) {
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.layerId = layerId;
		this.layerName = layerName;
		this.hasFullPermission = true;
	}
	
	public UserLayerPermissionItem2(UserLayerPermission userLayerPermission) {
		this.id=userLayerPermission.getId();
		this.layerId=userLayerPermission.getLayer().getId();
		this.layerName=userLayerPermission.getLayer().getName();
		this.userId=userLayerPermission.getUser().getId();
		this.userName=userLayerPermission.getUser().getUsername();
		this.hasFullPermission = true;
	}
	
	public static UserLayerPermissionItem2 newInstanceForLog(UserLayerPermission userLayerPermission) {
		
		UserLayerPermissionItem2 userLayerPermissionItem = new UserLayerPermissionItem2(userLayerPermission);
		
		return userLayerPermissionItem;
	}
}
