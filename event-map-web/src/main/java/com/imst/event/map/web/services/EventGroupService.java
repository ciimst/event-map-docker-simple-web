package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.repositories.EventGroupRepository;
import com.imst.event.map.web.db.specifications.EventGroupSpecifications;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.vo.EventGroupItem;

@Service
public class EventGroupService {

	@Autowired MasterDao masterDao;
	@Autowired EventGroupRepository eventGroupRepository;

	public List<EventGroupItem> findAllAndSetDbName(MasterDao masterDao, Sort sort,Integer currentLayerId, String dbName, List<Integer> eventGroupIdList) {
		
		List<EventGroupItem> eventGroupList = masterDao.findAll(EventGroupSpecifications.eventGroupSpecification(currentLayerId, eventGroupIdList), sort);

		eventGroupList.forEach(item -> {
			item.setDbName(dbName);
		});
		return eventGroupList;
	}
}
