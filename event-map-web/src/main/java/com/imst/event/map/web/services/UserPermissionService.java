package com.imst.event.map.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.db.projections.UserEventGroupPermissionProjection2;
import com.imst.event.map.web.db.projections.UserLayerPermissionProjection2;
import com.imst.event.map.web.db.repositories.EventGroupRepository;
import com.imst.event.map.web.db.repositories.LayerRepository;
import com.imst.event.map.web.db.repositories.UserEventGroupPermissionRepository;
import com.imst.event.map.web.db.repositories.UserLayerPermissionRepository;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.EventGroupTree;
import com.imst.event.map.web.vo.EventGroupItem;
import com.imst.event.map.web.vo.PermissionWrapperItem;
import com.imst.event.map.web.vo.UserEventGroupPermissionItem;
import com.imst.event.map.web.vo.UserLayerPermissionItem;

@Service
public class UserPermissionService {
	
	@Autowired
	private EventGroupRepository eventGroupRepository;	
	@Autowired
	private UserLayerPermissionRepository userLayerPermissionRepository;	
	@Autowired 
	private UserEventGroupPermissionRepository userEventGroupPermissionRepository;
	@Autowired 
	private LayerRepository layerRepository;	
	
	
	public PermissionWrapperItem findUserPermissions(User user){
		
		List<UserLayerPermissionProjection2> userLayerPermissionProjectedList = userLayerPermissionRepository.findAllProjectedByUser(user);
		
		List<UserLayerPermissionItem> userLayerPermissionItemList = new ArrayList<>();
		userLayerPermissionProjectedList.forEach(item -> {
			
			UserLayerPermissionItem userLayerPermissionItem = new UserLayerPermissionItem(item.getId(), item.getLayerName(), item.getLayerId(),  item.getUserId(), item.getLayerIsTemp(),item.getLayerGuid());
			userLayerPermissionItemList.add(userLayerPermissionItem);
		});

		List<Integer> userLayerPermissionIdList = userLayerPermissionProjectedList.stream().map(UserLayerPermissionProjection2::getLayerId).collect(Collectors.toList());
		
		List<UserEventGroupPermissionProjection2> userEventGroupPermissionProjectionList = userEventGroupPermissionRepository.findAllProjectedByUser(user);
		List<UserEventGroupPermissionItem> userEventGroupPermissionItemList = new ArrayList<>();
		userEventGroupPermissionProjectionList.forEach(item -> {
			
			UserEventGroupPermissionItem userEventGroupPermissionItem = new UserEventGroupPermissionItem(item.getEventGroupId(), item.getEventGroupName(), item.getEventGroupId(), item.getLayerId(), item.getLayerName(), item.getUserId(), item.getUserName());
			
//			UserEventGroupPermissionItem userEventGroupPermissionItem = new UserEventGroupPermissionItem();
//			userEventGroupPermissionItem.setEventGroupId(item.getEventGroupId());
//			userEventGroupPermissionItem.setEventGroupLayerId(item.getLayerId());
//			userEventGroupPermissionItem.setEventGroupLayerName(item.getLayerName());
//			userEventGroupPermissionItem.setId(item.getId());
//			userEventGroupPermissionItem.setUserId(item.getUserId());
//			userEventGroupPermissionItem.setUserName(item.getUserName());
//			userEventGroupPermissionItem.setEventGroupName(item.getEventGroupName());
			
			userEventGroupPermissionItemList.add(userEventGroupPermissionItem);
			
		});
		
		// İzinli olunan olay gruplarının ait oldugu layer id lerinin alınması
		List<Integer> distinctLayerIdList = userEventGroupPermissionItemList.stream().map(UserEventGroupPermissionItem::getEventGroupLayerId).distinct().collect(Collectors.toList());
		
		
		// izinli olunan grupların ait oldukları layerlara izin verilmesi
		List<Layer> layerList = layerRepository.findAllByIdIn(distinctLayerIdList);
		for (UserEventGroupPermissionItem userEventGroupPermissionItem : userEventGroupPermissionItemList) {
			
			List<Layer> list = layerList.stream().filter(item -> item.getId().equals(userEventGroupPermissionItem.getEventGroupLayerId())).collect(Collectors.toList());

			if(list.size() == 0) {
				continue;
			}
			Layer layer = list.get(0);
			
			boolean anyMatch = userLayerPermissionItemList.stream().anyMatch(item -> item.getLayerId().equals(layer.getId()));
			if(!anyMatch) {
				UserLayerPermissionItem userLayerPartialPermissionItem = new UserLayerPermissionItem(0, layer.getName(), layer.getId(), userEventGroupPermissionItem.getUserId(), layer.getIsTemp(), layer.getGuid());
				userLayerPartialPermissionItem.setHasFullPermission(false);
				userLayerPermissionItemList.add(userLayerPartialPermissionItem);
			}
		}
		
		// İzinli olunan layerların altındaki bütün grupların grup izinlerine eklenmesi
		List<EventGroup> allEventGroupsUnderLayersList = eventGroupRepository.findAllByLayerIdIn(userLayerPermissionIdList);

		for (EventGroup eventGroupUnderLayer : allEventGroupsUnderLayersList) {
			
			UserEventGroupPermissionItem extraEventGroupPermissionItem = new UserEventGroupPermissionItem(0,
					eventGroupUnderLayer.getName(), eventGroupUnderLayer.getId(), 
					eventGroupUnderLayer.getLayer().getId(), eventGroupUnderLayer.getLayer().getName(),					
					user.getId(), user.getName());
			
			
			
			userEventGroupPermissionItemList.add(extraEventGroupPermissionItem);
		}
		
		
		// İzinli olunan olay gruplarının ait oldugu layerların, taranacak olay gruplarına eklenmesi., tree icin
		List<EventGroup> allEventGroups = eventGroupRepository.findAllByLayerIdIn(distinctLayerIdList);
		
		allEventGroups.addAll(allEventGroupsUnderLayersList);
				
		
		/************/
		//Parent Gruba izin varsa child gruplarına izin ekleme.
		//izinli olunan layer ve olay gruplarının ait oldugu layerların altındaki tüm olay grupları getirildi. Child grupları bulmak için kullanıldı.
		

//		List<EventGroupItem> allEventGroupItemList = new ArrayList<>();
//		allEventGroups.forEach(item -> {
//			EventGroupItem tempEventGroupItem = new EventGroupItem(item.getId(), item.getName(), item.getColor(), item.getLayer().getId(), item.getLayer().getName(), item.getParentId(), item.getDescription());
//			allEventGroupItemList.add(tempEventGroupItem);
//		});
		
		List<EventGroupItem> allEventGroupItemList = new ArrayList<>();
		allEventGroupItemList = allEventGroups.stream()
				.map(item -> new EventGroupItem(item.getId(), item.getName(), item.getColor(),item.getDescription(), item.getParentId(), item.getLayer().getId(),""))
				.collect(Collectors.toList());		
		

		EventGroupTree eventGroupTree = new EventGroupTree(null, allEventGroupItemList);
		
		List<Integer> eventGroupPermissionIdList = userEventGroupPermissionItemList.stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		List<Integer> childEventGroupIds = eventGroupTree.getPermissionEventGroup(eventGroupPermissionIdList);
		
		
		childEventGroupIds.forEach(item -> {

			Optional<EventGroup> childEventGroup = allEventGroups.stream().filter(f -> f.getId().equals(item)).findAny();
		
			if(childEventGroup.isPresent()) {
				
				EventGroup eventGroup = childEventGroup.get();
				UserEventGroupPermissionItem extraEventGroupPermissionItem = new UserEventGroupPermissionItem(0, 
						eventGroup.getName(), eventGroup.getId(),
						eventGroup.getLayer().getId(), eventGroup.getLayer().getName(), 
						user.getId(), user.getName());
				

				if (!eventGroupPermissionIdList.contains(eventGroup.getId())) {
					userEventGroupPermissionItemList.add(extraEventGroupPermissionItem);
				}				
			}
			
		});
		
		/**************/
		
		PermissionWrapperItem permissionWrapperItem = new PermissionWrapperItem(userLayerPermissionItemList, userEventGroupPermissionItemList);
		
		
		return permissionWrapperItem;
	}
	
