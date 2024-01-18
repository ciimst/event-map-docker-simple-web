//package com.imst.event.map.web.utils;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import com.imst.event.map.web.db.projections.EventGroupProjection;
//import com.imst.event.map.web.vo.EventGroupItem;
//import com.imst.event.map.web.vo.EventGroupParentItem;
//
//public class EventGroupParentTree {
//
//	private List<EventGroupParentItem> eventGroups = new ArrayList<>();
//	private List<String> eventGroupNames = new ArrayList<>();
//	
//	private List<Integer> eventGroupParentId = new ArrayList<>();
//	
//	private List<Integer> childIdsGeneric = new ArrayList<>();
//	private List<Integer> parentIdsGeneric = new ArrayList<>();
//
//	public EventGroupParentTree(List<EventGroupParentItem> eventGroups) {
//		this.eventGroups = eventGroups;
//	}
//	
//	public EventGroupParentTree(List<EventGroupProjection> eventGroupProjection, Boolean state) {
//		
//		this.eventGroups = eventGroupProjection.stream().map(item -> new EventGroupParentItem(item)).collect(Collectors.toList());
//	}
//	
//	// Event grubunun parentinin, kendi cocugu secilmemesini sagliyor
//	// Bütün olay gruplarını parentleri ile beraber döner : Parent -> Child şeklinde
////	public List<EventGroupParentItem> eventGroupListThatCanBeAddedAsParent(Integer currentId){
////		
////		List<EventGroupParentItem> resultList = new ArrayList<>();
////		
////		eventGroups.forEach(item -> {
////			
////			
////			eventGroupNames = new ArrayList<>();
////			eventGroupParentId = new ArrayList<>();
////			
////			eventGroupNames.add(item.getName());
////			eventGroupParentId.add(item.getId());
////			
////			Map<String, List<Integer>> map = getEventGroupName(item);
////
////			boolean control = true;
////			
////
////			for (Entry<String, List<Integer>> entry : map.entrySet()) {
////				
////				control = entry.getValue().stream().anyMatch(f -> currentId != null && currentId.equals(f));
////			}
////			
////			if(!control) {
////				
////				List<String> namelist = new ArrayList<String>(map.keySet());
////				
////
////				EventGroupParentItem eventGroupItem = new EventGroupParentItem();
////				eventGroupItem.setId(item.getId());
////				eventGroupItem.setColor(item.getColor());
////				eventGroupItem.setDescription(item.getDescription());
////				eventGroupItem.setLayerId(item.getLayerId());
////				eventGroupItem.setLayerName(item.getLayerName());
////				eventGroupItem.setParentId(item.getParentId());
////				eventGroupItem.setParentName(item.getParentName());
////				eventGroupItem.setName(namelist.get(0));
////				resultList.add(eventGroupItem);
////			}
////					
////		});
////		
////		return resultList;
////	}
//	
//	public List<EventGroupParentItem> eventGroupListWithParentString(List<Integer> permissionedEventGroupIdList){
//		
//		List<EventGroupParentItem> resultList = new ArrayList<>();
//		
//		eventGroups.forEach(item -> {
//			
//			
//			eventGroupNames = new ArrayList<>();
//			eventGroupParentId = new ArrayList<>();
//			
//			eventGroupNames.add(item.getName());
//			eventGroupParentId.add(item.getId());
//			
//			boolean contains = permissionedEventGroupIdList.contains(item.getId());
//			if(!contains) {
//				return;
//			}
//			
//			Map<String, List<Integer>> map = getEventGroupName(item);
//				
//			List<String> namelist = new ArrayList<String>(map.keySet());
//			
//
//			EventGroupParentItem eventGroupItem = new EventGroupParentItem();
//			eventGroupItem.setId(item.getId());
//			eventGroupItem.setColor(item.getColor());
//			eventGroupItem.setDescription(item.getDescription());
//			eventGroupItem.setLayerId(item.getLayerId());
//			eventGroupItem.setLayerName(item.getLayerName());
//			eventGroupItem.setParentId(item.getParentId());
//			eventGroupItem.setParentName(item.getParentName());
//			eventGroupItem.setName(namelist.get(0));
//			resultList.add(eventGroupItem);
//					
//		});
//		
//		return resultList;
//	}
//	
//
//
//	public Map<String, List<Integer>> getEventGroupName(EventGroupParentItem eventGroupItem) {
//
//		
//		List<Integer> parentIdList = findParentName(eventGroupItem);		
//		String currentEventGroupNewName = getCurrentEventGroupNewName(eventGroupNames);
//		
//		Map<String, List<Integer>> map = new HashMap<>();
//		map.put(currentEventGroupNewName, parentIdList);
//		return map;
//	}
//
//	private List<Integer> findParentName(EventGroupParentItem eventGroupItem) {
//
//		Integer parentId = eventGroupItem.getParentId();
//		
//		if (parentId != null) {
//
//			List<EventGroupParentItem> parentEventGroupItemList = eventGroups.stream().filter(f -> parentId.equals(f.getId()))
//					.collect(Collectors.toList());
//			
//			EventGroupParentItem parentEventGroupItem = new EventGroupParentItem();
//			if(parentEventGroupItemList.size() > 0) {
//				
//				parentEventGroupItem = parentEventGroupItemList.get(0);				
//				eventGroupNames.add(parentEventGroupItem.getName());				
//				eventGroupParentId.add(parentEventGroupItem.getId());
//			}
//			
//			
//			if (parentEventGroupItem != null && parentEventGroupItem.getParentId() != null) {
//				
//				findParentName(parentEventGroupItem);
//			} 		
//
//		}
//		
//		return eventGroupParentId;
//
//	}
//
//	private String getCurrentEventGroupNewName(List<String> names) {
//
//		Collections.reverse(names);
//		return String.join(" -> ", names);
//	}
//	
//	/////////////////// RECURSIVE OLARAK IZINLERE GORE CHİLD BULMA
//	
//	public List<Integer> getPermissionEventGroup(List<Integer> eventGroupPermissionIdList) {
//
//		childIdsGeneric = new ArrayList<>();
//		return getPermissionEventGroupPrivate(eventGroupPermissionIdList);
//	}
//	
//	private List<Integer> getPermissionEventGroupPrivate(List<Integer> eventGroupPermissionIdList) {
//
//		for(Integer permEventGroupId : eventGroupPermissionIdList) {
//			
//			List<Integer> childIds = eventGroups.stream().filter(f -> permEventGroupId.equals(f.getParentId())).map(m -> m.getId()).collect(Collectors.toList());
//			childIdsGeneric.addAll(childIds);
//			
//			if(childIds.size() > 0) {
//				getPermissionEventGroupPrivate(childIds);
//			}
//		}
//		
//		return childIdsGeneric;
//	}
//	
//	
//	/////////////////// RECURSIVE OLARAK IZINLERE GORE PARENT BULMA
//
//	public List<Integer> getPermissionEventGroupParent(List<Integer> eventGroupPermissionIdList) {
//		
//		parentIdsGeneric = new ArrayList<>();
//		return getPermissionEventGroupParentPrivate(eventGroupPermissionIdList);
//	}
//	
//	private List<Integer> getPermissionEventGroupParentPrivate(List<Integer> eventGroupPermissionIdList) {
//		
//		for(Integer permEventGroupId : eventGroupPermissionIdList) {
//			
//			Optional<EventGroupParentItem> eventGroupItemOptional = eventGroups.stream().filter(f -> permEventGroupId.equals(f.getId())).findAny();
//			EventGroupParentItem eventGroupItem = eventGroupItemOptional.get();
//			parentIdsGeneric.add(eventGroupItem.getId());
//			
//			if(eventGroupItem.getParentId() != null) {
//				getPermissionEventGroupParentPrivate(Arrays.asList(eventGroupItem.getParentId()));
//			}
//		}
//		
//		return parentIdsGeneric;
//	}
//
//}