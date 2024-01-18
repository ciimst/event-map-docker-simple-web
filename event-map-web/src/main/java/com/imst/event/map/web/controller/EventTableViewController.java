package com.imst.event.map.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.constant.SettingsE;
import com.imst.event.map.web.constant.StateE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.constant.UserSettingsTypeE;
import com.imst.event.map.web.datatables.ajax.ColumnDef;
import com.imst.event.map.web.datatables.ajax.DataSet;
import com.imst.event.map.web.datatables.ajax.DatatablesCriterias;
import com.imst.event.map.web.datatables.ajax.DatatablesResponse;
import com.imst.event.map.web.datatables.util.StringUtils;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.projections.EventGroupProjection;
import com.imst.event.map.web.db.repositories.EventGroupRepository;
import com.imst.event.map.web.db.repositories.EventRepository;
import com.imst.event.map.web.db.repositories.UserSettingsRepository;
import com.imst.event.map.web.db.specifications.BlackListCheckedSpecification;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.services.AlertEventService;
import com.imst.event.map.web.services.EventExcelService;
import com.imst.event.map.web.services.EventService;
import com.imst.event.map.web.services.EventTypeService;
import com.imst.event.map.web.services.SettingsService;
import com.imst.event.map.web.services.UserLayerPermissionService;
import com.imst.event.map.web.services.UserPermissionService;
import com.imst.event.map.web.services.UserSettingsService;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.utils.EventGroupTree;
import com.imst.event.map.web.utils.SettingsUtil;
import com.imst.event.map.web.utils.UserSettingsUtil;
import com.imst.event.map.web.vo.BlackListItem;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.EventExcelItem;
import com.imst.event.map.web.vo.EventGroupItem;
import com.imst.event.map.web.vo.EventTableViewExcelItem;
import com.imst.event.map.web.vo.EventTableViewItem;
import com.imst.event.map.web.vo.EventTableViewItemMultiselectEventType;
import com.imst.event.map.web.vo.EventTableViewItemWrapper;
import com.imst.event.map.web.vo.EventTypeItem;
import com.imst.event.map.web.vo.GenericResponseItem;
import com.imst.event.map.web.vo.LayerSimpleItem;
import com.imst.event.map.web.vo.PermissionWrapperItem;
import com.imst.event.map.web.vo.SidebarAlertEventItem;
import com.imst.event.map.web.vo.UserSettingsTypeItem;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class EventTableViewController {

	@Autowired private EventService eventService;
    @Autowired private EventTypeService eventTypeService;
    @Autowired private UserLayerPermissionService userLayerPermissionService;
    @Autowired private AlertEventService alertEventService;
    @Autowired private UserSettingsRepository userSettingsRepository;
	@Autowired private SettingsService settingsService;
	@Autowired private UserSettingsService userSettingsService;
	
	@Autowired private EventRepository eventRepository;
    
	@Autowired private EventExcelService eventExcelService;
	@Autowired
	private UserPermissionService userPermissionService;
	
	@Autowired private EventGroupRepository eventGroupRepository;
	
	@Autowired private MasterDao masterDao;
    
    @RequestMapping(value = "/event-table-view/{layerId}", method = RequestMethod.POST)
    @ResponseBody
    public DatatablesResponse<EventTableViewItemWrapper> eventTableData(@RequestBody DatatablesCriterias criteria,
    		@PathVariable(value = "layerId") String currentLayerGuid) {


    	
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
    	

		
		Date startDate = null;
		Date endDate = null;
		
		String reserved1 = null;
		String reserved2 = null;
		String reserved3 = null;
		String reserved4 = null;
		String reserved5 = null;
		Boolean isAlertEvent = false;
		 
		int currentLayerId = layerItem.getId();
		String title = null;
		String spot = null;
		String description = null;
		List<Integer> eventTypeIdSearch = null;
		Integer eventGroupId = null;

		List<String> eventGroupdbNameIdList = new ArrayList<>();
		String eventSearchCity = null;
		String eventSearchCountry = null;
		String eventSearchBlackListTag = null;
		
		Boolean state = null;
		
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
					title = columnDef.getSearch().getValue(); 
				}
				break;
			case "spot":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					spot = columnDef.getSearch().getValue(); 
				}
				break;
			case "description":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					description = columnDef.getSearch().getValue(); 
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
			case "blackListTag":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					eventSearchBlackListTag = columnDef.getSearch().getValue(); 
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
			case "eventGroupId":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						eventGroupId = Integer.parseInt(columnDef.getSearch().getValue()); 
