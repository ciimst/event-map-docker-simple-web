package com.imst.event.map.web.controller;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imst.event.map.hibernate.entity.AlertState;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.constant.SettingsE;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.services.AlertEventService;
import com.imst.event.map.web.services.AlertService;
import com.imst.event.map.web.services.EventService;
import com.imst.event.map.web.services.EventTypeService;
import com.imst.event.map.web.services.UserLayerPermissionService;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.utils.SettingsUtil;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.EventRequestItem;
import com.imst.event.map.web.vo.EventTypeItem;
import com.imst.event.map.web.vo.LayerSimpleItem;
import com.imst.event.map.web.vo.SidebarAlertEventItem;
import com.imst.event.map.web.vo.SidebarAllWrapper;
import com.imst.event.map.web.vo.SidebarAllWrapperForHeatMap;
import com.imst.event.map.web.vo.SidebarEventItemWrapper;
import com.imst.event.map.web.vo.SidebarEventItemWrapperForHeatMap;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/event")
public class EventController {
	
	@Autowired private EventService eventService;
	@Autowired private EventTypeService eventTypeService;
	@Autowired private UserLayerPermissionService userLayerPermissionService;
	@Autowired private AlertEventService alertEventService;
	@Autowired private AlertService alertService;
	
	

	
	@RequestMapping(value = "/totalCountEvents", method = RequestMethod.GET)
	@ResponseBody
	public Integer totalCountEvents(
			  @RequestParam(name = "layerId") String currentLayerGuid,
			  @RequestParam(name= "eventSearch", required= false) String eventSearch,
			  @RequestParam(name= "eventTypeIdSearch", required= false) List<Integer> eventTypeIdSearch,
			  @RequestParam(name= "eventSearchCity", required= false) String eventSearchCity,
			  @RequestParam(name= "eventSearchCountry", required= false) String eventSearchCountry,
			  @RequestParam(name= "eventGroupdbNameIdList") List<String> eventGroupdbNameIdList,
			  @RequestParam("startDateStr") String startDateStr,
			  @RequestParam("endDateStr") String endDateStr
			) {


		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group

			return null;
		}

		Date startDate = DateUtils.convertToDate(startDateStr, DateUtils.TURKISH);
		Date endDate = DateUtils.convertToDate(endDateStr, DateUtils.TURKISH);
		
