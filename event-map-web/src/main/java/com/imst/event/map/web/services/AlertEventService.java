package com.imst.event.map.web.services;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.imst.event.map.web.db.repositories.AlertEventRepository;
import com.imst.event.map.web.db.specifications.AlertEventSpecifications;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.vo.SidebarAlertEventItem;

@Service

public class AlertEventService {

	@Autowired MasterDao masterDao;
	@Autowired AlertEventRepository alertEventRepository;
	
	public List<SidebarAlertEventItem> findAllProjectedBySidebar(Sort sort, List<String> eventIdDbName, Integer userId){
		
		List<SidebarAlertEventItem> alertEventList = masterDao.findAll(AlertEventSpecifications.alertEventSpecification(eventIdDbName, userId),sort);
		
		return alertEventList;
	}
	
	public Integer countAllUnreadByUserAndLayer(Integer userId, Integer layerId, List<Integer> permEventGroupIds){
		
		TypedQuery<Long> countQuery = masterDao.getCountQuery(AlertEventSpecifications.countAlertEventSpecificationUnreadByUserAndLayer(userId, layerId, permEventGroupIds));
		long executeCountQuery = MasterDao.executeCountQuery(countQuery);
		return (int) executeCountQuery;
	}
	
}
