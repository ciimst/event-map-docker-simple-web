package com.imst.event.map.web.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.repositories.EventRepository;
import com.imst.event.map.web.db.repositories.EventTypeRepository;
import com.imst.event.map.web.db.specifications.EventExcelSpecification;
import com.imst.event.map.web.db.specifications.EventSpecifications;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.vo.AlertCriteriaEventItem;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.EventExcelItem;
import com.imst.event.map.web.vo.EventItem;
import com.imst.event.map.web.vo.EventItemForEventTypeSelectBox;
import com.imst.event.map.web.vo.EventTableViewExcelItem;
import com.imst.event.map.web.vo.EventTableViewItem;
import com.imst.event.map.web.vo.EventTableViewItemMultiselectEventType;
import com.imst.event.map.web.vo.EventTableViewItemWrapper;
import com.imst.event.map.web.vo.EventTypeItem;
import com.imst.event.map.web.vo.SidebarEventItem;
import com.imst.event.map.web.vo.SidebarEventItemForHeatMap;
import com.imst.event.map.web.vo.SidebarEventItemWrapper;
import com.imst.event.map.web.vo.SidebarEventItemWrapperForHeatMap;
import com.imst.event.map.web.vo.SidebarEventMediaItem;
import com.imst.event.map.web.vo.SidebarTagItem;
import com.imst.event.map.web.vo.UserEventGroupPermissionItem;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class EventService {
	
	@Autowired MasterDao masterDao;
	@Autowired EventRepository eventRepository;
	@Autowired EventTypeRepository eventTypeRepository;
	
	@Autowired EventMediaService eventMediaService;
	@Autowired TagService tagService;
	@Autowired AlertEventService alertEventService;
	
//	public Page<SidebarEventItem> findAll(MasterDao masterDao,PageRequest pageRequest,List<Integer> groupIdList, List<Integer> userIdList, Integer currentLayerId, Date startDate, Date endDate, Integer lastId, Integer alertLastId, String eventSearch){
//		Page<SidebarEventItem> eventItemList = masterDao.findAll(EventSpecifications.getAllEventSpecification(groupIdList,userIdList ,currentLayerId ,startDate ,endDate , lastId, alertLastId, eventSearch),pageRequest);
//		return eventItemList;
//	}
//

//	public Page<EventItem> findAllEvents(MasterDao masterDao, PageRequest pageRequest, List<Integer> groupIdList, List<Integer> userIdList){
//		return masterDao.findAll(EventSpecifications.eventTableSpecification(groupIdList, userIdList),pageRequest);
//	}

	public Page<SidebarEventItem> findAllProjectedBySidebar(MasterDao masterDao, PageRequest pageRequest, Date startDate, Date endDate, Integer lastId, List<Integer> groupIdList, List<Integer> userIdList, 
			Integer currentLayerId, Date lastScrollDate, Integer alertLastId, String eventSearch, Date firstScrollDate, List<Integer> eventTypeIdSearch, Double southWestLng, Double southWestLat,
			Double northEastLng, Double northEastLat, List<Integer> eventGroupIdList, String eventSearchCity, String eventSearchCountry, boolean isAlertEvent) {
		
		Page<SidebarEventItem> eventSidebarList = masterDao.findAll(EventSpecifications.sidebarSpecification(startDate, endDate, lastId, groupIdList, userIdList, currentLayerId, lastScrollDate, alertLastId, eventSearch, firstScrollDate, eventTypeIdSearch, southWestLng, southWestLat, northEastLng, northEastLat, eventGroupIdList, eventSearchCity, eventSearchCountry, isAlertEvent), pageRequest);
		return eventSidebarList;
	}
	
	public Page<SidebarEventItemForHeatMap> findAllProjectedBySidebarForHeatMap(MasterDao masterDao, PageRequest pageRequest, Date startDate, Date endDate, Integer lastId, List<Integer> groupIdList, List<Integer> userIdList, 
			Integer currentLayerId, Date lastScrollDate, Integer alertLastId, String eventSearch, Date firstScrollDate, List<Integer> eventTypeIdSearch, String eventSearchCity, String eventSearchCountry, List<Integer> eventGroupIdList) {
		
		Page<SidebarEventItemForHeatMap> eventSidebarList = masterDao.findAll(EventSpecifications.sidebarSpecificationForHeatMap(startDate, endDate, lastId, groupIdList, userIdList, currentLayerId, lastScrollDate, alertLastId, eventSearch, firstScrollDate, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventGroupIdList), pageRequest);
		return eventSidebarList;
	}
	
	public Page<AlertCriteriaEventItem> findAllProjectedByAlert(MasterDao masterDao, PageRequest pageRequest, Integer lastId) {
		
		Page<AlertCriteriaEventItem> alertCriteriaEventList = masterDao.findAll(EventSpecifications.alertSpecification(lastId), pageRequest);
		return alertCriteriaEventList;
	}
	
	public List<SidebarEventItemWrapper> prepareEventItemWrapperList(PageRequest pageRequest, Date startDate, Date endDate, Date lastScrollDate, Map<String, Integer> lastIdMap, Integer currentLayerId, String eventSearch, List<Integer> eventTypeIdSearch, Double southWestLng, Double southWestLat,
			Double northEastLng, Double northEastLat, List<String> eventGroupdbNameIdList, String eventSearchCity, String eventSearchCountry, boolean isAlertEvent) {
		return prepareEventItemWrapperList(pageRequest, startDate, endDate,lastScrollDate, lastIdMap, currentLayerId, null, eventSearch, null, eventTypeIdSearch, southWestLng, southWestLat,northEastLng, northEastLat, eventGroupdbNameIdList,eventSearchCity, eventSearchCountry, isAlertEvent);
	}
	
	public List<SidebarEventItemWrapperForHeatMap> prepareEventItemWrapperListForHeatMap(PageRequest pageRequest, Date startDate, Date endDate, Date lastScrollDate, Map<String, Integer> lastIdMap, Integer currentLayerId, String eventSearch, List<Integer> eventTypeIdSearch, String eventSearchCity, String eventSearchCountry, List<String> eventGroupdbNameIdList) {
		return prepareEventItemWrapperListForHeatMap(pageRequest, startDate, endDate,lastScrollDate, lastIdMap, currentLayerId, null, eventSearch, null, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventGroupdbNameIdList);
	}
	
	public List<EventTypeItem> prepareEventTypeList(MasterDao masterDao, List<Integer> userIdList, List<Integer> groupIdList, Integer currentLayerId){
		
		List<Integer> eventTypeIdList = eventRepository.findPermissionedEventTypeIdAndCountByAndStateId(currentLayerId, userIdList, groupIdList, StateE.TRUE.getValue());
		List<EventTypeItem> listNew = eventTypeRepository.findByIdIn(eventTypeIdList);
		
//		List<EventItemForEventTypeSelectBox> eventSidebarList = masterDao.findAll(EventSpecifications.eventTypeSpecification(groupIdList, userIdList, currentLayerId), Sort.by(Direction.DESC, "id"));	
//		List<EventTypeItem> list = eventSidebarList.stream().map(item -> new EventTypeItem(item)).collect(Collectors.toList());
//		
//		
//		list = list.stream() 
//				  .filter(distinctByKey(p -> p.getId())) 
//				  .collect(Collectors.toList());
		
		return listNew;
	}
	
	private static <T> Predicate<T> distinctByKey(
	    Function<? super T, ?> keyExtractor) {
	  
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>(); 
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null; 
	}
	
	public List<SidebarEventItemWrapper> prepareEventItemWrapperList(PageRequest pageRequest, Date startDate, Date endDate, Date lastScrollDate, 
			Map<String, Integer> lastIdMap, Integer currentLayerId, Map<String, Integer> alertLastIdMap, String eventSearch, Date firstScrollDate, List<Integer> eventTypeIdSearch, Double southWestLng, Double southWestLat,
			Double northEastLng, Double northEastLat, List<String> eventGroupdbNameIdList, String eventSearchCity, String eventSearchCountry, boolean isAlertEvent) {
		
		//eventGroupdbNameIdList listesinde veriler dbName_id olacak şekilde gelmektedir. Callable ve default db de eventgroupların doğru gidebilmesi için map de tutulmuştur.
		//Map de key kısmında dbName_id, value kısmında ise event group id bilgisi tutulmaktadır.
		Map<String, Integer> eventGroupIdListMap = new HashMap<>();
		List<Integer> defaultDbEventGroupIdList = new ArrayList<>();
		if(eventGroupdbNameIdList != null) {
			
			for(String item : eventGroupdbNameIdList) {
				
				String[] str = item.split("_");				
				Integer eventGroupId =  Integer.parseInt(str[1]);
				eventGroupIdListMap.put(item, eventGroupId);
			}
		}
		
		//Default veritabanında bulunan event group id bilgilerini çekebilmek için filter yapıldı.
		//filterda key kısmının dbName_id bilgisine bakılarak yapıldı ve sonra map ile MapList içindeki value(event group id) bilgisi alındı.
		defaultDbEventGroupIdList = eventGroupIdListMap.entrySet().stream().filter(x-> x.getKey().equals(Statics.DEFAULT_DB_NAME + "_" + x.getValue()) ).map(Map.Entry::getValue).collect(Collectors.toList());
		if(defaultDbEventGroupIdList.size() == 0) {
			defaultDbEventGroupIdList.add(0);
		}
		
		

		if(alertLastIdMap == null) {
			alertLastIdMap = new HashMap<String, Integer>();
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		List<Integer> groupIdList = sessionUser.getGroupIdList();
		List<Integer> userIdList = sessionUser.getUserIdList();
		
		
		
		Page<SidebarEventItem> eventSidebarList = findAllProjectedBySidebar(masterDao, pageRequest, startDate, endDate, lastIdMap.get(Statics.DEFAULT_DB_NAME), groupIdList, userIdList, currentLayerId, lastScrollDate, alertLastIdMap.get(Statics.DEFAULT_DB_NAME), eventSearch, firstScrollDate, eventTypeIdSearch,  southWestLng, southWestLat, northEastLng, northEastLat, defaultDbEventGroupIdList,eventSearchCity, eventSearchCountry, isAlertEvent);
		List<Integer> eventIdList = eventSidebarList.stream().map(SidebarEventItem::getId).collect(Collectors.toList());
				
		List<SidebarEventItemWrapper> list = new ArrayList<>();
		if(eventIdList.size() > 0) {
			
			int startSize = 0;
			int endSize = 0;
			int lastSize = 0;	
			
			List<SidebarEventMediaItem> eventMediaSidebarList = new ArrayList<>();
			while(endSize < eventIdList.size()) {
							
				startSize = lastSize; 
				endSize = lastSize + Statics.eventMediaListSize;
				
				if(endSize > eventIdList.size()) {
					endSize = eventIdList.size();
				}
			
				List<SidebarEventMediaItem> tempEventMediaSidebarList = eventMediaService.findAllProjectedBySidebar(masterDao, Sort.by(Direction.ASC, "id"), eventIdList.subList(startSize, endSize));
				eventMediaSidebarList.addAll(tempEventMediaSidebarList);
				lastSize += Statics.eventMediaListSize;
			}
			
			
			Map<Integer, List<SidebarEventMediaItem>> eventMediaMap = eventMediaSidebarList.stream().collect(Collectors.groupingBy(SidebarEventMediaItem::getEventId));
			
			List<SidebarTagItem> eventTagSidebarList = tagService.findAllProjectedBySidebar(masterDao, Sort.by(Direction.ASC, "id"), eventIdList);
			Map<Integer, List<SidebarTagItem>> eventTagMap = eventTagSidebarList.stream().collect(Collectors.groupingBy(SidebarTagItem::getEventId));
			
			list = eventSidebarList.getContent().stream().map(item -> new SidebarEventItemWrapper(item, eventMediaMap.get(item.getId()), eventTagMap.get(item.getId()), Statics.DEFAULT_DB_NAME)).collect(Collectors.toList());
			
			Optional<Integer> max = eventIdList.stream().max(Comparator.comparing(item-> item));
			lastIdMap.put(Statics.DEFAULT_DB_NAME, max.get());
		}
		
		/**************************************************************/
				
		ExecutorService executor = Executors.newFixedThreadPool(10);
		
    	List<Future<List<SidebarEventItemWrapper>>> futureOrderListList = new ArrayList<>();
    	
    	Collection<DataSourceInfo> values = Statics.tenantDataSourceInfoMap.values();
    	for (DataSourceInfo dataSourceInfo : values) {
    		
    		//Tenant veritabanında bulunan event group id bilgilerini çekebilmek için filter yapıldı.
    		//filterda key kısmının dbName_id bilgisine bakılarak yapıldı ve sonra map ile MapList içindeki value(event group id) bilgisi alındı.
    		//dbName bilgisi için dataSourceInfo da bulunan name bilgisi kullanıldı.
    		
    		//isAlert event her koşulda tenat da false olarak gönderildi.
    		
    		List<Integer> callableEventGroupIdList = eventGroupIdListMap.entrySet().stream().filter(x-> x.getKey().equals(dataSourceInfo.getName() + "_" + x.getValue()) ).map(Map.Entry::getValue).collect(Collectors.toList());

    		if(callableEventGroupIdList.size() == 0) {
    			callableEventGroupIdList.add(0);
    		}
    		
			CallableSideBarEventItemService callableTest = new CallableSideBarEventItemService(dataSourceInfo, lastIdMap.get(dataSourceInfo.getName()), groupIdList, userIdList, currentLayerId, lastScrollDate, pageRequest, startDate, endDate, alertLastIdMap.get(dataSourceInfo.getName()), eventSearch, firstScrollDate, eventTypeIdSearch,southWestLng, southWestLat, northEastLng, northEastLat, callableEventGroupIdList, eventSearchCity, eventSearchCountry, false);
			futureOrderListList.add(executor.submit(callableTest));
		}

    	executor.shutdown();
    	
    	List<SidebarEventItemWrapper> totalList = new ArrayList<>();    	
    	
    	
    	for (Future<List<SidebarEventItemWrapper>> futureOrderList : futureOrderListList) {
    		
			try {
				totalList.addAll(futureOrderList.get());	
				
	    		if(futureOrderList.get().size() > 0) {
	    			
	    			Optional<SidebarEventItemWrapper> max = futureOrderList.get().stream().max(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getId()));
	    			lastIdMap.put(max.get().getDbName(), max.get().getEvent().getId());
	    		}
			} catch (InterruptedException e) {
				log.debug(e);
			} catch (ExecutionException e) {
				log.debug(e);
			}
			
		}
    	totalList.addAll(list);
//    	/**************************************************************/
    	
    	return totalList;
	}
	
//	public List<SidebarEventItemWrapper> getAllEvent(Integer currentLayerId, Date startDate, Date endDate, Map<String, Integer> lastIdMap, Map<String, Integer> alertLastIdMap, String eventSearch){
//		
//		if(alertLastIdMap == null) {
//			alertLastIdMap = new HashMap<String, Integer>();
//		}
//		
//		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
//		User user = new User();
//		user.setId(sessionUser.getUserId());
//				
//		List<Integer> groupIdList = sessionUser.getGroupIdList();
//		List<Integer> userIdList = sessionUser.getUserIdList();
//		
//		List<SidebarEventItem> eventSidebarList = masterDao.findAll(EventSpecifications.getAllEventSpecification(groupIdList, userIdList, currentLayerId, startDate, endDate, lastIdMap.get(Statics.DEFAULT_DB_NAME), alertLastIdMap.get(Statics.DEFAULT_DB_NAME), eventSearch), Sort.by(Order.desc("eventDate"))).stream().collect(Collectors.toList());
//		List<Integer> eventIdList = eventSidebarList.stream().map(SidebarEventItem::getId).collect(Collectors.toList());
//		
//		List<SidebarEventItemWrapper> list = new ArrayList<>();
//		
//		if(eventIdList.size() > 0) {
//			
//			
//			int startSize = 0;
//			int endSize = 0;
//			int lastSize = 0;	
//			
//			List<SidebarEventMediaItem> eventMediaSidebarList = new ArrayList<>();
//			while(endSize < eventIdList.size()) {
//							
//				startSize = lastSize; 
//				endSize = lastSize + Statics.eventMediaListSize;
//				
//				if(endSize > eventIdList.size()) {
//					endSize = eventIdList.size();
//				}
//			
//				List<SidebarEventMediaItem> tempEventMediaSidebarList = eventMediaService.findAllProjectedBySidebar(masterDao, Sort.by(Direction.ASC, "id"), eventIdList.subList(startSize, endSize));
//				eventMediaSidebarList.addAll(tempEventMediaSidebarList);
//				lastSize += Statics.eventMediaListSize;
//			}
//			
//			
//			Map<Integer, List<SidebarEventMediaItem>> eventMediaMap = eventMediaSidebarList.stream().collect(Collectors.groupingBy(SidebarEventMediaItem::getEventId));
//			
//			List<SidebarTagItem> eventTagSidebarList = tagService.findAllProjectedBySidebar(masterDao, Sort.by(Direction.ASC, "id"), eventIdList);
//			Map<Integer, List<SidebarTagItem>> eventTagMap = eventTagSidebarList.stream().collect(Collectors.groupingBy(SidebarTagItem::getEventId));
//			
//			list = eventSidebarList.stream().map(item -> new SidebarEventItemWrapper(item, eventMediaMap.get(item.getId()), eventTagMap.get(item.getId()), Statics.DEFAULT_DB_NAME)).collect(Collectors.toList());
//			Optional<Integer> max = eventIdList.stream().max(Comparator.comparing(item-> item));
//			lastIdMap.put(Statics.DEFAULT_DB_NAME, max.get());
//		}
//		
//		/**************************************************************/
//		
//		ExecutorService executor = Executors.newFixedThreadPool(10);
//		
//    	List<Future<List<SidebarEventItemWrapper>>> futureOrderListList = new ArrayList<>();
//    	
//    	Collection<DataSourceInfo> values = Statics.tenantDataSourceInfoMap.values();
//    	for (DataSourceInfo dataSourceInfo : values) {
//			CallableTimeDimentionEventItemService callableTest = new CallableTimeDimentionEventItemService(dataSourceInfo, lastIdMap.get(dataSourceInfo.getName()), groupIdList, userIdList, currentLayerId, startDate, endDate, alertLastIdMap.get(dataSourceInfo.getName()),eventSearch);
//			futureOrderListList.add(executor.submit(callableTest));
//		}
//
//    	executor.shutdown();
//    	
//    	List<SidebarEventItemWrapper> totalList = new ArrayList<>();    	
//    	
//    	
//    	for (Future<List<SidebarEventItemWrapper>> futureOrderList : futureOrderListList) {
//    		
//			try {
//				totalList.addAll(futureOrderList.get());	
//				
//	    		if(futureOrderList.get().size() > 0) {
//	    			
//	    			Optional<SidebarEventItemWrapper> max = futureOrderList.get().stream().max(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getId()));
//	    			lastIdMap.put(max.get().getDbName(), max.get().getEvent().getId());
//	    		}
//			} catch (InterruptedException e) {
//				log.debug(e);
//			} catch (ExecutionException e) {
//				log.debug(e);
//			}
//			
//		}
//    	totalList.addAll(list);
//    	/**************************************************************/
//		
//		
//		return totalList;
//	}
	
	
	public Event getOldestEvent() {
		
		Page<Event> oldestEvent = eventRepository.findAll(PageRequest.of(0, 1, Direction.ASC, "eventDate"));
		return oldestEvent.toList().get(0);
	}
	
	public EventItem getLastEventById(MasterDao masterDao) {
		
		Page<EventItem> oldestEvent = masterDao.findAll(EventSpecifications.lastEventSpecification(), PageRequest.of(0, 1, Direction.DESC, "id"));
		return oldestEvent.toList().get(0);
	}
	
	public List<SidebarEventItemWrapperForHeatMap> prepareEventItemWrapperListForHeatMap(PageRequest pageRequest, Date startDate, Date endDate, Date lastScrollDate, 
			Map<String, Integer> lastIdMap, Integer currentLayerId, Map<String, Integer> alertLastIdMap, String eventSearch, Date firstScrollDate, List<Integer> eventTypeIdSearch, String eventSearchCity, String eventSearchCountry, List<String> eventGroupdbNameIdList) {
		
        //eventGroupdbNameIdList listesinde veriler dbName_id olacak şekilde gelmektedir. Callable ve default db de eventgroupların doğru gidebilmesi için map de tutulmuştur.
		//Map de key kısmında dbName_id, value kısmında ise event group id bilgisi tutulmaktadır.
		Map<String, Integer> eventGroupIdListMap = new HashMap<>();
		List<Integer> defaultDbEventGroupIdList = new ArrayList<>();
		if(eventGroupdbNameIdList != null) {
			
			for(String item : eventGroupdbNameIdList) {
				
				String[] str = item.split("_");				
				Integer eventGroupId =  Integer.parseInt(str[1]);
				eventGroupIdListMap.put(item, eventGroupId);
			}
		}
		
		//Default veritabanında bulunan event group id bilgilerini çekebilmek için filter yapıldı.
		//filterda key kısmının dbName_id bilgisine bakılarak yapıldı ve sonra map ile MapList içindeki value(event group id) bilgisi alındı.
		defaultDbEventGroupIdList = eventGroupIdListMap.entrySet().stream().filter(x-> x.getKey().equals(Statics.DEFAULT_DB_NAME + "_" + x.getValue()) ).map(Map.Entry::getValue).collect(Collectors.toList());
		
		if(defaultDbEventGroupIdList.size() == 0) {
			defaultDbEventGroupIdList.add(0);
		}

		if(alertLastIdMap == null) {
			alertLastIdMap = new HashMap<String, Integer>();
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		List<Integer> groupIdList = sessionUser.getGroupIdList();
		List<Integer> userIdList = sessionUser.getUserIdList();
		
		
		
		Page<SidebarEventItemForHeatMap> eventSidebarList = findAllProjectedBySidebarForHeatMap(masterDao, pageRequest, startDate, endDate, lastIdMap.get(Statics.DEFAULT_DB_NAME), groupIdList, userIdList, currentLayerId, lastScrollDate, alertLastIdMap.get(Statics.DEFAULT_DB_NAME), eventSearch, firstScrollDate, eventTypeIdSearch, eventSearchCity, eventSearchCountry, defaultDbEventGroupIdList);
		
		List<Integer> eventIdList = eventSidebarList.stream().map(SidebarEventItemForHeatMap::getId).collect(Collectors.toList());
				
		List<SidebarEventItemWrapperForHeatMap> list = new ArrayList<>();
		if(eventIdList.size() > 0) {
			
			list = eventSidebarList.getContent().stream().map(item -> new SidebarEventItemWrapperForHeatMap(item, Statics.DEFAULT_DB_NAME)).collect(Collectors.toList());
			
			Optional<Integer> max = eventIdList.stream().max(Comparator.comparing(item-> item));
			lastIdMap.put(Statics.DEFAULT_DB_NAME, max.get());
		}
		
		/**************************************************************/
				
		ExecutorService executor = Executors.newFixedThreadPool(10);
		
    	List<Future<List<SidebarEventItemWrapperForHeatMap>>> futureOrderListList = new ArrayList<>();
    	
    	Collection<DataSourceInfo> values = Statics.tenantDataSourceInfoMap.values();
    	for (DataSourceInfo dataSourceInfo : values) {
    		
    		//Tenant veritabanında bulunan event group id bilgilerini çekebilmek için filter yapıldı.
    		//filterda key kısmının dbName_id bilgisine bakılarak yapıldı ve sonra map ile MapList içindeki value(event group id) bilgisi alındı.
    		//dbName bilgisi için dataSourceInfo da bulunan name bilgisi kullanıldı.
    		
    		List<Integer> callableEventGroupIdList = eventGroupIdListMap.entrySet().stream().filter(x-> x.getKey().equals(dataSourceInfo.getName() + "_" + x.getValue()) ).map(Map.Entry::getValue).collect(Collectors.toList());

    		if(callableEventGroupIdList.size() == 0) {
    			callableEventGroupIdList.add(0);
    		}
    		
    		CallableSideBarEventItemServiceForHeatmap callableTest = new CallableSideBarEventItemServiceForHeatmap(dataSourceInfo, lastIdMap.get(dataSourceInfo.getName()), groupIdList, userIdList, currentLayerId, lastScrollDate, pageRequest, startDate, endDate, alertLastIdMap.get(dataSourceInfo.getName()), eventSearch, firstScrollDate, eventTypeIdSearch, eventSearchCity, eventSearchCountry, callableEventGroupIdList);
			futureOrderListList.add(executor.submit(callableTest));
		}

    	executor.shutdown();
    	
    	List<SidebarEventItemWrapperForHeatMap> totalList = new ArrayList<>();    	
    	
    	
    	for (Future<List<SidebarEventItemWrapperForHeatMap>> futureOrderList : futureOrderListList) {
    		
			try {
				totalList.addAll(futureOrderList.get());	
				
	    		if(futureOrderList.get().size() > 0) {
	    			
	    			Optional<SidebarEventItemWrapperForHeatMap> max = futureOrderList.get().stream().max(Comparator.comparing(item-> ((SidebarEventItemWrapperForHeatMap) item).getEvent().getId()));
	    			lastIdMap.put(max.get().getDbName(), max.get().getEvent().getId());
	    		}
			} catch (InterruptedException e) {
				log.debug(e);
			} catch (ExecutionException e) {
				log.debug(e);
			}
			
		}
    	totalList.addAll(list);
    	/**************************************************************/
    	
    	return totalList;
	}
	
	public Integer totalCountEvents(Integer currentLayerId, String eventSearch, List<Integer> eventTypeIdSearch, String eventSearchCity, String eventSearchCountry, List<String> eventGroupdbNameIdList, Boolean isAlertEvent,
			Date startDate, Date endDate) {			
		
		
		//eventGroupdbNameIdList listesinde veriler dbName_id olacak şekilde gelmektedir. Callable ve default db de eventgroupların doğru gidebilmesi için map de tutulmuştur.
		//Map de key kısmında dbName_id, value kısmında ise event group id bilgisi tutulmaktadır.
		Map<String, Integer> eventGroupIdListMap = new HashMap<>();
		List<Integer> defaultDbEventGroupIdList = new ArrayList<>();
		if(eventGroupdbNameIdList != null) {
			
			for(String item : eventGroupdbNameIdList) {
				
				String[] str = item.split("_");				
				Integer eventGroupId =  Integer.parseInt(str[1]);
				eventGroupIdListMap.put(item, eventGroupId);
			}
		}
		
		//Default veritabanında bulunan event group id bilgilerini çekebilmek için filter yapıldı.
		//filterda key kısmının dbName_id bilgisine bakılarak yapıldı ve sonra map ile MapList içindeki value(event group id) bilgisi alındı.
		defaultDbEventGroupIdList = eventGroupIdListMap.entrySet().stream().filter(x-> x.getKey().equals(Statics.DEFAULT_DB_NAME + "_" + x.getValue()) ).map(Map.Entry::getValue).collect(Collectors.toList());
		
		if(defaultDbEventGroupIdList.size() == 0) {
			defaultDbEventGroupIdList.add(0);
		}
		
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		List<Integer> groupIdList = sessionUser.getGroupIdList();
		List<Integer> userIdList = sessionUser.getUserIdList();
		
		
		Integer eventCount = getTotalCountEvents(masterDao, groupIdList, userIdList, currentLayerId, eventSearch, eventTypeIdSearch, eventSearchCity, eventSearchCountry,defaultDbEventGroupIdList, isAlertEvent, startDate, endDate);
		
		
		ExecutorService executor = Executors.newFixedThreadPool(10);		
    	List<Future<Integer>> futureOrderListList = new ArrayList<>();
    	
    	Collection<DataSourceInfo> values = Statics.tenantDataSourceInfoMap.values();
    	   	
    	for (DataSourceInfo dataSourceInfo : values) {
    		
    		//Tenant veritabanında bulunan event group id bilgilerini çekebilmek için filter yapıldı.
    		//filterda key kısmının dbName_id bilgisine bakılarak yapıldı ve sonra map ile MapList içindeki value(event group id) bilgisi alındı.
    		//dbName bilgisi için dataSourceInfo da bulunan name bilgisi kullanıldı.
    		
    		List<Integer> callableEventGroupIdList = eventGroupIdListMap.entrySet().stream().filter(x-> x.getKey().equals(dataSourceInfo.getName() + "_" + x.getValue()) ).map(Map.Entry::getValue).collect(Collectors.toList());
    		if(callableEventGroupIdList.size() == 0) {
    			callableEventGroupIdList.add(0);
    		}
    		
    		CallableTotalEventCountService callableTest = new CallableTotalEventCountService(dataSourceInfo, groupIdList, userIdList, currentLayerId, eventSearch, eventTypeIdSearch, eventSearchCity, eventSearchCountry, callableEventGroupIdList, false, startDate, endDate);
			futureOrderListList.add(executor.submit(callableTest));
		}

    	executor.shutdown();
    	
    	Integer totalEventCount = 0;
    	
    	for (Future<Integer> futureOrderList : futureOrderListList) {
    		
			try {
				
				totalEventCount = totalEventCount + futureOrderList.get();
	    		
			} catch (InterruptedException e) {
				log.debug(e);
			} catch (ExecutionException e) {
				log.debug(e);
			}
			
		}
    	
    	totalEventCount = totalEventCount + eventCount;
		return totalEventCount;
	}
	
	public Integer getTotalCountEvents(MasterDao masterDao, List<Integer> groupIdList, List<Integer> userIdList, Integer currentLayerId, String eventSearch, List<Integer> eventTypeIdSearch, 
			String eventSearchCity, String eventSearchCountry, List<Integer> eventGroupIdList, Boolean isAlertEvent, Date startDate, Date endDate){
		
		long executeCountQuery = masterDao.executeCountQueryAndGetResult(EventSpecifications.totalCountEvents( groupIdList, userIdList, currentLayerId,eventSearch, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventGroupIdList, isAlertEvent, startDate, endDate));
			
		return (int) executeCountQuery;
	}
	
	
	public List<EventTypeItem> prepareEventTypeWrapperList(Integer currentLayerId) {
		

		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
				
		List<Integer> groupIdList = sessionUser.getGroupIdList();
		List<Integer> userIdList = sessionUser.getUserIdList();
		
		
		
		List<EventTypeItem> defaultDbEventTypeList = prepareEventTypeList(masterDao, userIdList, groupIdList, currentLayerId);


		
//		/**************************************************************/
//				
//		ExecutorService executor = Executors.newFixedThreadPool(10);
//		
//    	List<Future<List<EventTypeItem>>> futureOrderListList = new ArrayList<>();
//    	
//    	Collection<DataSourceInfo> values = Statics.tenantDataSourceInfoMap.values();
//    	for (DataSourceInfo dataSourceInfo : values) {
//    		
//    		
//    		CallableSearchEventForEventTypeService callableTest = new CallableSearchEventForEventTypeService(dataSourceInfo, groupIdList, userIdList, currentLayerId);
//			futureOrderListList.add(executor.submit(callableTest));
//		}
//
//    	executor.shutdown();
//    	
//    	List<EventTypeItem> totalList = new ArrayList<>();    	
//    	
//    	
//    	for (Future<List<EventTypeItem>> futureOrderList : futureOrderListList) {
//    		
//			try {
//				totalList.addAll(futureOrderList.get());	
//				
//	    		
//			} catch (InterruptedException e) {
//				log.debug(e);
//			} catch (ExecutionException e) {
//				log.debug(e);
//			}
//			
//		}
//    	totalList.addAll(defaultDbEventTypeList);
//    	/**************************************************************/
//    	
//    	totalList = totalList.stream().distinct().collect(Collectors.toList());
    	return defaultDbEventTypeList;
	}
	
	public List<EventExcelItem> findAllProjectedByEventExcel(PageRequest pageRequest, MasterDao masterDao,  Integer lastId, List<Integer> groupIdList, List<Integer> userIdList, 
			Integer currentLayerId, Integer alertLastId, String eventSearch, List<Integer> eventTypeIdSearch, List<Integer> eventGroupIdList, String eventSearchCity, String eventSearchCountry, Date startDate, Date endDate, Boolean isAlertEvent) {
		
		Page<EventExcelItem> pageEventSidebarList = masterDao.findAll(EventExcelSpecification.sidebarSpecification(lastId, groupIdList, userIdList, currentLayerId, alertLastId, eventSearch, eventTypeIdSearch,  eventGroupIdList, eventSearchCity, eventSearchCountry, startDate, endDate, isAlertEvent), pageRequest);
		
		List<EventExcelItem> eventSidebarList = pageEventSidebarList.getContent();
		
		return eventSidebarList;
	}
	
	public List<EventExcelItem> prepareEventExcelList(PageRequest pageRequest,
			Map<String, Integer> lastIdMap, Integer currentLayerId, Map<String, Integer> alertLastIdMap, String eventSearch, 
			List<Integer> eventTypeIdSearch,  List<String> eventGroupdbNameIdList, String eventSearchCity, String eventSearchCountry, Date startDate, Date endDate, Boolean isAlertEvent){
		
		//eventGroupdbNameIdList listesinde veriler dbName_id olacak şekilde gelmektedir. Callable ve default db de eventgroupların doğru gidebilmesi için map de tutulmuştur.
				//Map de key kısmında dbName_id, value kısmında ise event group id bilgisi tutulmaktadır.
				Map<String, Integer> eventGroupIdListMap = new HashMap<>();
				List<Integer> defaultDbEventGroupIdList = new ArrayList<>();
				if(eventGroupdbNameIdList != null) {
					
					for(String item : eventGroupdbNameIdList) {
						
						String[] str = item.split("_");				
						Integer eventGroupId =  Integer.parseInt(str[1]);
						eventGroupIdListMap.put(item, eventGroupId);
					}
				}
				
				//Default veritabanında bulunan event group id bilgilerini çekebilmek için filter yapıldı.
				//filterda key kısmının dbName_id bilgisine bakılarak yapıldı ve sonra map ile MapList içindeki value(event group id) bilgisi alındı.
				defaultDbEventGroupIdList = eventGroupIdListMap.entrySet().stream().filter(x-> x.getKey().equals(Statics.DEFAULT_DB_NAME + "_" + x.getValue()) ).map(Map.Entry::getValue).collect(Collectors.toList());
				
				if(defaultDbEventGroupIdList.size() == 0) {
					defaultDbEventGroupIdList.add(0);
	    		}
				
				

				if(alertLastIdMap == null) {
					alertLastIdMap = new HashMap<String, Integer>();
				}
				
				UserItemDetails sessionUser = ApplicationContextUtils.getUser();
				User user = new User();
				user.setId(sessionUser.getUserId());
						
				List<Integer> groupIdList = sessionUser.getGroupIdList();
				List<Integer> userIdList = sessionUser.getUserIdList();
				
				
				
				List<EventExcelItem> eventSidebarList = findAllProjectedByEventExcel(pageRequest, masterDao, lastIdMap.get(Statics.DEFAULT_DB_NAME), groupIdList, userIdList, currentLayerId, alertLastIdMap.get(Statics.DEFAULT_DB_NAME), eventSearch, eventTypeIdSearch, defaultDbEventGroupIdList,eventSearchCity, eventSearchCountry, startDate, endDate, isAlertEvent);
//				List<Integer> eventIdList = eventSidebarList.stream().map(EventExcelItem::getId).collect(Collectors.toList());
						
//				List<SidebarEventItemWrapper> list = new ArrayList<>();
//				if(eventIdList.size() > 0) {
//					
//					int startSize = 0;
//					int endSize = 0;
//					int lastSize = 0;	
//					
//					List<SidebarEventMediaItem> eventMediaSidebarList = new ArrayList<>();
//					while(endSize < eventIdList.size()) {
//									
//						startSize = lastSize; 
//						endSize = lastSize + Statics.eventMediaListSize;
//						
//						if(endSize > eventIdList.size()) {
//							endSize = eventIdList.size();
//						}
//					
//						List<SidebarEventMediaItem> tempEventMediaSidebarList = eventMediaService.findAllProjectedBySidebar(masterDao, Sort.by(Direction.ASC, "id"), eventIdList.subList(startSize, endSize));
//						eventMediaSidebarList.addAll(tempEventMediaSidebarList);
//						lastSize += Statics.eventMediaListSize;
//					}
//					
//					
//					Map<Integer, List<SidebarEventMediaItem>> eventMediaMap = eventMediaSidebarList.stream().collect(Collectors.groupingBy(SidebarEventMediaItem::getEventId));
//					
//					List<SidebarTagItem> eventTagSidebarList = tagService.findAllProjectedBySidebar(masterDao, Sort.by(Direction.ASC, "id"), eventIdList);
//					Map<Integer, List<SidebarTagItem>> eventTagMap = eventTagSidebarList.stream().collect(Collectors.groupingBy(SidebarTagItem::getEventId));
//					
//					list = eventSidebarList.stream().map(item -> new SidebarEventItemWrapper(item, eventMediaMap.get(item.getId()), eventTagMap.get(item.getId()), Statics.DEFAULT_DB_NAME)).collect(Collectors.toList());
//					
//					Optional<Integer> max = eventIdList.stream().max(Comparator.comparing(item-> item));
//					lastIdMap.put(Statics.DEFAULT_DB_NAME, max.get());
//				}
				
//				/**************************************************************/
//						
//				ExecutorService executor = Executors.newFixedThreadPool(10);
//				
//		    	List<Future<List<SidebarEventItemWrapper>>> futureOrderListList = new ArrayList<>();
//		    	
//		    	Collection<DataSourceInfo> values = Statics.tenantDataSourceInfoMap.values();
//		    	for (DataSourceInfo dataSourceInfo : values) {
//		    		
//		    		//Tenant veritabanında bulunan event group id bilgilerini çekebilmek için filter yapıldı.
//		    		//filterda key kısmının dbName_id bilgisine bakılarak yapıldı ve sonra map ile MapList içindeki value(event group id) bilgisi alındı.
//		    		//dbName bilgisi için dataSourceInfo da bulunan name bilgisi kullanıldı.
//		    		
//		    		List<Integer> callableEventGroupIdList = eventGroupIdListMap.entrySet().stream().filter(x-> x.getKey().equals(dataSourceInfo.getName() + "_" + x.getValue()) ).map(Map.Entry::getValue).collect(Collectors.toList());
		//
//		    		
//					CallableSideBarEventItemService callableTest = new CallableSideBarEventItemService(dataSourceInfo, lastIdMap.get(dataSourceInfo.getName()), groupIdList, userIdList, currentLayerId, lastScrollDate, pageRequest, startDate, endDate, alertLastIdMap.get(dataSourceInfo.getName()), eventSearch, firstScrollDate, eventTypeIdSearch,southWestLng, southWestLat, northEastLng, northEastLat, callableEventGroupIdList, eventSearchCity, eventSearchCountry);
//					futureOrderListList.add(executor.submit(callableTest));
//				}
		//
//		    	executor.shutdown();
//		    		
//		    	
//		    	
//		    	for (Future<List<SidebarEventItemWrapper>> futureOrderList : futureOrderListList) {
//		    		
//					try {
//						totalList.addAll(futureOrderList.get());	
//						
//			    		if(futureOrderList.get().size() > 0) {
//			    			
//			    			Optional<SidebarEventItemWrapper> max = futureOrderList.get().stream().max(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getId()));
//			    			lastIdMap.put(max.get().getDbName(), max.get().getEvent().getId());
//			    		}
//					} catch (InterruptedException e) {
//						log.debug(e);
//					} catch (ExecutionException e) {
//						log.debug(e);
//					}
//					
//				}
//		    	totalList.addAll(list);
//		    	/**************************************************************/
		    	
		    	
		    	
		return eventSidebarList;
	}
	
	
	public Page<EventTableViewItem> findAllProjectedByEventTableView(MasterDao masterDao, PageRequest pageRequest, Date startDate, Date endDate, List<Integer> groupIdList, List<Integer> userIdList, 
			Integer currentLayerId, String title, String spot, String description, List<Integer> eventTypeIdSearch, Integer eventGroupId, String eventSearchCity, String eventSearchCountry, String eventSearchBlackListTag, Boolean state, List<Integer> permEventGroupIds, Boolean hasEventStateViewRole,
			String reserved1, String reserved2, String reserved3, String reserved4, String reserved5, Boolean isAlertEvent) {
		
		Page<EventTableViewItem> eventSidebarList = masterDao.findAll(EventSpecifications.eventTableViewSpecification(startDate, endDate, groupIdList, userIdList, currentLayerId, title, spot, description, eventTypeIdSearch, eventGroupId, eventSearchCity, eventSearchCountry, eventSearchBlackListTag, state, permEventGroupIds, hasEventStateViewRole, reserved1, reserved2, reserved3, reserved4, reserved5, isAlertEvent), pageRequest);
		return eventSidebarList;
	}
	
	public List<EventTableViewItemWrapper> prepareEventItemWrapperListForEventTableView(PageRequest pageRequest, Date startDate, Date endDate, 
			Integer currentLayerId, String title, String spot, String description, List<Integer> eventTypeIdSearch,
			Integer eventGroupId, String eventSearchCity, String eventSearchCountry, String eventSearchBlackListTag, Boolean state, Boolean hasEventStateViewRole,
			String reserved1, String reserved2, String reserved3, String reserved4, String reserved5, Boolean isAlertEvent) {
		
		//eventGroupdbNameIdList listesinde veriler dbName_id olacak şekilde gelmektedir. Callable ve default db de eventgroupların doğru gidebilmesi için map de tutulmuştur.
		//Map de key kısmında dbName_id, value kısmında ise event group id bilgisi tutulmaktadır.
//		Map<String, Integer> eventGroupIdListMap = new HashMap<>();
//		List<Integer> defaultDbEventGroupIdList = new ArrayList<>();
//		if(eventGroupdbNameIdList != null) {
//			
//			for(String item : eventGroupdbNameIdList) {
//				
//				String[] str = item.split("_");				
//				Integer eventGroupId =  Integer.parseInt(str[1]);
//				eventGroupIdListMap.put(item, eventGroupId);
//			}
//		}
		
		//Default veritabanında bulunan event group id bilgilerini çekebilmek için filter yapıldı.
		//filterda key kısmının dbName_id bilgisine bakılarak yapıldı ve sonra map ile MapList içindeki value(event group id) bilgisi alındı.
//		defaultDbEventGroupIdList = eventGroupIdListMap.entrySet().stream().filter(x-> x.getKey().equals(Statics.DEFAULT_DB_NAME + "_" + x.getValue()) ).map(Map.Entry::getValue).collect(Collectors.toList());
//		if(defaultDbEventGroupIdList.size() == 0) {
//			defaultDbEventGroupIdList.add(0);
//		}
		
		

		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
				
    	
    	List<Integer> permEventGroupIds = sessionUser.getUserEventGroupPermissionList().stream().filter(f -> f.getEventGroupLayerId().equals(currentLayerId)).map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		 
		List<Integer> groupIdList = sessionUser.getGroupIdList();
		List<Integer> userIdList = sessionUser.getUserIdList();
		
		
		
		Page<EventTableViewItem> eventSidebarList = findAllProjectedByEventTableView(masterDao, pageRequest, startDate, endDate, groupIdList, userIdList, currentLayerId,  title, spot, description,  eventTypeIdSearch, eventGroupId,eventSearchCity, eventSearchCountry, eventSearchBlackListTag, state, permEventGroupIds, hasEventStateViewRole, reserved1, reserved2, reserved3, reserved4, reserved5, isAlertEvent);
		List<Integer> eventIdList = eventSidebarList.stream().map(EventTableViewItem::getId).collect(Collectors.toList());
				
		List<EventTableViewItemWrapper> list = new ArrayList<>();
		if(eventIdList.size() > 0) {
			
			int startSize = 0;
			int endSize = 0;
			int lastSize = 0;	
			
			List<SidebarEventMediaItem> eventMediaSidebarList = new ArrayList<>();
			while(endSize < eventIdList.size()) {
							
				startSize = lastSize; 
				endSize = lastSize + Statics.eventMediaListSize;
				
				if(endSize > eventIdList.size()) {
					endSize = eventIdList.size();
				}
			
				List<SidebarEventMediaItem> tempEventMediaSidebarList = eventMediaService.findAllProjectedBySidebar(masterDao, Sort.by(Direction.ASC, "id"), eventIdList.subList(startSize, endSize));
				eventMediaSidebarList.addAll(tempEventMediaSidebarList);
				lastSize += Statics.eventMediaListSize;
			}
			
			
			Map<Integer, List<SidebarEventMediaItem>> eventMediaMap = eventMediaSidebarList.stream().collect(Collectors.groupingBy(SidebarEventMediaItem::getEventId));
			
			List<SidebarTagItem> eventTagSidebarList = tagService.findAllProjectedBySidebar(masterDao, Sort.by(Direction.ASC, "id"), eventIdList);
			Map<Integer, List<SidebarTagItem>> eventTagMap = eventTagSidebarList.stream().collect(Collectors.groupingBy(SidebarTagItem::getEventId));
			
			list = eventSidebarList.getContent().stream().map(item -> new EventTableViewItemWrapper(item, eventMediaMap.get(item.getId()), eventTagMap.get(item.getId()), Statics.DEFAULT_DB_NAME)).collect(Collectors.toList());
			
		}
		

    	
    	return list;
	}
	
	public Integer totalCountEventTableView(Integer currentLayerId, String title, String spot, String description, List<Integer> eventTypeIdSearch, String eventSearchCity, String eventSearchCountry, String eventSearchBlackListTag, Integer eventGroupId,
			Date startDate, Date endDate, Boolean state, Boolean hasEventStateViewRole, 
			String reserved1, String reserved2, String reserved3, String reserved4, String reserved5, Boolean isAlertEvent) {			
		
		
		//eventGroupdbNameIdList listesinde veriler dbName_id olacak şekilde gelmektedir. Callable ve default db de eventgroupların doğru gidebilmesi için map de tutulmuştur.
		//Map de key kısmında dbName_id, value kısmında ise event group id bilgisi tutulmaktadır.
//		Map<String, Integer> eventGroupIdListMap = new HashMap<>();
//		List<Integer> defaultDbEventGroupIdList = new ArrayList<>();
//		if(eventGroupdbNameIdList != null) {
//			
//			for(String item : eventGroupdbNameIdList) {
//				
//				String[] str = item.split("_");				
//				Integer eventGroupId =  Integer.parseInt(str[1]);
//				eventGroupIdListMap.put(item, eventGroupId);
//			}
//		}
		
		//Default veritabanında bulunan event group id bilgilerini çekebilmek için filter yapıldı.
		//filterda key kısmının dbName_id bilgisine bakılarak yapıldı ve sonra map ile MapList içindeki value(event group id) bilgisi alındı.
//		defaultDbEventGroupIdList = eventGroupIdListMap.entrySet().stream().filter(x-> x.getKey().equals(Statics.DEFAULT_DB_NAME + "_" + x.getValue()) ).map(Map.Entry::getValue).collect(Collectors.toList());
		

		
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
				
    	
    	List<Integer> permEventGroupIds = sessionUser.getUserEventGroupPermissionList().stream().filter(f -> f.getEventGroupLayerId() == currentLayerId).map(UserEventGroupPermissionItem::getEventGroupId).collect(Collectors.toList());
		 
		List<Integer> groupIdList = sessionUser.getGroupIdList();
		List<Integer> userIdList = sessionUser.getUserIdList();
		
		
		Integer eventCount = getTotalCountEventTableView(masterDao, groupIdList, userIdList, currentLayerId, title, spot, description, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventSearchBlackListTag, eventGroupId, startDate, endDate, state, permEventGroupIds, hasEventStateViewRole, reserved1, reserved2, reserved3, reserved4, reserved5, isAlertEvent);
		

		return eventCount;
	}
	
	public Integer getTotalCountEventTableView(MasterDao masterDao, List<Integer> groupIdList, List<Integer> userIdList, Integer currentLayerId, String title, String spot, String description, List<Integer> eventTypeIdSearch, 
			String eventSearchCity, String eventSearchCountry, String eventSearchBlackListTag, Integer eventGroupId, Date startDate, Date endDate, Boolean state, List<Integer> permEventGroupIds, Boolean hasEventStateViewRole, 
			String reserved1, String reserved2, String reserved3, String reserved4, String reserved5, Boolean isAlertEvent){
		
		long executeCountQuery = masterDao.executeCountQueryAndGetResult(EventSpecifications.totalCountEventTableView( groupIdList, userIdList, currentLayerId, title, spot, description, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventSearchBlackListTag, eventGroupId, startDate, endDate, state, permEventGroupIds, hasEventStateViewRole, reserved1, reserved2, reserved3, reserved4, reserved5, isAlertEvent));
			
		return (int) executeCountQuery;
	}
	
	public List<EventExcelItem> prepareEventTableViewExcelList(PageRequest pageRequest, Date startDate, Date endDate,
			Integer currentLayerId, EventTableViewItemMultiselectEventType eventTableViewItem, Boolean hasEventStateViewRole){
		
			List<Integer> eventGroupIdList = new ArrayList<>();
			if(eventTableViewItem.getEventGroupId() != null) {
				eventGroupIdList.add(eventTableViewItem.getEventGroupId());
			}
				
			UserItemDetails sessionUser = ApplicationContextUtils.getUser();
			User user = new User();
			user.setId(sessionUser.getUserId());
					
			List<Integer> groupIdList = sessionUser.getGroupIdList();
			List<Integer> userIdList = sessionUser.getUserIdList();
				
				
			List<EventExcelItem> eventSidebarList = findAllProjectedByEventTableViewExcel(masterDao, pageRequest, startDate, endDate, groupIdList, userIdList, 
			currentLayerId,  eventTableViewItem.getTitle(), eventTableViewItem.getSpot(), eventTableViewItem.getDescription(),  eventTableViewItem.getEventTypeId(), eventGroupIdList, eventTableViewItem.getCity(), eventTableViewItem.getCountry(), eventTableViewItem.getBlackListTag(), eventTableViewItem.getState(), hasEventStateViewRole,
			eventTableViewItem.getReserved1(), eventTableViewItem.getReserved2(), eventTableViewItem.getReserved3(), eventTableViewItem.getReserved4(), eventTableViewItem.getReserved5(), eventTableViewItem.getIsAlertEvent());
			
	    	
		return eventSidebarList;
	}
	
	public List<EventExcelItem> findAllProjectedByEventTableViewExcel( MasterDao masterDao, PageRequest pageRequest, Date startDate, Date endDate, List<Integer> groupIdList, List<Integer> userIdList, 
			Integer currentLayerId,  String title, String spot, String description,  List<Integer> eventTypeId, List<Integer> eventGroupIdList, String city, String country, String eventSearchBlackListTag, Boolean state, Boolean hasEventStateViewRole, String reserved1, String reserved2, String reserved3, String reserved4, String reserved5, Boolean isAlertEvent) {
		
		Page<EventExcelItem> pageEventSidebarList = masterDao.findAll(EventExcelSpecification.eventTableViewExcelSpecification(startDate, endDate, groupIdList, userIdList, currentLayerId,  title, spot, description, eventTypeId, eventGroupIdList, city, country, eventSearchBlackListTag, state, hasEventStateViewRole, reserved1, reserved2, reserved3, reserved4, reserved5, isAlertEvent), pageRequest);
		
		List<EventExcelItem> eventSidebarList = pageEventSidebarList.getContent();
		
		return eventSidebarList;
	}
	
	
}
