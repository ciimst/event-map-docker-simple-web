package com.imst.event.map.web.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.imst.event.map.web.db.projections.EventGroupProjection;
import com.imst.event.map.web.vo.EventGroupItem;
import com.imst.event.map.web.vo.EventGroupParentItem;
import com.imst.event.map.web.vo.EventGroupTreeItem;

public class EventGroupTree {

	private List<EventGroupTreeItem> eventGroupList = new ArrayList<EventGroupTreeItem>();
	private List<EventGroupItem> currentLayerAllEventGroupList = new ArrayList<>();
	
	private List<Integer> ids = new ArrayList<>();

	private List<Integer> parentIdsGeneric = new ArrayList<>();
	
	public EventGroupTree(List<EventGroupTreeItem> eventGroupList, List<EventGroupItem> currentLayerAllEventGroupList) {
		this.eventGroupList = eventGroupList;		
		this.currentLayerAllEventGroupList = currentLayerAllEventGroupList;
	}
	
	private List<EventGroupParentItem> eventGroups = new ArrayList<>();
	private List<String> eventGroupNames = new ArrayList<>();
	public EventGroupTree(List<EventGroupProjection> eventGroupProjection) {
		
		this.eventGroups = eventGroupProjection.stream().map(item -> new EventGroupParentItem(item)).collect(Collectors.toList());
	}


	public List<EventGroupTreeItem> builTree() {
		List<EventGroupTreeItem> treeEventGroups = new ArrayList<EventGroupTreeItem>();

		// Olay grubunun bir parentı var ise fakat izinli olduğu listede yoksa childın
		// parent olabilmesi için parentId değeri sıfır yapılıyor.
		eventGroupList.forEach(item -> {

			Integer parentId = item.getParentId();
			if (parentId != null && parentId != 0) {
				boolean hasParentId = eventGroupList.stream().filter(f -> item.getParentId().equals(f.getId()) && item.getDbName().equals(f.getDbName()))
						.collect(Collectors.toList()).size() > 0;
				item.setParentId(hasParentId ? item.getParentId() : 0);
			}

		});
		//

		for (EventGroupTreeItem eventGroupNode : getRootNode()) {
			eventGroupNode = buildChilTree(eventGroupNode);
			treeEventGroups.add(eventGroupNode);
		}
		return treeEventGroups;
	}

	// Recursive olarak alt düğümleri oluşturuluyor.
	private EventGroupTreeItem buildChilTree(EventGroupTreeItem pNode) {
		List<EventGroupTreeItem> chilEventGroups = new ArrayList<EventGroupTreeItem>();
		for (EventGroupTreeItem eventGroupNode : eventGroupList) {

			if (eventGroupNode.getParentId().equals(pNode.getId()) && eventGroupNode.getDbName().equals(pNode.getDbName())) {
				chilEventGroups.add(buildChilTree(eventGroupNode));
			}
		}
		pNode.setItems(chilEventGroups);
		return pNode;
	}

	// Root olan düğümleri buluyor
	private List<EventGroupTreeItem> getRootNode() {
		List<EventGroupTreeItem> rootEventGroupLists = new ArrayList<EventGroupTreeItem>();
		for (EventGroupTreeItem eventGroupNode : eventGroupList) {
			if (eventGroupNode.getParentId().equals(0)) {
				rootEventGroupLists.add(eventGroupNode);
			}
		}
		return rootEventGroupLists;
	}

	/////////////////// RECURSIVE OLARAK IZINLERE GORE CHİLD BULMA
	
	public List<Integer> getPermissionEventGroup(List<Integer> eventGroupPermissionIdList) {

		for(Integer permEventGroupId : eventGroupPermissionIdList) {
			
			List<Integer> childIds = currentLayerAllEventGroupList.stream().filter(f -> permEventGroupId.equals(f.getParentId())).map(m -> m.getId()).collect(Collectors.toList());
			ids.addAll(childIds);
			
			if(childIds.size() > 0) {
				getPermissionEventGroup(childIds);
			}
			
			
		}
		
		return ids;
	}
	
	
/////////////////// RECURSIVE OLARAK IZINLERE GORE PARENT BULMA

