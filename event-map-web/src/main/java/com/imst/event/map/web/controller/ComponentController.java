package com.imst.event.map.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imst.event.map.hibernate.entity.Alert;
import com.imst.event.map.hibernate.entity.AlertEvent;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.projections.EventsTimeCountProjection;
import com.imst.event.map.web.db.repositories.AlertEventRepository;
import com.imst.event.map.web.db.repositories.EventsTimeCountRepository;
import com.imst.event.map.web.db.repositories.EventLinkRepository;
import com.imst.event.map.web.db.repositories.LayerRepository;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.services.AlertService;
import com.imst.event.map.web.services.CallableEventGroupItemService;
import com.imst.event.map.web.services.EventGroupService;
import com.imst.event.map.web.services.EventMediaService;
import com.imst.event.map.web.services.EventService;
import com.imst.event.map.web.services.EventTypeService;
import com.imst.event.map.web.services.GeoLayerService;
import com.imst.event.map.web.services.MapAreaGroupService;
import com.imst.event.map.web.services.MapAreaService;
import com.imst.event.map.web.services.TagService;
import com.imst.event.map.web.services.TileServerService;
import com.imst.event.map.web.services.UserEventGroupPermissionService;
import com.imst.event.map.web.services.UserGroupIdService;
import com.imst.event.map.web.services.UserLayerPermissionService;
import com.imst.event.map.web.services.UserUserIdService;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.utils.EventGroupTree;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.EventGroupItem;
import com.imst.event.map.web.vo.EventGroupTreeItem;
import com.imst.event.map.web.vo.EventLinkItem;
import com.imst.event.map.web.vo.EventRequestItem;
import com.imst.event.map.web.vo.EventTypeItem;
import com.imst.event.map.web.vo.GeoLayerItem;
import com.imst.event.map.web.vo.KeyItemWrapper;
import com.imst.event.map.web.vo.LayerSimpleItem;
import com.imst.event.map.web.vo.MapAreaGroupItem;
import com.imst.event.map.web.vo.MapAreaItem;
import com.imst.event.map.web.vo.SidebarAlertItem;
import com.imst.event.map.web.vo.TileServerItem;
import com.imst.event.map.web.vo.UserEventGroupPermissionItem;
import com.imst.event.map.web.vo.UserLayerPermissionItem;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/component")
public class ComponentController {
	
	@Autowired EventService eventService;
	@Autowired EventMediaService eventMediaService;
	@Autowired TagService tagService;
	@Autowired GeoLayerService geoLayerService;
	@Autowired MapAreaService mapAreaService;
	@Autowired TileServerService tileServerService;
	@Autowired EventGroupService eventGroupService;
	@Autowired EventTypeService eventTypeService;
	@Autowired MapAreaGroupService mapAreaGroupService;
	@Autowired UserGroupIdService userGroupIdService;
	@Autowired UserUserIdService userUserIdService;
	@Autowired UserLayerPermissionService userLayerPermissionService;
	@Autowired AlertService alertService;
	@Autowired AlertEventRepository alertEventRepository;
	@Autowired UserEventGroupPermissionService userEventGroupPermissionService;
	@Autowired LayerRepository layerRepository;
	@Autowired EventLinkRepository eventLinkRepository;
	@Autowired EventsTimeCountRepository eventsTimeCountRepository;
	
	@Autowired MasterDao masterDao;
	
	
	@RequestMapping(value = "/eventGroups", method = RequestMethod.POST)
	@ResponseBody
	public List<EventGroupItem> eventGroup(@RequestBody EventRequestItem eventRequestItem) {
		
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(eventRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		List<EventGroupItem> eventGroupList = getUserEventGroupPermissionList(layerItem.getId());

		
		List<EventGroupItem> totalList = new ArrayList<>();
		/*******************************************/
		
		if (layerItem.getHasFullPermission()) {
			ExecutorService executor = Executors.newFixedThreadPool(10);
			
	    	List<Future<List<EventGroupItem>>> futureOrderListList = new ArrayList<>();
	    	
	    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
	    		
	    		CallableEventGroupItemService callableTest = new CallableEventGroupItemService(dataSourceInfo, layerItem.getId());
				futureOrderListList.add(executor.submit(callableTest));
	    		
	    	}
	    	executor.shutdown();
	    	
	    	    	
	    	for (Future<List<EventGroupItem>> futureOrderList : futureOrderListList) {
	    		
				try {
					totalList.addAll(futureOrderList.get());	
					
				} catch (InterruptedException e) {
					log.debug(e);
				} catch (ExecutionException e) {
					log.debug(e);
				}
						
			}
	    	eventGroupList.addAll(totalList);
		}
		

		/*******************************************/   	
    		
		return eventGroupList;
	}
	
