package com.imst.event.map.web.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.hibernate.entity.Alert;
import com.imst.event.map.hibernate.entity.AlertEvent;
import com.imst.event.map.hibernate.entity.AlertState;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.repositories.AlertEventRepository;
import com.imst.event.map.web.db.repositories.AlertRepository;
import com.imst.event.map.web.db.repositories.AlertStateRepository;
import com.imst.event.map.web.db.specifications.AlertSpecifications;
import com.imst.event.map.web.vo.SidebarAlertItem;
import com.vividsolutions.jts.geom.Geometry;

@Service
public class AlertService {

	@Autowired MasterDao masterDao;
	@Autowired AlertRepository alertRepository;
	@Autowired AlertStateRepository alertStateRepository;
	@Autowired AlertEventRepository alertEventRepository;
	
	public AlertState findOneAlertStateByDbName(String dbName) {
		
		AlertState alertStateExample = new AlertState();
		alertStateExample.setDbName(dbName);
		
		Example<AlertState> example = Example.of(alertStateExample);
		Optional<AlertState> alertStateOptional = alertStateRepository.findOne(example);
		
		if (alertStateOptional.isPresent()) {
			
			return alertStateOptional.get();
		}
		
		return null;
	}
	
	public List<Alert> findByPolygonContains(Geometry point, Integer layerId, Integer eventTypeId, Integer eventGroupId, String tenantName) {
		List<Alert> alertList = alertRepository.findByPolygonContains(point, layerId, eventTypeId, eventGroupId, tenantName);
		
		
		return alertList;
	}
	
	public void saveAlertState(AlertState alertState) {
		
		alertStateRepository.save(alertState);
	}
	
	public void saveAlertEvents(List<AlertEvent> alertEventList) {
		
		alertEventRepository.saveAll(alertEventList);
	}

	
	public List<SidebarAlertItem> findAllProjectedByAlert(Sort sort, Integer alertId){
		
		List<SidebarAlertItem> alertEventList = masterDao.findAll(AlertSpecifications.alertSpecification(alertId), sort);
		
		return alertEventList;
	}
	
	public List<AlertState> findAllAlertState(){
		
		List<AlertState> alertStateList = alertStateRepository.findAll();
		
		return alertStateList;
	}

}
