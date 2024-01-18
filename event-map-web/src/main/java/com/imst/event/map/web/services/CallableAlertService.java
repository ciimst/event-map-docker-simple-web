package com.imst.event.map.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.imst.event.map.hibernate.entity.Alert;
import com.imst.event.map.hibernate.entity.AlertEvent;
import com.imst.event.map.hibernate.entity.AlertState;
import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.web.constant.MultitenantDatabaseE;
import com.imst.event.map.web.constant.Statics;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.multitenant.conf.TenantContext;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;
import com.imst.event.map.web.utils.IPUtils;
import com.imst.event.map.web.utils.MyStringUtils;
import com.imst.event.map.web.utils.SpatialUtil;
import com.imst.event.map.web.vo.AlertCriteriaEventItem;
import com.imst.event.map.web.vo.DataSourceInfo;
import com.imst.event.map.web.vo.EventItem;
import com.vividsolutions.jts.geom.Point;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CallableAlertService  implements Callable<String>{
	
	
	private String tenantName;
	private MultitenantDatabaseE multitenantDatabaseE;
	
	private Integer pageLoadLimit;
	
	private AlertService alertService;
	private EventService eventService;
	
	private MasterDao masterDao;
	
	
	public CallableAlertService(DataSourceInfo dataSourceInfo) {

		this.tenantName = dataSourceInfo.getName();
		this.multitenantDatabaseE = dataSourceInfo.getMultitenantDatabaseE();
		
		this.pageLoadLimit = Integer.parseInt(ApplicationContextUtils.getProperty("cronjob.alarm.check.size")); // default 500
		
		this.alertService = ApplicationContextUtils.getBean(AlertService.class);
		this.eventService = ApplicationContextUtils.getBean(EventService.class);
		
		this.masterDao = multitenantDatabaseE.getMasterDAOBean();
	}
	
	@Override
	public String call() throws Exception {


		AlertState alertState = alertService.findOneAlertStateByDbName(tenantName);
		if(alertState == null) {
			
			Integer lastEventId = 0;
			
			TenantContext.setCurrentTenant(this.tenantName);
			//-------------------Tenant-------------------------------
			EventItem oldestEvent = eventService.getLastEventById(masterDao);
			if(oldestEvent != null) {
				lastEventId = oldestEvent.getId();
			}
			//---------------------Tenant End--------------------------------
			
			TenantContext.setCurrentTenant(Statics.DEFAULT_DB_NAME);	
			
			alertState = new AlertState();
			alertState.setDbName(tenantName);
			alertState.setLastId(lastEventId);
			alertState.setCreateDate(DateUtils.nowT());
			
			alertService.saveAlertState(alertState);
			
			log.info(String.format("A new DB added to the system. Db name : %s - Last event id : %s", tenantName, lastEventId));
		}


		TenantContext.setCurrentTenant(this.tenantName);
		//-------------------Tenant-------------------------------
		PageRequest pageRequest = PageRequest.of(0, pageLoadLimit, Sort.by(Order.asc("id")));
		Page<AlertCriteriaEventItem> eventList = eventService.findAllProjectedByAlert(masterDao, pageRequest, alertState.getLastId());
		
		if(eventList.getContent().size() == 0) {
			return tenantName;
		}
		//---------------------Tenant End--------------------------------
		
		TenantContext.setCurrentTenant(Statics.DEFAULT_DB_NAME);
		
		for (AlertCriteriaEventItem alertCriteriaEventItem : eventList) {
			
			Point point = SpatialUtil.getPoint(alertCriteriaEventItem.getLatitude(), alertCriteriaEventItem.getLongitude());
			
			List<Alert> alertList = alertService.findByPolygonContains(point, alertCriteriaEventItem.getLayerId(), alertCriteriaEventItem.getEventTypeId(), alertCriteriaEventItem.getEventGroupId(),this.tenantName);
			
			List<AlertEvent> alertEventList = new ArrayList<>();
			for (Alert alert : alertList) {
				
				boolean checkExtraCriteriasResult = checkExtraCriterias(alertCriteriaEventItem, alert);
				if(!checkExtraCriteriasResult) {
					continue;
				}
				
				AlertEvent alertEvent = new AlertEvent();
				alertEvent.setDbName(tenantName);
				
				Event event = new Event();
				event.setId(alertCriteriaEventItem.getId());
				alertEvent.setEvent(event);
				alertEvent.setUser(alert.getUser());
				alertEvent.setAlert(alert);
				alertEvent.setCreateDate(DateUtils.nowT());
				alertEvent.setEventIdDbName(String.format("%s_%s", alertEvent.getEvent().getId(), alertEvent.getDbName()));
				alertEvent.setReadState(false);
				
				String ip = IPUtils.getIpAddress();
				alertEvent.setIp(ip);
				
				
				alertEventList.add(alertEvent);
			}
			
			if(alertEventList.size() > 0) {				
				alertService.saveAlertEvents(alertEventList);
			}
			
			if(alertState.getLastId() < alertCriteriaEventItem.getId()) {
				alertState.setLastId(alertCriteriaEventItem.getId());
			}
		}
		
		
		alertState.setUpdateDate(DateUtils.nowT());
		alertService.saveAlertState(alertState);
		
		return tenantName;
	}
	
	private boolean checkExtraCriterias(AlertCriteriaEventItem eventItem, Alert alert) {
		
		boolean queryContains = StringUtils.isEmpty(alert.getQuery()); // Alarm kriterinde herhangi bir kriter belirtilmemiş ise bu kriteri default olarak sağlıyor demektir. true kabul edilir
		if(!queryContains) { // query kriteri title, spot ve description alanları içerisinde OR'lanarak aranır.
			queryContains |= MyStringUtils.containsString(eventItem.getTitle(), alert.getQuery());
			queryContains |= MyStringUtils.containsString(eventItem.getSpot(), alert.getQuery());
			//queryContains |= MyStringUtils.containsString(eventItem.getDescription(), alert.getQuery());
		}
		
		boolean eventTypeResult = alert.getEventType() == null; // Alarm kriterinde EventType belirtilmemiş ise bu kriteri default olarak sağlıyor demektir
		if(!eventTypeResult) {
			eventTypeResult = eventItem.getEventTypeId().equals(alert.getEventType().getId());
		}
		
		boolean eventGroupResult = alert.getEventGroup() == null;
		if(!eventGroupResult) {
			eventGroupResult = eventItem.getEventGroupId().equals(alert.getEventGroup().getId()) && tenantName.equals(alert.getEventGroupDbName());			
		}
		
		boolean reservedKeyResult = StringUtils.isEmpty(alert.getReservedKey());
		if(!reservedKeyResult) {
			reservedKeyResult = MyStringUtils.containsString(eventItem.getReservedKey(), alert.getReservedKey());
		}
		
		boolean reservedTypeResult = StringUtils.isEmpty(alert.getReservedType());
		if(!reservedTypeResult) {
			reservedTypeResult = MyStringUtils.containsString(eventItem.getReservedType(), alert.getReservedType());
		}
		
		boolean reservedIdResult = StringUtils.isEmpty(alert.getReservedId());
		if(!reservedIdResult) {
			reservedIdResult = MyStringUtils.containsString(eventItem.getReservedId(), alert.getReservedId());
		}
		
		boolean reservedLinkResult = StringUtils.isEmpty(alert.getReservedLink());
		if(!reservedLinkResult) {
			reservedLinkResult = MyStringUtils.containsString(eventItem.getReservedLink(), alert.getReservedLink());
		}
		
		boolean result = queryContains && eventTypeResult && eventGroupResult && reservedKeyResult && reservedTypeResult && reservedIdResult && reservedLinkResult;
		
		return result;
	}
	

	
}
