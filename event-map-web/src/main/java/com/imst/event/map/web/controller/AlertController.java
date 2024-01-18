package com.imst.event.map.web.controller;

import java.sql.Timestamp;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.imst.event.map.hibernate.entity.Alert;
import com.imst.event.map.hibernate.entity.AlertEvent;
import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserLayerPermission;
import com.imst.event.map.web.constant.LogTypeE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.projections.AlertProjection;
import com.imst.event.map.web.db.projections.AlertEventCountProjection;
import com.imst.event.map.web.db.repositories.AlertEventRepository;
import com.imst.event.map.web.db.repositories.AlertRepository;
import com.imst.event.map.web.db.repositories.EventTypeRepository;
import com.imst.event.map.web.db.repositories.UserLayerPermissionRepository;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.services.AlertEventService;
import com.imst.event.map.web.services.DBLogger;
import com.imst.event.map.web.services.UserLayerPermissionService;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.utils.SpatialUtil;
import com.imst.event.map.web.vo.AlertItem;
import com.imst.event.map.web.vo.AlertRequestItem;
import com.imst.event.map.web.vo.EventTypeItem;
import com.imst.event.map.web.vo.GenericResponseItem;
import com.imst.event.map.web.vo.LayerSimpleItem;
import com.imst.event.map.web.vo.UserEventGroupPermissionItem;
import com.imst.event.map.web.vo.UserItem;
import com.vividsolutions.jts.geom.Polygon;

@Controller
@RequestMapping("/alert")
public class AlertController {
	
	@Autowired AlertRepository alertRepository;
	@Autowired UserLayerPermissionService userLayerPermissionService;
	@Autowired EventTypeRepository eventTypeRepository;
	@Autowired UserLayerPermissionRepository userLayerPermissionRepository;
	@Autowired AlertEventRepository alertEventRepository;
	@Autowired AlertEventService alertEventService;
	