    	Integer totalEventCount = eventService.totalCountEvents(layerItem.getId(), eventSearch, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventGroupdbNameIdList, false, startDate, endDate);
		return totalEventCount;
	}
	
	
	@RequestMapping(value = "/loadAllEvents", method = RequestMethod.GET)
	@ResponseBody
	public String loadAllEvents(
			  @RequestParam(name = "layerId") String currentLayerGuid,
			  @RequestParam(name= "eventSearch", required= false) String eventSearch,
			  @RequestParam(name= "eventTypeIdSearch", required= false) List<Integer> eventTypeIdSearch,
			  @RequestParam(name= "eventSearchCity", required= false) String eventSearchCity,
			  @RequestParam(name= "eventSearchCountry", required= false) String eventSearchCountry,
			  @RequestParam(name= "eventGroupdbNameIdList") List<String> eventGroupdbNameIdList,
			  @RequestParam("startDateStr") String startDateStr,
			  @RequestParam("endDateStr") String endDateStr) throws JsonProcessingException {

		
		try {
			LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
			if(layerItem == null) {
				// there is no permission to see events under this region
				return null;
			}
			
			
			if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
				// there is no permission to see events under this event group

				return null;
			}
			
			Integer page = 0;
			int maxPerLoad = SettingsUtil.getInteger(SettingsE.WEB_LOAD_ALL_EVENTS);
			int pageLoadLimit = 10000;

			Date lastScrollDate = null;

			PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.desc("eventDate"), Order.desc("createDate")));


			Map<String, Integer> lastIdMap = new HashMap<>();
	    	lastIdMap.put(Statics.DEFAULT_DB_NAME, null);

	    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
	    		lastIdMap.put(dataSourceInfo.getName(), null);
	    	}

	    	List<SidebarEventItemWrapper> totalList = new ArrayList<>();
			Long lastScrollDateLong = null;
			
			Date startDate = DateUtils.convertToDate(startDateStr, DateUtils.TURKISH);
			Date endDate = DateUtils.convertToDate(endDateStr, DateUtils.TURKISH);
			
			
	    	boolean keepMakingList = true;
	    	while(keepMakingList) {
	    		
	    		List<SidebarEventItemWrapper> list = eventService.prepareEventItemWrapperList(pageRequest, startDate, endDate,  lastScrollDate, lastIdMap, layerItem.getId(), eventSearch, eventTypeIdSearch, null, null, null, null, eventGroupdbNameIdList, eventSearchCity, eventSearchCountry, false);
	    		Collections.addAll(totalList, fillDataNotExistForOtherDbs(list).toArray(new SidebarEventItemWrapper[0]));
	    		totalList = totalList.stream().sorted(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getEventDate()).reversed()).collect(Collectors.toList());
	    		
	        	try {
	        		lastScrollDateLong = totalList.get(totalList.size() - 1).getEvent().getEventDate().getTime();
	        		
	        		if(lastScrollDateLong != null) {
	        			lastScrollDate = new Date(lastScrollDateLong);
	        		}
	        		
	        		lastIdMap = new HashMap<>();
	            	lastIdMap.put(Statics.DEFAULT_DB_NAME, null);

	            	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
	            		lastIdMap.put(dataSourceInfo.getName(), null);
	            	}
	        		
	    		} catch (Exception e) {	}
	        	
	        	keepMakingList = list.size() > 0 && totalList.size() < maxPerLoad ? true : false;
	    	}
	    	
	    	
	    	totalList.forEach(item -> {
	    		
	    		if(item.getEvent().getId() == 128) {
	    			System.out.println(item.getEvent().getLatitude());
	    		}
	    	});
	    	

	    	SidebarAllWrapper sidebarAllWrapper = new SidebarAllWrapper(totalList, lastIdMap);
	    	
	    	//Lastscrolldate setlenmesinin nedeni javascript tarafında bunun tutulup scrollde sorguya katılıyor olması. Yeni olay çekerken lastId ve lastScrollDate kontrolü yapıldığı için setlenmesi gerekiyor.
	    	//Buradan toplu liste ekrana dönüyor ve sayfada olaylar scroll yapılırken buradan dönülen liste bitene kadar scroll işleminde var olan listeden sidebarın doldurulması ve dolasıyla scroll metoduna oş istek satması sağlandı.
	    	try {
	    		lastScrollDateLong = totalList.get(totalList.size() - 1).getEvent().getEventDate().getTime();
	    		
			} catch (Exception e) {	}
	    	sidebarAllWrapper.setLastScrollDate(lastScrollDateLong);
	    	
	    	try {
	    		// page refresh esnasında sonradan eklenmiş eski tarihli bir olayın idsinin büyük olmasından dolayı sayfa açıldıktan sonra refresh ile görünmesini önlemek için eklenmiştir
	    		sidebarAllWrapper.setFirstScrollDate(DateUtils.now().getTime());
			} catch (Exception e) {	}   
	    	
			return new ObjectMapper().writeValueAsString(sidebarAllWrapper);
			
		}catch(Exception e) {
			log.error(e);
		}
		

		return new ObjectMapper().writeValueAsString(new SidebarAllWrapper());
	}


	@RequestMapping(value = "/scroll", method = RequestMethod.GET)
	@ResponseBody
	public SidebarAllWrapper eventsScroll(
			  @RequestParam(name = "lastScrollDate", required = false) Long lastScrollDateLong,
			  @RequestParam(name = "layerId") String currentLayerGuid,
			  @RequestParam(name= "eventSearch", required= false) String eventSearch,
			  @RequestParam(name= "eventSearchCity", required= false) String eventSearchCity,
			  @RequestParam(name= "eventSearchCountry", required= false) String eventSearchCountry,
			  @RequestParam(name= "eventTypeIdSearch", required= false) List<Integer> eventTypeIdSearch,
			  @RequestParam(name = "liveDownNormalScroll", required= false) boolean liveDownNormalScroll,
			  @RequestParam(name= "eventGroupdbNameIdList") List<String> eventGroupdbNameIdList,
			  @RequestParam("startDateStr") String startDateStr,
			  @RequestParam("endDateStr") String endDateStr
			) {
				
		//Boş liste dönülüyor.Javascript listesinin içinde olay varsa boş liste dönüyor.
		if(liveDownNormalScroll != true) {
			SidebarAllWrapper sidebarAllWrapperNULL = new SidebarAllWrapper(null, null);
			
			return sidebarAllWrapperNULL;
		}
		
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group

			return null;
		}
	
		Integer page = 0;
		
		int pageLoadLimit = SettingsUtil.getInteger(SettingsE.PAGE_EVENT_COUNT_PER_LOAD);
		
		Date lastScrollDate = null;
		if(lastScrollDateLong != null) {
			lastScrollDate = new Date(lastScrollDateLong);
		}
		
		
		PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.desc("eventDate"), Order.desc("createDate"))); 
		
		
		Map<String, Integer> lastIdMap = new HashMap<>();
    	lastIdMap.put(Statics.DEFAULT_DB_NAME, null);
    	
    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
    		lastIdMap.put(dataSourceInfo.getName(), null);
    	}
			
    	Date startDate = DateUtils.convertToDate(startDateStr, DateUtils.TURKISH);
		Date endDate = DateUtils.convertToDate(endDateStr, DateUtils.TURKISH);
		
		List<SidebarEventItemWrapper> totalList = eventService.prepareEventItemWrapperList(pageRequest, startDate, endDate,  lastScrollDate, lastIdMap, layerItem.getId(), eventSearch, eventTypeIdSearch, null, null, null, null, eventGroupdbNameIdList, eventSearchCity, eventSearchCountry, false);
		
		/**************************************************************/
    	List<SidebarEventItemWrapper> totalListResult = totalList.stream().sorted(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getEventDate()).reversed()).limit(pageLoadLimit).collect(Collectors.toList());
    	
    	/**************************************************************/
    	
    	// Diğer veritabanlarından gelmeyen verilerin doldurulması işlemi
    	totalListResult = fillDataNotExistForOtherDbs(totalListResult);
		
    	/**************************************************************/
    		    	    	    	    	   	
    	SidebarAllWrapper sidebarAllWrapper = new SidebarAllWrapper(totalListResult, lastIdMap);
    	try {
    		lastScrollDateLong = totalListResult.get(totalListResult.size() - 1).getEvent().getEventDate().getTime();			
		} catch (Exception e) {	}
    	sidebarAllWrapper.setLastScrollDate(lastScrollDateLong);
    	
    	try {
    		// page refresh esnasında sonradan eklenmiş eski tarihli bir olayın idsinin büyük olmasından dolayı sayfa açıldıktan sonra refresh ile görünmesini önlemek için eklenmiştir
    		sidebarAllWrapper.setFirstScrollDate(DateUtils.now().getTime());
		} catch (Exception e) {	}   	


		return sidebarAllWrapper;
	}

	@RequestMapping(value = "/refresh", method = RequestMethod.POST)
	@ResponseBody
	public SidebarAllWrapper eventsRefresh( @RequestBody EventRequestItem eventRequestItem) {
		
//		if(
////				IPUtils.isIpContaining("177.177.177.47") 
////				||
//				IPUtils.isIpContaining("177.177.16.17")
//				) {
//			return null;
//		}
		
		Date startDate = DateUtils.convertToDate(eventRequestItem.getStartDateStr(), DateUtils.TURKISH);
		Date endDate = DateUtils.convertToDate(eventRequestItem.getEndDateStr(), DateUtils.TURKISH);
		Date lastScrollDate = null;
		Date firstScrollDate = null;
		if(eventRequestItem.getFirstScrollDate() != null) {
			try {
				
				firstScrollDate = new Date(Long.parseLong(eventRequestItem.getFirstScrollDate()));
			} catch (Exception e) {
			}
		}
		int page = 0;

		
		if(eventRequestItem.getLastEventIdMap() == null) {
			return null;
		}
		
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(eventRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		List<String> eventGroupdbNameIdList = eventRequestItem.getEventGroupdbNameIdList();

		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group

			return null;
		}
		
		int pageLoadLimit = SettingsUtil.getInteger(SettingsE.PAGE_EVENT_COUNT_PER_LOAD);
		
		PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(/*Order.asc("eventDate"),*/ Order.asc("id")));
		
		
		Map<String, Integer> lastIdMap = eventRequestItem.getLastEventIdMap();
		
		List<AlertState> alertStateList = alertService.findAllAlertState();
		Map<String, Integer> alertLastIdMap = alertStateList.stream().collect(Collectors.toMap(AlertState::getDbName, AlertState::getLastId));
		
		List<SidebarEventItemWrapper> totalList = eventService.prepareEventItemWrapperList(pageRequest, startDate, endDate, lastScrollDate, lastIdMap, layerItem.getId(), alertLastIdMap, eventRequestItem.getEventSearchText(), firstScrollDate, eventRequestItem.getEventTypeIdSearch(), null, null, null, null, eventGroupdbNameIdList, eventRequestItem.getEventSearchCity(), eventRequestItem.getEventSearchCountry(), false);
		
		/**************************************************************/
    	
		List<SidebarEventItemWrapper> totalListResult = totalList.stream().sorted(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getId()).reversed()).collect(Collectors.toList());
    	
    	/**************************************************************/

    	// Diğer veritabanlarından gelmeyen verilerin doldurulması işlemi
    	totalListResult = fillDataNotExistForOtherDbs(totalListResult);
		
    	/**************************************************************/
    	
    	SidebarAllWrapper sidebarAllWrapper = new SidebarAllWrapper(totalListResult, lastIdMap);
   	
		return sidebarAllWrapper;
	}
	
	@RequestMapping(value = "/time", method = RequestMethod.GET)
	@ResponseBody
	public SidebarAllWrapper eventsTime(
			  @RequestParam(name="time") String time,										  
			  @RequestParam(name="lastScrollDate", required = false) Long lastScrollDateLong,
			  @RequestParam("layerId") String currentLayerGuid,
			  @RequestParam(name= "eventSearch", required= false) String eventSearch,
			  @RequestParam(name= "eventSearchCity", required= false) String eventSearchCity,
			  @RequestParam(name= "eventSearchCountry", required= false) String eventSearchCountry,
			  @RequestParam(name= "eventTypeIdSearch", required= false) List<Integer> eventTypeIdSearch,
			  @RequestParam(name= "eventGroupdbNameIdList") List<String> eventGroupdbNameIdList
			) {

		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
		
		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group

			return null;
		}
		
		
		Integer page = 0;
		int pageLoadLimit = SettingsUtil.getInteger(SettingsE.PAGE_EVENT_COUNT_PER_LOAD);
		
		Date startDate = DateUtils.convertToDate(time, DateUtils.TURKISH_DATE);
		
		// Sidebardaki son olaydan sonraki olayları getirebilmek için kullanılır. startDate'e eşitlenerek standart akış sağlanır. page herzaman sıfırdır.
		Date lastScrollDate = null;
		if(lastScrollDateLong != null) {
			startDate = new Date(lastScrollDateLong);
		}
		
		PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.asc("eventDate"), Order.asc("createDate")));
		
		
		Map<String, Integer> lastIdMap = new HashMap<>();
    	lastIdMap.put(Statics.DEFAULT_DB_NAME, null);
    	
    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
    		lastIdMap.put(dataSourceInfo.getName(), null);
    	}

    	
		List<SidebarEventItemWrapper> totalList = eventService.prepareEventItemWrapperList(pageRequest, startDate, null, lastScrollDate, lastIdMap, layerItem.getId(), eventSearch, eventTypeIdSearch, null, null, null, null, eventGroupdbNameIdList, eventSearchCity, eventSearchCountry, false);
		
		/**************************************************************/
		
    	List<SidebarEventItemWrapper> totalListResult = totalList.stream().sorted(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getEventDate())).limit(pageLoadLimit).collect(Collectors.toList());
    	
    	/**************************************************************/

    	// Diğer veritabanlarından gelmeyen verilerin doldurulması işlemi
    	totalListResult = fillDataNotExistForOtherDbs(totalListResult);
    	
    	/**************************************************************/
    	
    	   	
    	SidebarAllWrapper sidebarAllWrapper = new SidebarAllWrapper(totalListResult, lastIdMap);
    	try {
    		lastScrollDateLong = totalListResult.get(totalListResult.size() - 1).getEvent().getEventDate().getTime();		
		} catch (Exception e) {	}
    	sidebarAllWrapper.setLastScrollDate(lastScrollDateLong);
    	
		return sidebarAllWrapper;
	}
	
	@RequestMapping(value = "/timeDimensionPlayback", method = RequestMethod.GET)
	@ResponseBody
	public String getAllEvent(
			@RequestParam("layerId") String currentLayerGuid,
			@RequestParam("startDateStr") String startDateStr,
			@RequestParam("endDateStr") String endDateStr,
			@RequestParam(name= "eventSearch", required= false) String eventSearch,
			@RequestParam(name= "eventSearchCity", required= false) String eventSearchCity,
			@RequestParam(name= "eventSearchCountry", required= false) String eventSearchCountry,
			@RequestParam(name= "eventTypeIdSearch", required= false) List<Integer> eventTypeIdSearch,
			@RequestParam(name= "eventGroupdbNameIdList") List<String> eventGroupdbNameIdList) throws JsonProcessingException{
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {			
			return null;
		}
		
		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group

			return null;
		}
		
		Map<String, Integer> lastIdMap = new HashMap<>();
    	lastIdMap.put(Statics.DEFAULT_DB_NAME, null);
    	
    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
    		lastIdMap.put(dataSourceInfo.getName(), null);
    	}
    	
    	int page = 0;
    	int pageLoadLimit = 10000;
    	PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.asc("id")/* Order.desc("eventDate"), Order.desc("createDate")*/)); 

    	//Date startDate = null;
		Date lastScrollDate = null;
		List<SidebarEventItemWrapper> totalList = new ArrayList<>();
		
		Date startDate = DateUtils.convertToDate(startDateStr, DateUtils.TURKISH);
		Date endDate = DateUtils.convertToDate(endDateStr, DateUtils.TURKISH);
		
		
		boolean keepMakingList = true;
		
		while(keepMakingList) {
			
			List<SidebarEventItemWrapper> list = eventService.prepareEventItemWrapperList(pageRequest, startDate, endDate, lastScrollDate, lastIdMap, layerItem.getId(), eventSearch, eventTypeIdSearch, null, null, null, null, eventGroupdbNameIdList, eventSearchCity, eventSearchCountry, false);
			Collections.addAll(totalList, fillDataNotExistForOtherDbs(list).toArray(new SidebarEventItemWrapper[0]));
			
			//list = fillDataNotExistForOtherDbs(list);
			//totalList.addAll(list);
			keepMakingList = list.size() > 0 ? true : false;
		}
		
		
		
		totalList = totalList.stream().sorted(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getEventDate()).reversed()).collect(Collectors.toList());
		SidebarAllWrapper sidebarAllWrapper = new SidebarAllWrapper(totalList, null);
		
		return new ObjectMapper().writeValueAsString(sidebarAllWrapper);
		
	}
	
	@RequestMapping(value = "/heatmapForFirstLoading", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String getAllEventHeatmapForFirstLoading( @RequestParam("layerId") String currentLayerGuid,
			@RequestParam("startDateStr") String startDateStr,
			@RequestParam("endDateStr") String endDateStr,
			@RequestParam(name= "eventSearch", required= false) String eventSearch,
			@RequestParam(name= "eventTypeIdSearch", required= false) List<Integer> eventTypeIdSearch,
			@RequestParam(name= "eventSearchCity", required= false) String eventSearchCity,
			@RequestParam(name= "eventSearchCountry", required= false) String eventSearchCountry,
			@RequestParam(name= "eventGroupdbNameIdList") List<String> eventGroupdbNameIdList) throws JsonProcessingException {
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {			
			return null;
		}
		
		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group

			return null;
		}
		
		Map<String, Integer> lastIdMap = new HashMap<>();
    	lastIdMap.put(Statics.DEFAULT_DB_NAME, null);
    	
    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
    		lastIdMap.put(dataSourceInfo.getName(), null);
    	}
    	
    	int page = 0;
    	int pageLoadLimit = 10000;
    	PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.asc("id")/* Order.desc("eventDate"), Order.desc("createDate")*/)); 

    	//Date startDate = null;
		Date lastScrollDate = null;
		List<SidebarEventItemWrapperForHeatMap> totalList = new ArrayList<>();
		
		Date startDate = DateUtils.convertToDate(startDateStr, DateUtils.TURKISH);
		Date endDate = DateUtils.convertToDate(endDateStr, DateUtils.TURKISH);
		
		
		boolean keepMakingList = true;

		while(keepMakingList) {
			// lastidmap ile tablonun tamamı sorgulanır
			List<SidebarEventItemWrapperForHeatMap> list = eventService.prepareEventItemWrapperListForHeatMap(pageRequest, startDate, endDate, lastScrollDate, lastIdMap, layerItem.getId(), eventSearch, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventGroupdbNameIdList);
			Collections.addAll(totalList, fillDataNotExistForOtherDbsForHeatMap(list).toArray(new SidebarEventItemWrapperForHeatMap[0]));
			keepMakingList = list.size() > 0;
		}

		totalList = totalList.stream().sorted(Comparator.comparing(item-> ((SidebarEventItemWrapperForHeatMap) item).getEvent().getEventDate()).reversed()).collect(Collectors.toList());
		SidebarAllWrapperForHeatMap sidebarAllWrapper = new SidebarAllWrapperForHeatMap(totalList, null);

		return new ObjectMapper().writeValueAsString(sidebarAllWrapper);
	}
	
	
	@RequestMapping(value = "/heatmap", method = RequestMethod.GET)
	@ResponseBody
	public SidebarAllWrapper getAllEventHeatmap( @RequestParam("layerId") String currentLayerGuid,
			@RequestParam("startDateStr") String startDateStr,
			@RequestParam("endDateStr") String endDateStr,
			@RequestParam(name= "eventSearch", required= false) String eventSearch,
			@RequestParam(name= "eventTypeIdSearch", required= false) List<Integer> eventTypeIdSearch,
			@RequestParam(name="southWestLng") Double southWestLng, @RequestParam(name="southWestLat") Double southWestLat,
			@RequestParam(name="northEastLng") Double northEastLng, @RequestParam(name="northEastLat") Double northEastLat,
			@RequestParam(name="eventGroupdbNameIdList") List<String> eventGroupdbNameIdList,
			@RequestParam(name= "eventSearchCity", required= false) String eventSearchCity,
			@RequestParam(name= "eventSearchCountry", required= false) String eventSearchCountry){
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {			
			return null;
		}
		
		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group

			return null;
		}
		
		Map<String, Integer> lastIdMap = new HashMap<>();
    	lastIdMap.put(Statics.DEFAULT_DB_NAME, null);
    	
    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
    		lastIdMap.put(dataSourceInfo.getName(), null);
    	}
    	
    	int page = 0;
    	int pageLoadLimit = 500;
    	PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.asc("id")/* Order.desc("eventDate"), Order.desc("createDate")*/)); 

    	//Date startDate = null;
		Date lastScrollDate = null;
		List<SidebarEventItemWrapper> totalList = new ArrayList<>();
		
		Date startDate = DateUtils.convertToDate(startDateStr, DateUtils.TURKISH);
		Date endDate = DateUtils.convertToDate(endDateStr, DateUtils.TURKISH);
		
		
		List<SidebarEventItemWrapper> list = eventService.prepareEventItemWrapperList(pageRequest, startDate, endDate, lastScrollDate, lastIdMap, layerItem.getId(), eventSearch, eventTypeIdSearch, southWestLng, southWestLat, northEastLng, northEastLat, eventGroupdbNameIdList, eventSearchCity, eventSearchCountry, false);
		list = fillDataNotExistForOtherDbs(list);
		totalList.addAll(list);
		
		totalList = totalList.stream().sorted(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getEventDate()).reversed()).limit(500).collect(Collectors.toList());
		SidebarAllWrapper sidebarAllWrapper = new SidebarAllWrapper(totalList, null);
		
		return sidebarAllWrapper;
	}
	
	

	@RequestMapping(value = "/searchEventType", method = RequestMethod.POST)
	@ResponseBody
	public List<EventTypeItem> searchEventType(@RequestBody EventRequestItem eventRequestItem) {


		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(eventRequestItem.getLayerId());
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}

		List<EventTypeItem> eventTypeItemList = eventService.prepareEventTypeWrapperList(layerItem.getId());

		String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);

		eventTypeItemList.forEach(item->{

			String name = ApplicationContextUtils.getMessage("icons." + item.getCode(), locale);
			name = name.equals("icons." + item.getCode()) ? item.getName() : name;
			item.setName(name);

		});
		
		Collator collator = Collator.getInstance(locale);
		collator.setStrength(Collator.PRIMARY);
		List<EventTypeItem> sortedList = eventTypeItemList.stream()
                .sorted(Comparator.comparing(EventTypeItem::getName, collator))
                .collect(Collectors.toList());
		
		return sortedList;

	}



	// Diğer veritabanlarından gelmeyen verilerin doldurulması işlemi
	private List<SidebarEventItemWrapper> fillDataNotExistForOtherDbs(List<SidebarEventItemWrapper> totalListResult) {
		
    	// Eventtype
    	List<Integer> sidebarEventTypeIdList = totalListResult.stream().map(item->((SidebarEventItemWrapper) item).getEvent().getEventTypeId()).collect(Collectors.toList());
    	List<EventTypeItem> imageList = eventTypeService.findAll(Sort.by(Direction.ASC, "id"), sidebarEventTypeIdList);        	
    	Map<Integer, EventTypeItem> eventTypeIdMap = imageList.stream().collect(Collectors.toMap(EventTypeItem::getId, Function.identity()));

    	// User
    	UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());		
    	
		// alert
		List<String> eventDbNameAndIdList  = totalListResult.stream().map(item-> item.getEventIdDbName()).collect(Collectors.toList());		
    	List<SidebarAlertEventItem> alertEventItemList = alertEventService.findAllProjectedBySidebar(Sort.by(Direction.ASC, "id"), eventDbNameAndIdList, sessionUser.getUserId());    	   	
    	Map<String, List<SidebarAlertEventItem>> alertEventIdMap = alertEventItemList.stream().collect( Collectors.groupingBy(SidebarAlertEventItem::getEventIdDbName));   	

    	for(SidebarEventItemWrapper sidebarEventItemWrapper : totalListResult) {
    		sidebarEventItemWrapper.getEvent().setEventTypeImage(eventTypeIdMap.get(sidebarEventItemWrapper.getEvent().getEventTypeId()).getImage());
    		sidebarEventItemWrapper.getEvent().setEventTypeName(eventTypeIdMap.get(sidebarEventItemWrapper.getEvent().getEventTypeId()).getName());
    		sidebarEventItemWrapper.setAlertList(alertEventIdMap.get(sidebarEventItemWrapper.getEventIdDbName()));
    		
    		boolean state = sidebarEventItemWrapper.getEvent().getStateId() != null && sidebarEventItemWrapper.getEvent().getStateId().equals(StateE.TRUE.getValue()) ?  true : false;
    		sidebarEventItemWrapper.getEvent().setState(state);
    	}
    	
    	return totalListResult;
	}
	
	
	private List<SidebarEventItemWrapperForHeatMap> fillDataNotExistForOtherDbsForHeatMap(List<SidebarEventItemWrapperForHeatMap> totalListResult) {
		
    	// User
    	UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());		
    	
		// alert
		List<String> eventDbNameAndIdList  = totalListResult.stream().map(item-> item.getEventIdDbName()).collect(Collectors.toList());		
    	List<SidebarAlertEventItem> alertEventItemList = alertEventService.findAllProjectedBySidebar(Sort.by(Direction.ASC, "id"), eventDbNameAndIdList, sessionUser.getUserId());    	   	
    	Map<String, List<SidebarAlertEventItem>> alertEventIdMap = alertEventItemList.stream().collect( Collectors.groupingBy(SidebarAlertEventItem::getEventIdDbName));   	

    	for(SidebarEventItemWrapperForHeatMap sidebarEventItemWrapperForHeatMap : totalListResult) {
    		sidebarEventItemWrapperForHeatMap.setAlertList(alertEventIdMap.get(sidebarEventItemWrapperForHeatMap.getEventIdDbName()));
    	}
    	
    	return totalListResult;
	}
	
	
	//Timedimension infinite-scroll için eklendi. Başka bir yerde kullanılmıyor.
	@RequestMapping(value = "/timeDimensionScroll", method = RequestMethod.GET)
	@ResponseBody
	public String timeDimensionScroll() {		
		return "";
	}
		
}