	public PermissionWrapperItem updateSessionUserPermissions(){
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = findUserPermissions(user);
		
		sessionUser.setUserEventGroupPermissionList(permissionWrapperItem.getUserEventGroupPermissionItemList());
		
		sessionUser.setUserLayerPermissionList(permissionWrapperItem.getUserLayerPermissionItemList());
		
		return permissionWrapperItem;
	}
	
//	public List<UserEventGroupPermissionItem> findUserEventGroupPermissions(User user){
//		
//		
//		List<UserLayerPermissionItem> userLayerPermissionItemList = userLayerPermissionRepository.findAllByUser(user);
//		List<Integer> userLayerPermissionIdList = userLayerPermissionItemList.stream().map(UserLayerPermissionItem::getLayerId).collect(Collectors.toList());
//		
//		List<UserEventGroupPermissionItem> userEventGroupPermissionItemList = userEventGroupPermissionRepository.findAllByUser(user);
//				
//		// İzinli olunan olay gruplarının ait oldugu layer id lerinin alınması
//		List<Integer> distinctLayerIdList = userEventGroupPermissionItemList.stream().map(UserEventGroupPermissionItem::getLayerId).distinct().collect(Collectors.toList());
//		
//		
//		// İzinli olunan layerların altındaki bütün grupların grup izinlerine eklenmesi
//		List<EventGroup> allEventGroupsUnderLayersList = eventGroupRepository.findAllByLayerIdIn(userLayerPermissionIdList);
//
//		for (EventGroup eventGroupUnderLayer : allEventGroupsUnderLayersList) {
//			UserEventGroupPermissionItem extraEventGroupPermissionItem = new UserEventGroupPermissionItem(0, 
//					eventGroupUnderLayer.getLayer().getId(), eventGroupUnderLayer.getLayer().getName(), 
//					eventGroupUnderLayer.getId(), eventGroupUnderLayer.getName(),
//					user.getId(), user.getName());
//			userEventGroupPermissionItemList.add(extraEventGroupPermissionItem);
//		}
//		
//		
//		// İzinli olunan olay gruplarının ait oldugu layerların, taranacak olay gruplarına eklenmesi., tree icin
//		List<EventGroup> allEventGroups = eventGroupRepository.findAllByLayerIdIn(distinctLayerIdList);
//		
//		allEventGroups.addAll(allEventGroupsUnderLayersList);
//				
//		
//		/************/
//		//Parent Gruba izin varsa child gruplarına izin ekleme.
//		//izinli olunan layer ve olay gruplarının ait oldugu layerların altındaki tüm olay grupları getirildi. Child grupları bulmak için kullanıldı.
//		
//
//		List<EventGroupItem> allEventGroupItemList = new ArrayList<>();
//		
//		allEventGroups.forEach(item -> {
//			EventGroupItem tempEventGroupItem = new EventGroupItem(item.getId(), item.getName(), item.getColor(), item.getLayer().getId(), item.getLayer().getName(), item.getParentId(), item.getDescription());
//			allEventGroupItemList.add(tempEventGroupItem);
//		});
//		
//
//		EventGroupTree eventGroupTree = new EventGroupTree(allEventGroupItemList);
//		
//		List<Integer> eventGroupPermissionIdList = userEventGroupPermissionItemList.stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
//		List<Integer> childEventGroupIds = eventGroupTree.getPermissionEventGroup(eventGroupPermissionIdList);
//		
//		
//		childEventGroupIds.forEach(item -> {
//
//			Optional<EventGroup> childEventGroup = allEventGroups.stream().filter(f -> f.getId().equals(item)).findAny();
//		
//			if(childEventGroup.isPresent()) {
//				
//				EventGroup eventGroup = childEventGroup.get();
//				UserEventGroupPermissionItem extraEventGroupPermissionItem = new UserEventGroupPermissionItem(0, 
//						eventGroup.getLayer().getId(), eventGroup.getLayer().getName(), 
//						eventGroup.getId(), eventGroup.getName(),
//						user.getId(), user.getName());
//				userEventGroupPermissionItemList.add(extraEventGroupPermissionItem);			
//			}
//			
//		});
//		
//		/**************/
//		
//		
//		return userEventGroupPermissionItemList;
//	}
	
//	public List<UserLayerPermissionItem> findUserLayerPermissions(User user){
//		
//		List<UserLayerPermissionItem> userLayerPermissionItemList = userLayerPermissionRepository.findAllByUser(user);
//		
//		List<UserEventGroupPermissionItem> userEventGroupPermissionItemList = userEventGroupPermissionRepository.findAllByUser(user);
//		
//		// İzinli olunan olay gruplarının ait oldugu layer id lerinin alınması
//		List<Integer> distinctLayerIdList = userEventGroupPermissionItemList.stream().map(UserEventGroupPermissionItem::getLayerId).distinct().collect(Collectors.toList());
//		
//		// izinli olunan grupların ait oldukları layerlara izin verilmesi
//		List<Layer> layerList = layerRepository.findAllByIdIn(distinctLayerIdList);
//		for (UserEventGroupPermissionItem userEventGroupPermissionItem : userEventGroupPermissionItemList) {
//			
//			List<Layer> list = layerList.stream().filter(item -> item.getId().equals(userEventGroupPermissionItem.getLayerId())).collect(Collectors.toList());
//
//			if(list.size() == 0) {
//				continue;
//			}
//			Layer layer = list.get(0);
//			
//			boolean anyMatch = userLayerPermissionItemList.stream().anyMatch(item -> item.getLayerId().equals(layer.getId()));
//			if(!anyMatch) {
//				UserLayerPermissionItem userLayerPartialPermissionItem = new UserLayerPermissionItem(0, layer.getId(),layer.getName(), userEventGroupPermissionItem.getUserId(), userEventGroupPermissionItem.getUserName());
//				userLayerPartialPermissionItem.setHasFullPermission(false);
//				userLayerPermissionItemList.add(userLayerPartialPermissionItem);
//			}
//		}
//				
//		return userLayerPermissionItemList;
//		
//	}
	

}