	@Autowired
	private DBLogger dbLogger;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public GenericResponseItem add(@RequestBody AlertRequestItem alertRequestItem) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, "Başarılı");
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(alertRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		Map<String, Object> alertsForLog = new TreeMap<>();
		LogTypeE logTypeE;
		
		
		Alert alert = new Alert();
		
		Polygon polygon = null;
		if(alertRequestItem.getIsCircle() == null || !alertRequestItem.getIsCircle()) { // Polygon
			polygon = SpatialUtil.getPolygon(alertRequestItem.getLatLongItemArr());	
		}else { // Circle
			polygon = SpatialUtil.getCircle(alertRequestItem.getLatLongItemArr());
		}
		
		alert.setPolygonCoordinate(polygon);
		
		alert.setLayer(layerItem.getLayerWithId());
		
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(userItemDetails.getUserId());
		alert.setUser(user);
		
		alert.setCreateDate(DateUtils.nowT());
		
		logTypeE = LogTypeE.ALERT_ADD;
		
		alert.setEventGroup(null);
		if(alertRequestItem.getEventGroupId() != null && alertRequestItem.getEventGroupId() != 0) {
			EventGroup eventGroup = new EventGroup();
			eventGroup.setId(alertRequestItem.getEventGroupId());
			alert.setEventGroup(eventGroup);
		}
		
		alert.setEventType(null);
		if(alertRequestItem.getEventTypeId() != null && alertRequestItem.getEventTypeId() != 0) {
			EventType eventType = new EventType();
			eventType.setId(alertRequestItem.getEventTypeId());
			alert.setEventType(eventType);
		}
						
		alert.setEventGroupDbName(alertRequestItem.getEventGroupDbName());			
		alert.setQuery(alertRequestItem.getQuery());						
		alert.setReservedId(alertRequestItem.getReservedId());			
		alert.setReservedKey(alertRequestItem.getReservedKey());						
		alert.setReservedType(alertRequestItem.getReservedType());						
		alert.setReservedLink(alertRequestItem.getReservedLink());				
		alert.setName(alertRequestItem.getName());		
		alert.setColor(alertRequestItem.getColor());
		Alert saved = alertRepository.save(alert);
		
		
		alertsForLog.put("new", AlertItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(alertsForLog), logTypeE);
		
		genericResponseItem.setData(saved.getId());
		
		return genericResponseItem;
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public Boolean edit(@RequestBody AlertRequestItem alertRequestItem) {
					
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(alertRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return false;
		}
		
		Map<String, Object> alertsForLog = new TreeMap<>();
		LogTypeE logTypeE;
		logTypeE = LogTypeE.ALERT_EDIT;
		
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(userItemDetails.getUserId());
		
		Alert alert = alertRepository.findByIdAndLayerAndUser(alertRequestItem.getId(), layerItem.getLayerWithId(), user);
		if(alert == null) {
			return false;
		}
		
		Polygon polygon = SpatialUtil.getPolygon(alertRequestItem.getLatLongItemArr());
		alert.setPolygonCoordinate(polygon);
		
		alert.setUpdateDate(DateUtils.nowT());
		
		Alert saved = alertRepository.save(alert);
		
		alertsForLog.put("old", AlertItem.newInstanceForLog(saved));
		
		dbLogger.log(new Gson().toJson(alertsForLog), logTypeE);

		return true;
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Boolean delete(@RequestBody AlertRequestItem alertRequestItem) {
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(alertRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return false;
		}
		
		
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(userItemDetails.getUserId());
		
		Alert alert = alertRepository.findByIdAndLayerAndUser(alertRequestItem.getId(), layerItem.getLayerWithId(), user);
		if(alert == null) {
			return false;
		}
		
		List<AlertEvent> alertEventList = alertEventRepository.findAllByAlertId(alert.getId());
		
		alertEventRepository.deleteAll(alertEventList);
		alertRepository.delete(alert);
		
		
		
		Map<String, Object> alertsForLog = new TreeMap<>();
		alertsForLog.put("deleted", AlertItem.newInstanceForLog(alert));
		
		dbLogger.log(new Gson().toJson(alertsForLog), LogTypeE.ALERT_DELETE);
		
		
		return true;
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.POST)
	@ResponseBody
	public List<AlertItem> get(@RequestBody AlertRequestItem alertRequestItem) {
		
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(alertRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(userItemDetails.getUserId());
		
		List<AlertProjection> list = alertRepository.findAllProjectedByLayerAndUserOrderByIdDesc(layerItem.getLayerWithId(), user);
		List<Integer> alertIdList = list.stream().map(AlertProjection::getId).collect(Collectors.toList());
		
		List<AlertEventCountProjection> alertWithAlertEventCountList =  alertEventRepository.findAllByAlertEventCount(alertIdList);
		List<AlertItem> alertItemList = list.stream().map(item->new AlertItem(item, layerItem.getGuid(), alertWithAlertEventCountList)).collect(Collectors.toList());
		
		return alertItemList;
	}
	
	@RequestMapping(value="/get/eventType", method = RequestMethod.POST)
	@ResponseBody
	public List<EventTypeItem> eventType() {
		List<EventType> list = eventTypeRepository.findAll();
		
		String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		List<EventTypeItem> eventTypeItemList = new ArrayList<>();
		list.forEach(item->{

			EventTypeItem eventTypeItem = new EventTypeItem(item);
			String name = ApplicationContextUtils.getMessage("icons." + item.getCode(), locale);
			name = name.equals("icons." + item.getCode()) ? item.getName() : name;
			eventTypeItem.setName(name);
			eventTypeItemList.add(eventTypeItem);

		});
		
		Collator collator = Collator.getInstance(locale);
		collator.setStrength(Collator.PRIMARY);
		List<EventTypeItem> sortedList = eventTypeItemList.stream()
                .sorted(Comparator.comparing(EventTypeItem::getName, collator))
                .collect(Collectors.toList());
		
		return sortedList;
	}
	
	@RequestMapping(value="/editAlertModal", method = RequestMethod.POST)
	@ResponseBody
	public Boolean edit(AlertItem alertItem) {
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(alertItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to change alert under this region
			return false;
		}
		
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(userItemDetails.getUserId());
		
		Alert alert = alertRepository.findByIdAndLayerAndUser(alertItem.getId(), layerItem.getLayerWithId(), user);
		if(alert == null) {
			return false;
		}
		
		Timestamp nowT = DateUtils.nowT();
				
		
		alert.setName(alertItem.getName());
		alert.setQuery(alertItem.getQuery());
		
		alert.setEventGroup(null);
		if(alertItem.getEventGroupId() != null && alertItem.getEventGroupId() != 0) {
			EventGroup eventGroup = new EventGroup();
			eventGroup.setId(alertItem.getEventGroupId());
			alert.setEventGroup(eventGroup);
		}
		
		if(alertItem.getEventGroupId() != null && alertItem.getEventGroupId() != 0  && alertItem.getEventGroupDbName() != null) {
			alert.setEventGroupDbName(alertItem.getEventGroupDbName());
		}
		
		alert.setEventType(null);
		if(alertItem.getEventTypeId() != null && alertItem.getEventTypeId() != 0) {
			EventType eventType = new EventType();
			eventType.setId(alertItem.getEventTypeId());
			alert.setEventType(eventType);
		}
		
		alert.setReservedId(alertItem.getReservedId());
		alert.setReservedKey(alertItem.getReservedKey());
		alert.setReservedType(alertItem.getReservedType());
		alert.setReservedLink(alertItem.getReservedLink());
		alert.setColor(alertItem.getColor());
		
		alert.setUpdateDate(nowT);
		
		try {
			alertRepository.save(alert);	
		} catch (Exception e) {
			return false;
		}
		
		
		return true;
	}
	
	@RequestMapping(value="/getAlertPermissionUser", method = RequestMethod.POST)
	@ResponseBody
	public List<UserItem> alertShareUserList(@RequestParam(name="layerId") String layerId){
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(layerId);
		
		if(layerItem == null) {
			return null;
		}
		
		List<UserLayerPermission> userLayerPermissionlist = userLayerPermissionRepository.findAllByLayerId(layerItem.getId());	
		List<User> userLayerList = userLayerPermissionlist.stream().map(UserLayerPermission::getUser).collect(Collectors.toList());
		
		List<UserItem> userList = userLayerList.stream().map(item->new UserItem(item)).collect(Collectors.toList());
		
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		
		int index = 0;
		int userId = userItemDetails.getUserId();
		while(index < userList.size()) {
			
			if(userList.get(index).getId() == userId) {
				userList.remove(index);
			}
			index++;
		}		
		
		userList = userList.stream().sorted(Comparator.comparing(UserItem::getName, Statics.sortedCollator())).collect(Collectors.toList());
		return userList;
	}
	
	@RequestMapping(value="/addSharedAlert", method = RequestMethod.POST)
	@ResponseBody
	public Boolean addSharedAlert(@RequestParam(name = "alertId") Integer alertId, @RequestBody List<Integer> userIdList) {
		
		if(userIdList.size() > 0) {
		
			Alert sharedAlert = alertRepository.findAllById(alertId);
			
			for(Integer id: userIdList) {
				
				Alert alert = new Alert();				
								
				User user = new User();
				user.setId(id);	
				
				alert.setUser(user);
				alert.setName(sharedAlert.getName());
				alert.setLayer(sharedAlert.getLayer());
				alert.setPolygonCoordinate(sharedAlert.getPolygonCoordinate());
				alert.setCreateDate(DateUtils.nowT());
				alert.setUpdateDate(sharedAlert.getUpdateDate());
				alert.setQuery(sharedAlert.getQuery());
				alert.setEventType(sharedAlert.getEventType());
				alert.setEventGroup(sharedAlert.getEventGroup());
				alert.setReservedKey(sharedAlert.getReservedKey());
				alert.setReservedType(sharedAlert.getReservedType());
				alert.setReservedId(sharedAlert.getReservedId());
				alert.setReservedLink(sharedAlert.getReservedLink());
				alert.setEventGroupDbName(sharedAlert.getEventGroupDbName());
				
				
				UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
								
				alert.setSharedBy(userItemDetails.getDisplayName());
				alertRepository.save(alert);
			}	
			
		}else {
			
			return false;
		}
		
		
		return true;
	}
	
	@RequestMapping(value="/readAlarmCount", method=RequestMethod.POST)
	@ResponseBody
	public Integer unReadAlarmCount(String layerId) {
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(layerId);
		
		if(layerItem == null) {
			return null;
		}
		
		UserItemDetails userItemDetails = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(userItemDetails.getUserId());
		
		List<Integer> permEventGroupIds = userItemDetails.getUserEventGroupPermissionList().stream().map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		Integer unReadAlarmCount = alertEventService.countAllUnreadByUserAndLayer(user.getId(), layerItem.getId(), permEventGroupIds);
		
		return (int) unReadAlarmCount;
	}
	
}