	public List<Integer> getPermissionEventGroupParent(List<Integer> eventGroupPermissionIdList) {
	
		parentIdsGeneric = new ArrayList<>();
		return getPermissionEventGroupParentPrivate(eventGroupPermissionIdList);
	}
	
	private List<Integer> getPermissionEventGroupParentPrivate(List<Integer> eventGroupPermissionIdList) {
	
		for(Integer permEventGroupId : eventGroupPermissionIdList) {
		
			Optional<EventGroupItem> eventGroupItemOptional = currentLayerAllEventGroupList.stream().filter(f -> permEventGroupId.equals(f.getId())).findAny();
			
			if(eventGroupItemOptional.isPresent()) {
				EventGroupItem eventGroupItem = eventGroupItemOptional.get();
				parentIdsGeneric.add(eventGroupItem.getId());
				
				if(eventGroupItem.getParentId() != null) {
					getPermissionEventGroupParentPrivate(Arrays.asList(eventGroupItem.getParentId()));
				}
			}
			
			
		}
		
		return parentIdsGeneric;
	}
	

		//Combobox isimlendirme
	public List<EventGroupParentItem> eventGroupListWithParentString(List<Integer> permissionedEventGroupIdList){
		
		List<EventGroupParentItem> resultList = new ArrayList<>();
		
		eventGroups.forEach(item -> {
			
			
			eventGroupNames = new ArrayList<>();
			parentIdsGeneric = new ArrayList<>();
			
			eventGroupNames.add(item.getName());
			parentIdsGeneric.add(item.getId());
			
			boolean contains = permissionedEventGroupIdList.contains(item.getId());
			if(!contains) {
				return;
			}
			
			Map<String, List<Integer>> map = getEventGroupName(item);
				
			List<String> namelist = new ArrayList<String>(map.keySet());
			

			EventGroupParentItem eventGroupItem = new EventGroupParentItem();
			eventGroupItem.setId(item.getId());
			eventGroupItem.setColor(item.getColor());
			eventGroupItem.setDescription(item.getDescription());
			eventGroupItem.setLayerId(item.getLayerId());
			eventGroupItem.setLayerName(item.getLayerName());
			eventGroupItem.setParentId(item.getParentId());
			eventGroupItem.setParentName(item.getParentName());
			eventGroupItem.setName(namelist.get(0));
			resultList.add(eventGroupItem);
					
		});
		
		return resultList;
	}
	


	public Map<String, List<Integer>> getEventGroupName(EventGroupParentItem eventGroupItem) {

		
		List<Integer> parentIdList = findParentName(eventGroupItem);		
		String currentEventGroupNewName = getCurrentEventGroupNewName(eventGroupNames);
		
		Map<String, List<Integer>> map = new HashMap<>();
		map.put(currentEventGroupNewName, parentIdList);
		return map;
	}

	private List<Integer> findParentName(EventGroupParentItem eventGroupItem) {

		Integer parentId = eventGroupItem.getParentId();
		
		if (parentId != null) {

			List<EventGroupParentItem> parentEventGroupItemList = eventGroups.stream().filter(f -> parentId.equals(f.getId()))
					.collect(Collectors.toList());
			
			EventGroupParentItem parentEventGroupItem = new EventGroupParentItem();
			if(parentEventGroupItemList.size() > 0) {
				
				parentEventGroupItem = parentEventGroupItemList.get(0);				
				eventGroupNames.add(parentEventGroupItem.getName());				
				parentIdsGeneric.add(parentEventGroupItem.getId());
			}
			
			
			if (parentEventGroupItem.getParentId() != null) {
				
				findParentName(parentEventGroupItem);
			} 		

		}
		
		return parentIdsGeneric;

	}

	private String getCurrentEventGroupNewName(List<String> names) {

		Collections.reverse(names);
		return String.join(" -> ", names);
	}
	

}