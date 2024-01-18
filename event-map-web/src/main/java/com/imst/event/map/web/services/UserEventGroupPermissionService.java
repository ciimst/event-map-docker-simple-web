package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.db.specifications.EventGroupSpecifications;
import com.imst.event.map.web.vo.UserEventGroupPermissionItem;

@Service
public class UserEventGroupPermissionService {

	@Autowired MasterDao masterDao;
	
	
	public List<UserEventGroupPermissionItem> findAllByUser(Sort sort, User user){
		
		List<UserEventGroupPermissionItem> eventGroupList = masterDao.findAll(EventGroupSpecifications.userEventGroupPermissionSpecification(user), sort);
		
		return eventGroupList;
	}
	
	public List<UserEventGroupPermissionItem> findAllByLayerIdList(Sort sort, List<Integer> layerIdList){
		
		List<UserEventGroupPermissionItem> eventGroupList = masterDao.findAll(EventGroupSpecifications.userEventGroupPermissionByLayerIdListSpecification(layerIdList), sort);
		
		return eventGroupList;
	}
}
