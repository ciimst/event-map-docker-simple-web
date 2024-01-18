package com.imst.event.map.web.vo;

import com.imst.event.map.hibernate.entity.Layer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LayerSimpleItem {
	
	private Integer id;
	private String guid;
	private String name;
	private Boolean isTemp;
	private Boolean hasFullPermission;
	
	public LayerSimpleItem(Layer layer, Boolean hasFullPermission) {
	
		this.id = layer.getId();
		this.guid = layer.getGuid();
		this.name = layer.getName();
		this.isTemp = layer.getIsTemp();
		this.hasFullPermission = hasFullPermission;
	}
	
	public LayerSimpleItem(UserLayerPermissionItem userLayerPermissionItem) {
		
		this.id = userLayerPermissionItem.getLayerId();
		this.guid = userLayerPermissionItem.getLayerGuid();
		this.name = userLayerPermissionItem.getLayerName();
		this.isTemp = userLayerPermissionItem.getIsTemp();
		this.hasFullPermission = userLayerPermissionItem.getHasFullPermission();
	}
	
	public Layer getLayerWithId() {
		
		Layer layer = new Layer();
		layer.setId(this.id);
		
		return layer;
	}
	
}