	@RequestMapping(value = "/treeEventGroup", method = RequestMethod.POST)
	@ResponseBody
	public List<EventGroupTreeItem> treeEventGroup(@RequestBody EventRequestItem eventRequestItem) {

		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(eventRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		//izinli olunan grupların id listesi
//		List<Integer> eventGroupPermissionIdList = getUserEventGroupPermissionList(layerItem.getId());
		
		List<String> eventGroupdbNameIdList = eventRequestItem.getEventGroupdbNameIdList();
		
		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group

			return null;
		}
		
		
		List<EventGroupTreeItem> eventGroupTreeItemList = new ArrayList<EventGroupTreeItem>();
		List<EventGroupItem> eventGroupItemList = getUserEventGroupPermissionList(layerItem.getId());
//		eventGroupService.findAllAndSetDbName(masterDao, Sort.by(Direction.ASC, "id"), layerItem.getId(), Statics.DEFAULT_DB_NAME, eventGroupPermissionIdList);
		

		/*******************************************/
		
		List<EventGroupItem> totalList = new ArrayList<>();
		
		if (layerItem.getHasFullPermission()) {
			
			ExecutorService executor = Executors.newFixedThreadPool(10);
			
	    	List<Future<List<EventGroupItem>>> futureOrderListList = new ArrayList<>();
	    	
	    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
	    		
	    		CallableEventGroupItemService callableTest = new CallableEventGroupItemService(dataSourceInfo, layerItem.getId());
				futureOrderListList.add(executor.submit(callableTest));
	    		
	    	}
	    	executor.shutdown();
	    	
	    	    	
	    	for (Future<List<EventGroupItem>> futureOrderList : futureOrderListList) {
	    		
				try {
					totalList.addAll(futureOrderList.get());	
					
				} catch (InterruptedException e) {
					log.debug(e);
				} catch (ExecutionException e) {
					log.debug(e);
				}
						
			}
	    	eventGroupItemList.addAll(totalList);
		}

