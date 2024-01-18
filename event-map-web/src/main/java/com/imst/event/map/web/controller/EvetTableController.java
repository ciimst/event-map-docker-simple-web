package com.imst.event.map.web.controller;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.constant.SettingsE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.constant.UserSettingsTypeE;
import com.imst.event.map.web.datatables.ajax.ColumnDef;
import com.imst.event.map.web.datatables.ajax.DataSet;
import com.imst.event.map.web.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.web.datatables.ajax.DatatablesResponse;
import com.imst.event.map.web.datatables.util.StringUtils;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.services.AlertEventService;
import com.imst.event.map.web.services.EventExcelService;
import com.imst.event.map.web.services.EventService;
import com.imst.event.map.web.services.EventTypeService;
import com.imst.event.map.web.services.SettingsService;
import com.imst.event.map.web.services.UserLayerPermissionService;
import com.imst.event.map.web.services.UserSettingsService;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.utils.SettingsUtil;
import com.imst.event.map.web.utils.UserSettingsUtil;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.EventExcelItem;
import com.imst.event.map.web.vo.EventTypeItem;
import com.imst.event.map.web.vo.LayerSimpleItem;
import com.imst.event.map.web.vo.SidebarAlertEventItem;
import com.imst.event.map.web.vo.SidebarEventItem;
import com.imst.event.map.web.vo.SidebarEventItemWrapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class EvetTableController {
    
	@Autowired private EventService eventService;
    @Autowired private EventTypeService eventTypeService;
    @Autowired private UserLayerPermissionService userLayerPermissionService;
    @Autowired private AlertEventService alertEventService;
    
	@Autowired private SettingsService settingsService;
	@Autowired private UserSettingsService userSettingsService;
	@Autowired private EventExcelService eventExcelService;
	

    @RequestMapping(value = "/event-table/{layerId}", method = RequestMethod.POST)
    @ResponseBody
    public DatatablesResponse<SidebarEventItemWrapper> eventTableData(@RequestBody DatatablesCriterias criteria,
    		@PathVariable(value = "layerId") String currentLayerGuid) {


		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
    	
		
		Date startDate = null;
		Date endDate = null;
		
//		Integer lastId = null;
//		List<Integer> userIdList = Arrays.asList(new Integer[]{0});
//		List<Integer> groupIdList = Arrays.asList(new Integer[]{0});
		 
		int currentLayerId = layerItem.getId();
		Date lastScrollDate = null;
//		Integer alertLastId = null;
		String eventSearch = null;
		Date firstScrollDate = null;
		List<Integer> eventTypeIdSearch = null;
//		Double southWestLng = null;
//		Double southWestLat = null;
//
//		Double northEastLng = null;
//		Double northEastLat = null;
//		List<Integer> eventGroupIdList = new ArrayList<>();
		List<String> eventGroupdbNameIdList = null;
		String eventSearchCity = null;
		String eventSearchCountry = null;
		Boolean isAlertEvent = false;
		
		List<ColumnDef> columns = criteria.getColumns();
		for (ColumnDef columnDef : columns) {
			
			if (columnDef.getData() == null) {
				continue;
			}
			
			if (StringUtils.isBlank(columnDef.getSearch().getValue())) {
				continue;
			}
			
			switch (columnDef.getName()) {
			case "title":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					eventSearch = columnDef.getSearch().getValue(); 
				}
				break;
			case "city":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					eventSearchCity = columnDef.getSearch().getValue(); 
				}
				break;
			case "country":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					eventSearchCountry = columnDef.getSearch().getValue(); 
				}
				break;
			case "eventTypeId":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						eventTypeIdSearch = new ArrayList<>();
						String[] eventTypeIdSearchString = columnDef.getSearch().getValue().split(",");
						for (String item : eventTypeIdSearchString) {
							Integer itemInt = Integer.parseInt(item);
							eventTypeIdSearch.add(itemInt);
						}
					} catch (Exception e) {
					}
				}
				break;
			case "eventGroupIdList":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						final ObjectMapper objectMapper = new ObjectMapper();
						String[] eventGroupDbNameWithIdArray = objectMapper.readValue(columnDef.getSearch().getValue(), String[].class);
						eventGroupdbNameIdList = Arrays.asList(eventGroupDbNameWithIdArray);
					} catch (Exception e) {
					}
				}
				break;
				
			case "alert"://alarmlı olay filtresinde kullanılıyor
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						isAlertEvent = Boolean.parseBoolean(columnDef.getSearch().getValue()); 
					} catch (Exception e) {
					}
				}
				break;
				
			case "startDateStr"://startdate ve enddate aramasında kullanılacak.
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						startDate = DateUtils.convertToDate(columnDef.getSearch().getValue(), DateUtils.TURKISH);  
					} catch (Exception e) {
					}
				}
				break;
			case "endDateStr"://startdate ve enddate aramasında kullanılacak.
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						endDate = DateUtils.convertToDate(columnDef.getSearch().getValue(), DateUtils.TURKISH);  
					} catch (Exception e) {
					}
				}
				break;

			default:
				break;
			}
		}

		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group
			
			DataSet<SidebarEventItemWrapper> dataSet1 = new DataSet<>(new ArrayList<SidebarEventItemWrapper>(), 0L, 0L);
	    	DatatablesResponse<SidebarEventItemWrapper> datatablesResponseItem = DatatablesResponse.build(dataSet1, criteria);
			
			return datatablesResponseItem;
		}
		
		int pageLoadLimit = SettingsUtil.getInteger(SettingsE.PAGE_EVENT_COUNT_PER_LOAD);
		int page = criteria.getStart() / pageLoadLimit;
		
		Integer lastEventIdForRefresh = null;
		
		Map<String, Integer> lastIdMap = new HashMap<>();
    	lastIdMap.put(Statics.DEFAULT_DB_NAME, null);

    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
    		lastIdMap.put(dataSourceInfo.getName(), null);
    	}
    	
    	PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.desc("eventDate"), Order.desc("id"))); 
		
		if(criteria.getSearch().getValue() != null && criteria.getRefresh() != null && criteria.getRefresh().equals("true")) {

			lastEventIdForRefresh = Integer.parseInt(criteria.getSearch().getValue());
			lastIdMap.put(Statics.DEFAULT_DB_NAME, lastEventIdForRefresh);
			
			firstScrollDate = new Date(criteria.getPageRefreshDate().getTime());
			pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.desc("id"))); 
		}
		

		List<SidebarEventItemWrapper> totalList = eventService.prepareEventItemWrapperList(pageRequest, startDate, endDate,  lastScrollDate, lastIdMap, currentLayerId, null, eventSearch, firstScrollDate, eventTypeIdSearch, null, null, null, null, eventGroupdbNameIdList, eventSearchCity, eventSearchCountry, isAlertEvent);
		Integer totalEventCount = eventService.totalCountEvents(layerItem.getId(), eventSearch, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventGroupdbNameIdList, isAlertEvent, startDate, endDate);
		
		/**************************************************************/
    	List<SidebarEventItemWrapper> totalListResult = totalList;
    	//totalListResult = totalList.stream().sorted(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getId())).limit(pageLoadLimit).collect(Collectors.toList());
    	
    	/**************************************************************/
    	
    	// Diğer veritabanlarından gelmeyen verilerin doldurulması işlemi
    	totalListResult = fillDataNotExistForOtherDbs(totalListResult);
		
    	/**************************************************************/
    	
		if(!(criteria.getRefresh() != null && criteria.getRefresh().equals("true"))) {//refresh değilse
			
			while(totalListResult.size() < pageLoadLimit) {
		    	SidebarEventItem sidebarEventItem = new SidebarEventItem(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,null,null);
		    	totalListResult.add(new SidebarEventItemWrapper(sidebarEventItem, null, null, null));
			}

		}
    	
    	DataSet<SidebarEventItemWrapper> dataSet = new DataSet<>(totalListResult, (long) pageLoadLimit, totalEventCount.longValue());
    	DatatablesResponse<SidebarEventItemWrapper> datatablesResponseItem = DatatablesResponse.build(dataSet, criteria);
    	

    	
        return datatablesResponseItem;
    }
    
    
	@RequestMapping(value = "/event-table/export", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> exportToExcelAndDownload(  
			  @RequestParam(name = "layerId") String currentLayerGuid,
			  @RequestParam(name= "eventSearch", required= false) String eventSearch,
			  @RequestParam(name= "eventTypeIdSearch", required= false) List<Integer> eventTypeIdSearch,
			  @RequestParam(name= "eventSearchCity", required= false) String eventSearchCity,
			  @RequestParam(name= "eventSearchCountry", required= false) String eventSearchCountry,
			  @RequestParam(name= "eventGroupdbNameIdList") List<String> eventGroupdbNameIdList,
			  @RequestParam(name= "startDateStr", required= false) String startDateStr,
			  @RequestParam(name= "endDateStr", required= false) String endDateStr,
			  @RequestParam(name= "isAlertEvent", required= false) Boolean isAlertEvent) {		
		
		ResponseEntity<?> responseEntity = null;
		UserItemDetails sessionUser = null;
		Workbook workbook = null;
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
			
		try {
			
			settingsService.updateSettingsCache();
				
			sessionUser = ApplicationContextUtils.getUser();		
			sessionUser.setExcelStateInformation("started");
				
			Integer currentLayerId = layerItem.getId();	
			
			
			Map<String, Integer> lastIdMap = new HashMap<>();
	    	lastIdMap.put(Statics.DEFAULT_DB_NAME, null);

	    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
	    		lastIdMap.put(dataSourceInfo.getName(), null);
	    	}
			List<EventExcelItem> totalList = new ArrayList<>();
			
			Integer page = 0;		
			
			UserSettingsUtil userSettingsUtil = userSettingsService.updateUserSettingsCacheAndGet();
				
			int pageLoadLimit=0;
			
			if(userSettingsUtil.hasSettings(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL) && userSettingsUtil.getInteger(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL) <= SettingsUtil.getInteger(SettingsE.MAX_COUNT_EVENTS_EXCEL)) {
			
				pageLoadLimit = userSettingsUtil.getInteger(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL);
			}
			else {
				
				pageLoadLimit = SettingsUtil.getInteger(SettingsE.MAX_COUNT_EVENTS_EXCEL);
			}
				
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.isNotBlank(startDateStr)) {
				try {
					startDate = DateUtils.convertToDate(startDateStr, DateUtils.TURKISH);  
				} catch (Exception e) {
				}
			}
			
			if (StringUtils.isNotBlank(endDateStr)) {
				try {
					endDate = DateUtils.convertToDate(endDateStr, DateUtils.TURKISH);  
				} catch (Exception e) {
				}
			}
			
			
			PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.desc("eventDate"), Order.desc("id"))); 	
			totalList = eventService.prepareEventExcelList(pageRequest, lastIdMap, currentLayerId, null, eventSearch, eventTypeIdSearch, eventGroupdbNameIdList, eventSearchCity, eventSearchCountry, startDate, endDate, isAlertEvent);
			

			workbook = eventExcelService.createExcel(totalList);
	   

		    ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    workbook.write(stream);
		    
		   		       
		    HttpHeaders headers = new HttpHeaders();
					
			headers.add("Content-Disposition", String.format("attachment; filename=Events.xlsx"));
					
			responseEntity = ResponseEntity.ok()
		      .headers(headers)
		      .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
		      .body(stream.toByteArray());
		
			return responseEntity;
			
		}catch (IOException e) {
			log.error(e);
		}finally {
			
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				log.error(e);
			}
			sessionUser.setExcelStateInformation("finished");
		}
		
		return responseEntity;
 	 }
    
	
	@RequestMapping(value = "/event-table/excelStateInformation", method = RequestMethod.GET)
	
	public String excelStateInformation() {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
	
		return sessionUser.getExcelStateInformation();
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
    	}
    	
    	return totalListResult;
	}
}
