package com.imst.event.map.web.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.imst.event.map.web.constant.MultitenantDatabaseE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.multitenant.conf.TenantContext;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.SidebarEventItemForHeatMap;
import com.imst.event.map.web.vo.SidebarEventItemWrapperForHeatMap;


public class CallableSideBarEventItemServiceForHeatmap implements Callable<List<SidebarEventItemWrapperForHeatMap>> {

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
	private MasterDao masterDao;
	
	private String eventSearchText;
	private List<Integer> eventTypeIdSearch;
	private String eventSearchCity;
	private String eventSearchCountry;
	private List<Integer> eventGroupIdList;
	
	private Date firstScrollDate;

	public CallableSideBarEventItemServiceForHeatmap(DataSourceInfo dataSourceInfo, Integer lastId, List <Integer> groupIdList, List <Integer> userIdList, Integer currentLayerId, Date lastScrollDate, 
			PageRequest pageRequest, Date startDate,  Date endDate, Integer alertLastIdMap, String eventSearchText, Date firstScrollDate, List<Integer> eventTypeIdSearch, String eventSearchCity, String eventSearchCountry, List<Integer> eventGroupIdList) {

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
		this.eventSearchCity = eventSearchCity;
		this.eventSearchCountry = eventSearchCountry;
		this.eventGroupIdList = eventGroupIdList;
		
		this.firstScrollDate = firstScrollDate;
		
		this.eventService = ApplicationContextUtils.getBean(EventService.class);
		
		this.masterDao = multitenantDatabaseE.getMasterDAOBean();
	}


	@Override
	public List<SidebarEventItemWrapperForHeatMap> call() throws Exception {

		TenantContext.setCurrentTenant(this.tenantName);

		Page<SidebarEventItemForHeatMap> eventSidebarList = eventService.findAllProjectedBySidebarForHeatMap(masterDao, pageRequest, startDate, endDate, lastId, groupIdList, userIdList, currentLayerId, lastScrollDate, alertLastIdMap, eventSearchText, firstScrollDate, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventGroupIdList);
		List<Integer> eventIdList = eventSidebarList.stream().map(SidebarEventItemForHeatMap::getId).collect(Collectors.toList());

		List<SidebarEventItemWrapperForHeatMap> list = new ArrayList<>();
		if(eventIdList.size() > 0) {
			
			int endSize = 0;
			int lastSize = 0;	

			while(endSize < eventIdList.size()) {
							 
				endSize = lastSize + Statics.eventMediaListSize;
				
				if(endSize > eventIdList.size()) {
					endSize = eventIdList.size();
				}
			
				lastSize += Statics.eventMediaListSize;
			}
			
				
			list = eventSidebarList.getContent().stream().map(item -> new SidebarEventItemWrapperForHeatMap(item, this.tenantName)).collect(Collectors.toList());
		}	
				
		return list;
	}



}