		/*******************************************/ 
		
		
		for(EventGroupItem item : eventGroupItemList) {
			
			String dbNameAndEventGroupId = item.getDbName() + "_" + item.getId();
			boolean showCheckboxes = eventGroupdbNameIdList.stream().filter(f -> f.equals(dbNameAndEventGroupId)).collect(Collectors.toList()).size() > 0;
			eventGroupTreeItemList.add(new EventGroupTreeItem(item.getId(),item.getParentId() != null ? item.getParentId() : 0,item.getName(), item.getDbName(), showCheckboxes));
		}
		
	        
	    EventGroupTree eventGroupTree = new EventGroupTree(eventGroupTreeItemList, null);
	    eventGroupTreeItemList = eventGroupTree.builTree();
	    
//	    List<EventGroupTreeItem> totalListToEventGroupTreeList = new ArrayList<>();
//	    totalList.forEach(item -> {
//	    	boolean showCheckboxes = eventGroupdbNameIdList.stream().filter(f -> f.equals(item.getDbName()+"_"+item.getId())).collect(Collectors.toList()).size() > 0;
//	    	EventGroupTreeItem tree = new EventGroupTreeItem(item.getId(), item.getParentId(), item.getName(), item.getDbName(), showCheckboxes);
//	    	totalListToEventGroupTreeList.add(tree);
//	    });	    
//	    eventGroupTreeItemList.addAll(totalListToEventGroupTreeList);
		return eventGroupTreeItemList;

	}
	
	
	@RequestMapping(value = "/geolayer", method = RequestMethod.POST)
	@ResponseBody
	public List<GeoLayerItem> geolayer(@RequestBody EventRequestItem eventRequestItem) {
		
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(eventRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		List<GeoLayerItem> geolayerList = geoLayerService.findAllProjectedBySidebar(Sort.by(Direction.ASC, "id"), layerItem.getId());
		
		return geolayerList;
	}
	
	@RequestMapping(value = "/key", method = RequestMethod.POST)
	@ResponseBody
	public KeyItemWrapper key(@RequestBody EventRequestItem eventRequestItem) {	
		
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(eventRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		List<EventGroupItem> eventGroupList = getUserEventGroupPermissionList(layerItem.getId());
		
		/*******************************************/
		List<EventGroupItem> totalList = new ArrayList<>();
		if (layerItem.getHasFullPermission()) {
			ExecutorService executor = Executors.newFixedThreadPool(10);
			
	    	List<Future<List<EventGroupItem>>> futureOrderListList = new ArrayList<>();
	    	
	    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
	    		
	    		CallableEventGroupItemService callableTest = new CallableEventGroupItemService(dataSourceInfo, layerItem.getId());
				futureOrderListList.add(executor.submit(callableTest));   		
	    	}

	    	executor.shutdown();
	    	
	    	    	
	    	for (Future<List<EventGroupItem>> futureOrderList : futureOrderListList) {
	    		
				try {
					totalList.addAll(futureOrderList.get());	
					
				} catch (InterruptedException e) {
					log.debug(e);
				} catch (ExecutionException e) {
					log.debug(e);
				}
						
			}
	    	eventGroupList.addAll(totalList);
		}

		/*******************************************/
		
//		List<EventTypeItem> eventTypeList = eventTypeService.findAll(Sort.by(Direction.ASC, "id"), null); //null ekledim
    	List<EventTypeItem> eventTypeList = eventService.prepareEventTypeWrapperList(layerItem.getId());

		List<MapAreaGroupItem> mapAreaGroupList = mapAreaGroupService.findAll(Sort.by(Direction.ASC, "id"), layerItem.getId());
						
		KeyItemWrapper result = new KeyItemWrapper(eventTypeList, eventGroupList, mapAreaGroupList);
				
		return result;
	}
	
	@RequestMapping(value = "/layer", method = RequestMethod.GET)
	@ResponseBody
	public List<UserLayerPermissionItem> layer(Model model) {	
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		List<UserLayerPermissionItem> userLayerPermissionList = sessionUser.getUserLayerPermissionList();
		
				
		userLayerPermissionList = userLayerPermissionList.stream().sorted(Comparator.comparing(UserLayerPermissionItem::getLayerName)).collect(Collectors.toList());
		return userLayerPermissionList;
	}
	
	@RequestMapping(value = "/mapArea", method = RequestMethod.POST)
	@ResponseBody
	public List<MapAreaItem> mapArea(@RequestBody EventRequestItem eventRequestItem) {

		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(eventRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		List<MapAreaItem> mapAreaItems = mapAreaService.findAllProjectedBySidebar(Sort.by(Direction.ASC, "id"), layerItem.getId());
		
		return mapAreaItems;
	}
	
	@RequestMapping(value = "/tileServer", method = RequestMethod.GET)
	@ResponseBody
	public List<TileServerItem> tileServer(Model model) {
		
		List<TileServerItem> tileServerItems = tileServerService.findAllProjectedBySidebar(Sort.by(Direction.ASC, "id"));
		
		return tileServerItems;
	}
	
	@RequestMapping(value = "/openDetailModalAlert", method = RequestMethod.POST)
	@ResponseBody
	public List<SidebarAlertItem> alertList(@RequestParam(name = "alertId") List<Integer> selectEventAlertId, @RequestParam(name="alertEventIds") List<Integer> idList){
		List<SidebarAlertItem> alertList = new ArrayList<>();
		
		int index = 0;		
		for(Integer alertId :selectEventAlertId) {
			
			alertList.addAll( alertService.findAllProjectedByAlert(Sort.by(Direction.ASC, "id"), alertId));
			
			AlertEvent alertEvent = alertEventRepository.findAllById(idList.get(index));
			Alert alert = new Alert();
			alert.setId(alertId);
			alertEvent.setAlert(alert);
			alertEvent.setReadState(true);
						
			alertEventRepository.save(alertEvent);
			
			index++;
		}
			
		return alertList;
		
	}
	
	@RequestMapping(value = "/getEventColumnLinks", method = RequestMethod.POST)
	@ResponseBody
	public List<EventLinkItem> GetEventColumnLinks(@RequestParam(name = "columnId") List<Integer> eventColumnIdList){
		
		List<EventLinkItem> columnForEventLinkItemList = eventLinkRepository.findAllByEventColumnIdIn(eventColumnIdList);
		
		return columnForEventLinkItemList;
		
	}
	
	
	@RequestMapping(value = "/getTimeInEventCounts", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Map<Object, Long>> GetTimeInEventCounts(@RequestParam("layerId") String currentLayerGuid, 
			@RequestParam(name = "year", required = false) Integer year, 
			@RequestParam(name = "month", required = false) Integer month, 
			@RequestParam(name = "isFirstOpening", required = false) Boolean isFirstOpening){
		
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		List<Integer> userPermEventGroupIds = sessionUser.getUserEventGroupPermissionList().stream().filter(f -> f.getEventGroupLayerId().equals(layerItem.getId())).map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		
		Map<String, Map<Object, Long>> map = new HashMap<>();
		
		//Default da şuan ki ay, gün ve yılın değerlerini döndürür.
		if(isFirstOpening == true && year == null && month == null) {
			
			
			Calendar calendar = Calendar.getInstance();
		    calendar.setTime(DateUtils.now());
		    Integer defaultMonth = calendar.get(Calendar.MONTH) +1;
		    Integer defaultYear = calendar.get(Calendar.YEAR);
		    
		    
		    Map<Object, Long> currentMonthInDays = eventsTimeCountRepository.findAllByEventGroupIdInAndEventMonthAndEventYear(userPermEventGroupIds,defaultMonth,defaultYear)
					.stream().collect(Collectors.groupingBy(item -> item.getEventDay(),  Collectors.summingLong(EventsTimeCountProjection::getEventCount)));
					
			List<EventsTimeCountProjection> currentMonthsInYear = eventsTimeCountRepository.findAllByEventGroupIdInAndEventYear(userPermEventGroupIds,defaultYear);
			Map<Object, Long> mapCurrentMonthsInYear = currentMonthsInYear.stream().collect(Collectors.groupingBy(item -> item.getEventMonth(),  Collectors.summingLong(EventsTimeCountProjection::getEventCount)));
				
			List<Object[]> yearsInEventCount = eventsTimeCountRepository.eventCountsInYears(userPermEventGroupIds);
			Map<Object, Long> mapYearsInEventCount = new HashMap<>();
			
			yearsInEventCount.forEach(item -> {
				
				Long eventCount = (Long)item[0];
				mapYearsInEventCount.put(item[1], eventCount);
				
			});
			
			map.put("DAYS", currentMonthInDays);
			map.put("MONTHS", mapCurrentMonthsInYear);
			map.put("YEARS", mapYearsInEventCount);
			
			
			return map;
		}
		
		//Aylara basıldığında ay ve gün değerlerini döndürür.
		if(month != null && year != null) {
			
			List<EventsTimeCountProjection> currentMonthInDays = eventsTimeCountRepository.findAllByEventGroupIdInAndEventMonthAndEventYear(userPermEventGroupIds,month,year);
			Map<Object, Long> mapCurrentDayssInMonth = currentMonthInDays.stream().collect(Collectors.groupingBy(item -> item.getEventDay(),  Collectors.summingLong(EventsTimeCountProjection::getEventCount)));
			
			List<EventsTimeCountProjection> currentYearInMonths = eventsTimeCountRepository.findAllByEventGroupIdInAndEventYear(userPermEventGroupIds,year);
			Map<Object, Long> mapCurrentMonthsInYear = currentYearInMonths.stream().collect(Collectors.groupingBy(item -> item.getEventMonth(),  Collectors.summingLong(EventsTimeCountProjection::getEventCount)));
	
			map.put("DAYS", mapCurrentDayssInMonth);
			map.put("MONTHS", mapCurrentMonthsInYear);
			
			return map;
			
		}

		//Yıllara basıldığında ay değerlerini döndürür.
		if(month == null && year != null) {
			
			List<EventsTimeCountProjection> currentYearInMonths = eventsTimeCountRepository.findAllByEventGroupIdInAndEventYear(userPermEventGroupIds,year);
			Map<Object, Long> mapCurrentMonthsInYear = currentYearInMonths.stream().collect(Collectors.groupingBy(item -> item.getEventMonth(),  Collectors.summingLong(EventsTimeCountProjection::getEventCount)));
	
			map.put("MONTHS", mapCurrentMonthsInYear);
			
			return map;
		}

		return map;
		
	}
	

	private List<EventGroupItem> getUserEventGroupPermissionList(Integer layerId){
		

		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		List<UserEventGroupPermissionItem> permList = sessionUser.getUserEventGroupPermissionList();
		List<Integer> permIdlist = permList.stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		
		List<EventGroupItem> currentLayerAllEventGroupList = eventGroupService.findAllAndSetDbName(masterDao, Sort.by(Direction.ASC, "id"), layerId ,Statics.DEFAULT_DB_NAME, permIdlist);	
		

		return currentLayerAllEventGroupList;
	}
	
	
	
}
