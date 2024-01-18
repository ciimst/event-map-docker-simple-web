package com.imst.event.map.web.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.imst.event.map.web.constant.MultitenantDatabaseE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.multitenant.conf.TenantContext;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.SidebarEventItem;
import com.imst.event.map.web.vo.SidebarEventItemWrapper;
import com.imst.event.map.web.vo.SidebarEventMediaItem;
import com.imst.event.map.web.vo.SidebarTagItem;


public class CallableSideBarEventItemService implements Callable<List<SidebarEventItemWrapper>> {

	private String tenantName;
	private MultitenantDatabaseE multitenantDatabaseE;
	private Integer lastId;
	private List <Integer> groupIdList;
	private List <Integer> userIdList;
	private Integer currentLayerId;
	private Date lastScrollDate;
	private PageRequest pageRequest;
	private Date startDate;
	private Date endDate;
	private Integer alertLastIdMap;

	private EventService eventService;
	private EventMediaService eventMediaService;
	private TagService tagService;
	private MasterDao masterDao;
	
	private String eventSearchText;
	private String eventSearchCity;
	private String eventSearchCountry;
	private List<Integer> eventTypeIdSearch;
	
	private Date firstScrollDate;
	
	private Double southWestLng;
	private Double southWestLat;
	private Double northEastLng;
	private Double northEastLat;
	
	List<Integer> eventGroupIdList;
	
	private Boolean isAlertEvent;

	public CallableSideBarEventItemService(DataSourceInfo dataSourceInfo, Integer lastId, List <Integer> groupIdList, List <Integer> userIdList, Integer currentLayerId, Date lastScrollDate, 
			PageRequest pageRequest, Date startDate,  Date endDate, Integer alertLastIdMap, String eventSearchText, Date firstScrollDate, List<Integer> eventTypeIdSearch, Double southWestLng, Double southWestLat,
			Double northEastLng, Double northEastLat, List<Integer> eventGroupIdList, String eventSearchCity, String eventSearchCountry, Boolean isAlertEvent) {

		this.tenantName = dataSourceInfo.getName();
		this.multitenantDatabaseE = dataSourceInfo.getMultitenantDatabaseE();
		this.lastId = lastId;				
		
		this.groupIdList = groupIdList;
		this.userIdList = userIdList;
		this.currentLayerId = currentLayerId;
		this.lastScrollDate = lastScrollDate;
		this.pageRequest = pageRequest;
		this.startDate = startDate;
		this.endDate = endDate;
		this.alertLastIdMap = alertLastIdMap;
		this.eventSearchText = eventSearchText;
		this.eventTypeIdSearch = eventTypeIdSearch;
		
		this.firstScrollDate = firstScrollDate;
		
		this.southWestLng = southWestLng;
		this.southWestLat = southWestLat;
		this.northEastLng = northEastLng;
		this.northEastLat = northEastLat;
		this.eventGroupIdList = eventGroupIdList;
		this.isAlertEvent = isAlertEvent;
		
		this.eventSearchCity = eventSearchCity;
		this.eventSearchCountry = eventSearchCountry;
		
		this.eventService = ApplicationContextUtils.getBean(EventService.class);
		this.eventMediaService = ApplicationContextUtils.getBean(EventMediaService.class);
		this.tagService = ApplicationContextUtils.getBean(TagService.class);
		
		this.masterDao = multitenantDatabaseE.getMasterDAOBean();
	}


	@Override
	public List<SidebarEventItemWrapper> call() throws Exception {

		TenantContext.setCurrentTenant(this.tenantName);

		Page<SidebarEventItem> eventSidebarList = eventService.findAllProjectedBySidebar(masterDao, pageRequest, startDate, endDate, lastId, groupIdList, userIdList, currentLayerId, lastScrollDate, alertLastIdMap, eventSearchText, firstScrollDate, eventTypeIdSearch,southWestLng, southWestLat, northEastLng, northEastLat, eventGroupIdList, eventSearchCity, eventSearchCountry, isAlertEvent);
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
			
				
			list = eventSidebarList.getContent().stream().map(item -> new SidebarEventItemWrapper(item, eventMediaMap.get(item.getId()), eventTagMap.get(item.getId()), this.tenantName)).collect(Collectors.toList());
		}	
				
		return list;
	}



}