package com.imst.event.map.web.services;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.imst.event.map.web.constant.MultitenantDatabaseE;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.multitenant.conf.TenantContext;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.EventGroupItem;

public class CallableEventGroupItemService  implements Callable<List<EventGroupItem>>{
	
	
	private String tenantName;
	private MultitenantDatabaseE multitenantDatabaseE;
	private Integer currentLayerId;
	
	private EventGroupService eventGroupService;
	private MasterDao masterDao;
	
	public CallableEventGroupItemService(DataSourceInfo dataSourceInfo, Integer currentLayerId) {

		this.tenantName = dataSourceInfo.getName();
		this.multitenantDatabaseE = dataSourceInfo.getMultitenantDatabaseE();
		this.currentLayerId = currentLayerId;

		this.eventGroupService = ApplicationContextUtils.getBean(EventGroupService.class);	
		
		this.masterDao = multitenantDatabaseE.getMasterDAOBean();
	}
	
	@Override
	public List<EventGroupItem> call() throws Exception {

		TenantContext.setCurrentTenant(this.tenantName);

				
		List<EventGroupItem> eventGroupList = eventGroupService.findAllAndSetDbName(masterDao, Sort.by(Direction.ASC, "id"), currentLayerId, tenantName, null);

		return eventGroupList;
	}
}