//						final ObjectMapper objectMapper = new ObjectMapper();
//						String[] eventGroupDbNameWithIdArray = objectMapper.readValue(columnDef.getSearch().getValue(), String[].class);
						eventGroupdbNameIdList.add("default_" + eventGroupId.toString());
					} catch (Exception e) {
					}
				}
				break;
			case "state":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						state = Boolean.parseBoolean(columnDef.getSearch().getValue()); 
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
				
			case "alert":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					try {
						isAlertEvent = Boolean.parseBoolean(columnDef.getSearch().getValue()); 
					} catch (Exception e) {
					}
				}
				break;				
			case "reserved1":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					reserved1 = columnDef.getSearch().getValue(); 
				}
				break;
			case "reserved2":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					reserved2 = columnDef.getSearch().getValue(); 
				}
				break;
			case "reserved3":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					reserved3 = columnDef.getSearch().getValue(); 
				}
				break;
			case "reserved4":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					reserved4 = columnDef.getSearch().getValue(); 
				}
				break;
			case "reserved5":
				if (StringUtils.isNotBlank(columnDef.getSearch().getValue())) {
					reserved5 = columnDef.getSearch().getValue(); 
				}
				break;

			default:
				break;
			}
		}
		
		
		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group
			
			DataSet<EventTableViewItemWrapper> dataSet1 = new DataSet<>(new ArrayList<EventTableViewItemWrapper>(), 0L, 0L);
	    	DatatablesResponse<EventTableViewItemWrapper> datatablesResponseItem = DatatablesResponse.build(dataSet1, criteria);
			
			return datatablesResponseItem;
		}
		
		int pageLoadLimit = SettingsUtil.getInteger(SettingsE.PAGE_EVENT_COUNT_PER_LOAD);
		Map<String, Integer> lastIdMap = new HashMap<>();
    	lastIdMap.put(Statics.DEFAULT_DB_NAME, null);

    	for(DataSourceInfo dataSourceInfo : Statics.tenantDataSourceInfoMap.values()) {
    		lastIdMap.put(dataSourceInfo.getName(), null);
    	}
    	
    	PageRequest pageRequest = criteria.getPageRequest(EventTableViewItem.class);
    	
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		boolean hasEventStateViewRole = sessionUser.getAuthorities().stream().anyMatch(item -> item.getAuthority().equals("ROLE_EVENT_STATE_VIEW"));
    	
		List<EventTableViewItemWrapper> totalList = eventService.prepareEventItemWrapperListForEventTableView(pageRequest, startDate, endDate, currentLayerId,  title, spot, description , eventTypeIdSearch, eventGroupId, eventSearchCity, eventSearchCountry, eventSearchBlackListTag, state, hasEventStateViewRole, reserved1, reserved2, reserved3, reserved4, reserved5, isAlertEvent);
		Integer totalEventCount = eventService.totalCountEventTableView(layerItem.getId(), title, spot, description, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventSearchBlackListTag, eventGroupId, startDate, endDate, state, hasEventStateViewRole, reserved1, reserved2, reserved3, reserved4, reserved5, isAlertEvent);
		
		/**************************************************************/
    	List<EventTableViewItemWrapper> totalListResult = totalList;
    	//totalListResult = totalList.stream().sorted(Comparator.comparing(item-> ((SidebarEventItemWrapper) item).getEvent().getId())).limit(pageLoadLimit).collect(Collectors.toList());
    	
    	/**************************************************************/
    	
    	// Diğer veritabanlarından gelmeyen verilerin doldurulması işlemi
    	totalListResult = fillDataNotExistForOtherDbs(totalListResult);
		
    	/**************************************************************/
    	
		if(!(criteria.getRefresh() != null && criteria.getRefresh().equals("true"))) {//refresh değilse
			
			while(totalListResult.size() < pageLoadLimit) {
		    	EventTableViewItem sidebarEventItem = new EventTableViewItem(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,null,null,null, null, null, null,null,null, null,null,null);
		    	totalListResult.add(new EventTableViewItemWrapper(sidebarEventItem, null, null, null));
			}

		}
		
		String language = LocaleContextHolder.getLocale().getLanguage();
		Locale locale = new Locale(language);
		
		for (EventTableViewItemWrapper item : totalListResult) {
			item.getEvent().getEventTypeCode();
			String name = ApplicationContextUtils.getMessage("icons." + item.getEvent().getEventTypeCode(), locale);
			name = name.equals("icons." + item.getEvent().getEventTypeCode()) ? item.getEvent().getEventTypeName() : name;
			item.getEvent().setEventTypeName(name);
		}
		
    	
    	DataSet<EventTableViewItemWrapper> dataSet = new DataSet<>(totalListResult, (long) pageLoadLimit, totalEventCount.longValue());
    	DatatablesResponse<EventTableViewItemWrapper> datatablesResponseItem = DatatablesResponse.build(dataSet, criteria);
    	

    	
        return datatablesResponseItem;
    }
    
	// Diğer veritabanlarından gelmeyen verilerin doldurulması işlemi
	private List<EventTableViewItemWrapper> fillDataNotExistForOtherDbs(List<EventTableViewItemWrapper> totalListResult) {
		
    	// Eventtype
    	List<Integer> sidebarEventTypeIdList = totalListResult.stream().map(item->((EventTableViewItemWrapper) item).getEvent().getEventTypeId()).collect(Collectors.toList());
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

    	for(EventTableViewItemWrapper sidebarEventItemWrapper : totalListResult) {
    		sidebarEventItemWrapper.getEvent().setEventTypeImage(eventTypeIdMap.get(sidebarEventItemWrapper.getEvent().getEventTypeId()).getImage());
    		sidebarEventItemWrapper.getEvent().setEventTypeName(eventTypeIdMap.get(sidebarEventItemWrapper.getEvent().getEventTypeId()).getName());
    		sidebarEventItemWrapper.getEvent().setEventTypeCode(eventTypeIdMap.get(sidebarEventItemWrapper.getEvent().getEventTypeId()).getCode());
    		sidebarEventItemWrapper.setAlertList(alertEventIdMap.get(sidebarEventItemWrapper.getEventIdDbName()));
    	}
    	
    	return totalListResult;
	}
	
	
	
	
	@RequestMapping(value="/event-table-view/user-settings", method = RequestMethod.POST)
	public List<String> getData() {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());	

		
		List<UserSettingsTypeItem> userSettingsAllList = userSettingsRepository.findAllByUserAndUserSettingsTypeGroupName(user, UserSettingsTypeE.EVENT_TABLE_VIEW_COLUMN.getName());  
		
		List<String> columnList = new ArrayList<>();
		userSettingsAllList.forEach(item -> {
			
			
			switch(item.getSettingsKey()) {
			
				case "EventTableViewTitle":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("title");
					}
					
					break;
					
				case "EventTableViewSpot":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("spot");
					}
					
					break;
					
				case "EventTableViewDescription":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("description");
					}
					
					break;
				case "EventTableViewEventDate":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("eventDate");
					}
					
					break;
				case "EventTableViewEventGroup":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("eventGroup");
					}
					
					break;
				case "EventTableViewEventType":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("eventType");
					}
					
					break;
				case "EventTableViewCity":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("city");
					}
					
					break;
				case "EventTableViewCountry":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("country");
					}
					
					break;
				case "EventTableViewLatitudeAndLongitude":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("latitudeAndLongitude");
					}
					
					break;
				case "EventTableViewState":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("state");
					}
					
					break;
				case "EventTableViewReservedKey":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("reservedKey");
					}
					
					break;
				case "EventTableViewReservedType":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("reservedType");
					}
					
					break;
				case "EventTableViewReservedId":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("reservedId");
					}
					
					break;
				case "EventTableViewReservedLink":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("reservedLink");
					}
					
					break;
				case "EventTableViewBlackListTag":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("blackListTag");
					}
					
					break;
				case "EventTableViewReserved1":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("reserved1");
					}
					
					break;
				case "EventTableViewReserved2":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("reserved2");
					}
					
					break;
				case "EventTableViewReserved3":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("reserved3");
					}
					
					break;
				case "EventTableViewReserved4":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("reserved4");
					}
					
					break;
				case "EventTableViewReserved5":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("reserved5");
					}
					
					break;
				case "EventTableViewTag":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("tag");
					}
					
					break;
				case "EventTableViewMedia":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("media");
					}
					
					break;	
					
				case "EventTableViewAlertEvent":
					if(Boolean.parseBoolean(item.getSettingsValue())) {
						columnList.add("alertEvent");
					}
					
					break;
					
				default:
					break;
			}

			
		});
		
		if(userSettingsAllList.size() == 0) {
			List<String> allColumn = Arrays.asList("title", "spot", "description", "eventDate", "eventGroup","eventType", "city", "country", "latitudeAndLongitude", "state", "reservedKey", "reservedType", "reservedId", "reservedLink", "blackListTag",
					"reserved1", "reserved2", "reserved3", "reserved4", "reserved5", "tag", "media", "alertEvent");
			
			columnList.addAll(allColumn);
		}

		return columnList;
	}
    
	@PreAuthorize("hasRole('ROLE_EVENT_BATCH_OPERATIONS')")
	@RequestMapping(value = "/event-table-view/export/{layerId}/{eventTableViewItem}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> exportToExcelAndDownload(  
			  @PathVariable(name = "layerId") String currentLayerGuid,
			  @PathVariable("eventTableViewItem") String eventItemStr
			  ) {		
		
		ResponseEntity<?> responseEntity = null;
		UserItemDetails sessionUser = null;
		Workbook workbook = null;
		
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
			
		try {
			
			ObjectMapper objectMapper = new ObjectMapper();
			EventTableViewItemMultiselectEventType eventTableViewItem = objectMapper.readValue(eventItemStr, EventTableViewItemMultiselectEventType.class);
			
			settingsService.updateSettingsCache();
				
			sessionUser = ApplicationContextUtils.getUser();	
				
			Integer currentLayerId = layerItem.getId();	
			

	    	List<EventExcelItem> totalList = new ArrayList<>();
			
			Date startDate = null;
			Date endDate = null;
			
			if (StringUtils.isNotBlank(eventTableViewItem.getStartDateStr())) {
				try {
					startDate = DateUtils.convertToDate(eventTableViewItem.getStartDateStr(), DateUtils.TURKISH);  
				} catch (Exception e) {
				}
			}
			
			if (StringUtils.isNotBlank(eventTableViewItem.getEndDateStr())) {
				try {
					endDate = DateUtils.convertToDate(eventTableViewItem.getEndDateStr(), DateUtils.TURKISH);  
				} catch (Exception e) {
				}
			}
			
			Integer page = 0;	
			
			UserSettingsUtil userSettingsUtil = userSettingsService.updateUserSettingsCacheAndGet();
			
			int pageLoadLimit=0;
			
			if(userSettingsUtil.hasSettings(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL) && userSettingsUtil.getInteger(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL) <= SettingsUtil.getInteger(SettingsE.MAX_COUNT_EVENTS_EXCEL)) {
			
				pageLoadLimit = userSettingsUtil.getInteger(UserSettingsTypeE.MAX_COUNT_EVENTS_EXCEL);
			}
			else {
				
				pageLoadLimit = SettingsUtil.getInteger(SettingsE.MAX_COUNT_EVENTS_EXCEL);
			}
			
			PageRequest pageRequest = PageRequest.of(page, pageLoadLimit, Sort.by(Order.desc("eventDate"), Order.desc("id"))); 	
						
			boolean hasEventStateViewRole = sessionUser.getAuthorities().stream().anyMatch(item -> item.getAuthority().equals("ROLE_EVENT_STATE_VIEW"));
			
			totalList = eventService.prepareEventTableViewExcelList(pageRequest, startDate, endDate, currentLayerId, eventTableViewItem, hasEventStateViewRole);
			
			
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
    
	
	@RequestMapping(value = "/event-table-view/excelStateInformation", method = RequestMethod.GET)
	
	public String excelStateInformation() {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
	
		return sessionUser.getExcelStateInformation();
	}
	
	
	@PreAuthorize("hasRole('ROLE_EVENT_BATCH_OPERATIONS')")
	@RequestMapping(value = "event-table-view/stateChange", method = RequestMethod.POST)
	public GenericResponseItem stateChange(Integer id) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		if(Optional.ofNullable(id).orElse(0) < 1) {
			genericResponseItem.setState(false);
			return genericResponseItem;
		}
		
		
		Event event = eventRepository.findByIdAndStateIdIn(id, Arrays.asList(StateE.TRUE.getValue(), StateE.FALSE.getValue()));
		
		if(event == null) {
			genericResponseItem.setState(false);
			return genericResponseItem;
		}
		
		
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		if (!userEventGroupPermissionIdList.contains(event.getEventGroup().getId())) {
			genericResponseItem.setState(false);
			genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.no.permission"));
			return genericResponseItem;
		}
		
		if (event.getBlackListTag() != null) {
			
			Layer tempLayer = new Layer();
			tempLayer.setId(event.getEventGroup().getLayer().getId());
			
			List<EventGroupProjection> allEventGroupProjectionList = eventGroupRepository.findAllProjectedByLayerOrderByName(tempLayer);
			
			List<EventGroupItem> allEventGroupList = new ArrayList<>();
			
			allEventGroupProjectionList.forEach(item-> {
				
				EventGroupItem eventGroupItem = new EventGroupItem(item.getId(), item.getName(), item.getColor(), item.getDescription(), item.getParentId(), item.getLayerId(), "");
				
				allEventGroupList.add(eventGroupItem);
			});
			
			EventGroupTree eventGroupTree = new EventGroupTree(null, allEventGroupList);
			List<Integer> permissionEventGroupIdParentList = eventGroupTree.getPermissionEventGroupParent(Arrays.asList(event.getEventGroup().getId()));
			
			BlackListItem blackListItem = new BlackListItem();
	    	blackListItem.setTag(event.getBlackListTag() != null ? event.getBlackListTag().trim() : null); 
	    	blackListItem.setLayerId(event.getEventGroup().getLayer().getId());  
			blackListItem.setEventGroupId(event.getEventGroup().getId()); 
			blackListItem.setEventTypeId(event.getEventType().getId()); 
			
			BlackListCheckedSpecification blackListCheckedSpecification = new BlackListCheckedSpecification(blackListItem, permissionEventGroupIdParentList);
			List<BlackListItem> blackLists = masterDao.findAll(blackListCheckedSpecification, Sort.by(Direction.DESC, "id"));
			
			if(!blackLists.isEmpty()) {
				
				BlackListItem blackListItemDb = blackLists.get(0);
				
				log.warn(event.getTitle() + " olayı; Black List'e ait Id : " + blackListItemDb.getId() + ", Name : " + blackListItemDb.getName() + ", Etiket : " + blackListItemDb.getTag() + ", Katman Id : " + blackListItemDb.getLayerId() + ", Katman Adı : " + blackListItemDb.getLayerName() + ", Olay Grubu Id : " + blackListItemDb.getEventGroupId() + ", Olay Türü Id : " + blackListItemDb.getEventTypeId() + " bilgilerini içerdiği için ekleme yapılamamaktadır.");
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.editing.cannot.be.done.because.event.content.contains.fields.belonging.blacklist")); 

				return genericResponseItem;
			}
		}
		
		
		event.setState(StateE.getToggleStateChange(event.getState().getId()));
		eventRepository.save(event);
		
		genericResponseItem.setState(true);
		return genericResponseItem;
	}
	
	@PreAuthorize("hasRole('ROLE_EVENT_BATCH_OPERATIONS')")
	@RequestMapping(value = "event-table-view/batchOperations", method = RequestMethod.POST)
	@ResponseBody
	public GenericResponseItem eventStateChangeBatchOperations(@RequestParam(name = "layerId") String currentLayerGuid, @RequestParam(name = "batchState") Boolean bacthState, 
			@RequestBody EventTableViewItemMultiselectEventType eventTableViewItem ) {
		
		GenericResponseItem genericResponseItem = new GenericResponseItem(true, ApplicationContextUtils.getMessage("label.success.save"));
				
		LayerSimpleItem layerItem = userLayerPermissionService.checkLayerPermissionAndGet(currentLayerGuid);
		if(layerItem == null) {
			// there is no permission to see events under this region
			return null;
		}
    	
		
		List<String> eventGroupdbNameIdList = new ArrayList<>();
		if(eventTableViewItem.getEventGroupId() != null) {
			eventGroupdbNameIdList.add("default_" + eventTableViewItem.getEventGroupId().toString());
		}
		
		
		if(!userLayerPermissionService.checkEventGroupPermission(layerItem, eventGroupdbNameIdList)) {
			// there is no permission to see events under this event group

			genericResponseItem.setState(false);
			return genericResponseItem;
		}
		
		Date startDate = null;
		Date endDate = null;
		
		if (StringUtils.isNotBlank(eventTableViewItem.getStartDateStr())) {
			try {
				startDate = DateUtils.convertToDate(eventTableViewItem.getStartDateStr(), DateUtils.TURKISH);  
			} catch (Exception e) {
			}
		}
		
		if (StringUtils.isNotBlank(eventTableViewItem.getEndDateStr())) {
			try {
				endDate = DateUtils.convertToDate(eventTableViewItem.getEndDateStr(), DateUtils.TURKISH);  
			} catch (Exception e) {
			}
		}
		
		 
		int currentLayerId = layerItem.getId();
		
		
		int page = 0;
		PageRequest pageRequest = PageRequest.of(page , Integer.MAX_VALUE, Sort.by(Order.desc("eventDate"), Order.desc("id"))); 
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		User user = new User();
		user.setId(sessionUser.getUserId());
		
		boolean hasEventStateViewRole = sessionUser.getAuthorities().stream().anyMatch(item -> item.getAuthority().equals("ROLE_EVENT_STATE_VIEW"));
		
		List<EventTableViewItemWrapper> totalList = eventService.prepareEventItemWrapperListForEventTableView(pageRequest, startDate, endDate, currentLayerId,  eventTableViewItem.getTitle(), eventTableViewItem.getSpot(), eventTableViewItem.getDescription() , eventTableViewItem.getEventTypeId(), eventTableViewItem.getEventGroupId(), eventTableViewItem.getCity(), eventTableViewItem.getCountry(), eventTableViewItem.getBlackListTag(), eventTableViewItem.getState(), hasEventStateViewRole, eventTableViewItem.getReserved1(), eventTableViewItem.getReserved2(), eventTableViewItem.getReserved3(), eventTableViewItem.getReserved4(), eventTableViewItem.getReserved5(), eventTableViewItem.getIsAlertEvent());

		
		PermissionWrapperItem permissionWrapperItem = userPermissionService.findUserPermissions(user);
		
		List<Integer> userEventGroupPermissionIdList = permissionWrapperItem.getUserEventGroupPermissionItemIds();
		
		for(EventTableViewItemWrapper event : totalList) {
			
			
			if (bacthState &&  !userEventGroupPermissionIdList.contains(event.getEvent().getEventGroupId())) {
				genericResponseItem.setState(false);
				genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.event.no.permission"));
				return genericResponseItem;
			}
			
			if (event.getEvent().getBlackListTag() != null) {
				
				Layer tempLayer = new Layer();
				tempLayer.setId(event.getEvent().getLayerId());
				
				List<EventGroupProjection> allEventGroupProjectionList = eventGroupRepository.findAllProjectedByLayerOrderByName(tempLayer);
				
				List<EventGroupItem> allEventGroupList = new ArrayList<>();
				
				allEventGroupProjectionList.forEach(item-> {
					
					EventGroupItem eventGroupItem = new EventGroupItem(item.getId(), item.getName(), item.getColor(), item.getDescription(), item.getParentId(), item.getLayerId(), "");
					
					allEventGroupList.add(eventGroupItem);
				});
				
				EventGroupTree eventGroupTree = new EventGroupTree(null, allEventGroupList);
				List<Integer> permissionEventGroupIdParentList = eventGroupTree.getPermissionEventGroupParent(Arrays.asList(event.getEvent().getEventGroupId()));
				
				BlackListItem blackListItem = new BlackListItem();
		    	blackListItem.setTag(event.getEvent().getBlackListTag() != null ? event.getEvent().getBlackListTag().trim() : null); 
		    	blackListItem.setLayerId(event.getEvent().getLayerId());  
				blackListItem.setEventGroupId(event.getEvent().getEventGroupId()); 
				blackListItem.setEventTypeId(event.getEvent().getEventTypeId()); 
				
				BlackListCheckedSpecification blackListCheckedSpecification = new BlackListCheckedSpecification(blackListItem, permissionEventGroupIdParentList);
				List<BlackListItem> blackLists = masterDao.findAll(blackListCheckedSpecification, Sort.by(Direction.DESC, "id"));
				
				if(!blackLists.isEmpty()) {
					
					BlackListItem blackListItemDb = blackLists.get(0);
					
					log.warn(event.getEvent().getTitle() + " olayı; Black List'e ait Id : " + blackListItemDb.getId() + ", Name : " + blackListItemDb.getName() + ", Etiket : " + blackListItemDb.getTag() + ", Katman Id : " + blackListItemDb.getLayerId() + ", Katman Adı : " + blackListItemDb.getLayerName() + ", Olay Grubu Id : " + blackListItemDb.getEventGroupId() + ", Olay Türü Id : " + blackListItemDb.getEventTypeId() + " bilgilerini içerdiği için ekleme yapılamamaktadır.");
					genericResponseItem.setState(false);
					genericResponseItem.setDescription(ApplicationContextUtils.getMessage("label.editing.cannot.be.done.because.event.content.contains.fields.belonging.blacklist") + " Olay Adı: " + event.getEvent().getTitle() + " Olay grubu:" + event.getEvent().getGroupName() + " Olay Türü: " + event.getEvent().getEventTypeName()); 

					return genericResponseItem;
				}
			}
		}
		
		
		List<Integer> eventIdList = totalList.stream().map(EventTableViewItemWrapper::getEvent).map(EventTableViewItem::getId).collect(Collectors.toList());
		
		Integer stateId = StateE.getBatchOperationStateChange(bacthState).getId();
		
		eventRepository.updateBatchOperationsEventState(eventIdList, stateId);

		genericResponseItem.setState(true);
		return genericResponseItem;
	}
    
}
