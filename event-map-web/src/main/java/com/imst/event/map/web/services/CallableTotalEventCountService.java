package com.imst.event.map.web.services;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.imst.event.map.web.constant.MultitenantDatabaseE;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.multitenant.conf.TenantContext;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.vo.DataSourceInfo;


public class CallableTotalEventCountService implements Callable<Integer> {

	private String tenantName;
	private MultitenantDatabaseE multitenantDatabaseE;
	private List <Integer> groupIdList;
	private List <Integer> userIdList;
	private Integer currentLayerId;
	private EventService eventService;
	private MasterDao masterDao;
	private String eventSearch; 
	private List<Integer> eventTypeIdSearch;
	private String eventSearchCity;
	private String eventSearchCountry;
	private List<Integer> eventGroupIdList;
	private Boolean isAlertEvent;
	private Date startDate;
	private Date endDate;

	public CallableTotalEventCountService(DataSourceInfo dataSourceInfo, List <Integer> groupIdList, List <Integer> userIdList, Integer currentLayerId, String eventSearch, List<Integer> eventTypeIdSearch, 
			String eventSearchCity, String eventSearchCountry, List<Integer> eventGroupIdList, Boolean isAlertEvent, Date startDate, Date endDate) {

		this.tenantName = dataSourceInfo.getName();
		this.multitenantDatabaseE = dataSourceInfo.getMultitenantDatabaseE();		
		
		this.groupIdList = groupIdList;
		this.userIdList = userIdList;
		this.currentLayerId = currentLayerId;
		this.eventSearch = eventSearch;
		this.eventTypeIdSearch = eventTypeIdSearch;
		this.eventSearchCity = eventSearchCity;
		this.eventSearchCountry = eventSearchCountry;
		this.eventGroupIdList = eventGroupIdList;
		this.isAlertEvent = isAlertEvent;
		this.startDate = startDate;
		this.endDate = endDate;
		
		this.eventService = ApplicationContextUtils.getBean(EventService.class);
		this.masterDao = multitenantDatabaseE.getMasterDAOBean();
	}


	@Override
	public Integer call() throws Exception {

		TenantContext.setCurrentTenant(this.tenantName);
		
		Integer eventCount = eventService.getTotalCountEvents(masterDao, groupIdList, userIdList, currentLayerId, eventSearch, eventTypeIdSearch, eventSearchCity, eventSearchCountry, eventGroupIdList, isAlertEvent, startDate, endDate);			
		return eventCount;
	}



}