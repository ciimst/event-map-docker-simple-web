package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.repositories.EventTypeRepository;
import com.imst.event.map.web.db.specifications.EventTypeSpecifications;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.vo.EventTypeItem;

@Service
public class EventTypeService {

	@Autowired MasterDao masterDao;
	@Autowired EventTypeRepository EventTypeRepository;

	public List<EventTypeItem> findAll(Sort sort, List<Integer> eventTypeIdList) {
		
		List<EventTypeItem> EventTypeList = masterDao.findAll(EventTypeSpecifications.EventTypeSpecification(eventTypeIdList), sort);
		return EventTypeList;
	}
}
