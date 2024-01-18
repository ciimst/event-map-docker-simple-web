package com.imst.event.map.web.vo;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionWrapperItem {
	
	private List<UserLayerPermissionItem> userLayerPermissionItemList;
	private List<UserEventGroupPermissionItem> userEventGroupPermissionItemList;
	
	public PermissionWrapperItem() {
		
	}
	
	public PermissionWrapperItem(List<UserLayerPermissionItem> userLayerPermissionItemList, List<UserEventGroupPermissionItem> userEventGroupPermissionItemList) {
		this.userLayerPermissionItemList = userLayerPermissionItemList;
		this.userEventGroupPermissionItemList = userEventGroupPermissionItemList;
	}
	
	public List<Integer> getUserEventGroupPermissionItemIds() {
				
		return userEventGroupPermissionItemList.stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
	}
	
	public List<Integer> getUserLayerPermissionItemIds() {
		
		return userLayerPermissionItemList.stream().map(UserLayerPermissionItem::getLayerId).collect(Collectors.toList());
	}
	
	public List<Integer> getUserLayerNoFullPermissionItemIds() {
		
		return userLayerPermissionItemList.stream().filter(o -> o.getHasFullPermission() == false).map(UserLayerPermissionItem::getLayerId).collect(Collectors.toList());
	}
	
	public List<Integer> getUserLayerHasFullPermissionItemIds() {
		
		return userLayerPermissionItemList.stream().filter(o -> o.getHasFullPermission() == true).map(UserLayerPermissionItem::getLayerId).collect(Collectors.toList());
	}

}
