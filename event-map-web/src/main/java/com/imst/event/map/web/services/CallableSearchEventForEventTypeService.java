package com.imst.event.map.web.services;

import java.util.List;
import java.util.concurrent.Callable;

import com.imst.event.map.web.constant.MultitenantDatabaseE;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.multitenant.conf.TenantContext;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.EventTypeItem;


public class CallableSearchEventForEventTypeService implements Callable<List<EventTypeItem>> {

	private String tenantName;
	private MultitenantDatabaseE multitenantDatabaseE;
	private List <Integer> groupIdList;
	private List <Integer> userIdList;
	private Integer currentLayerId;
	private EventService eventService;
	private MasterDao masterDao;


	public CallableSearchEventForEventTypeService(DataSourceInfo dataSourceInfo, List <Integer> groupIdList, List <Integer> userIdList, Integer currentLayerId) {

		this.tenantName = dataSourceInfo.getName();
		this.multitenantDatabaseE = dataSourceInfo.getMultitenantDatabaseE();				
		this.groupIdList = groupIdList;
		this.userIdList = userIdList;
		this.currentLayerId = currentLayerId;
		this.eventService = ApplicationContextUtils.getBean(EventService.class);
		this.masterDao = multitenantDatabaseE.getMasterDAOBean();
	}


	@Override
	public List<EventTypeItem> call() throws Exception {

		TenantContext.setCurrentTenant(this.tenantName);
		
		
		List<EventTypeItem> list = eventService.prepareEventTypeList(masterDao, groupIdList, userIdList, currentLayerId);

		return list;
	}



}