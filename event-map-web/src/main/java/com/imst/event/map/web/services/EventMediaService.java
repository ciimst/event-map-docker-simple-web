package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.repositories.EventMediaRepository;
import com.imst.event.map.web.db.specifications.EventMediaSpecifications;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.vo.SidebarEventMediaItem;

@Service
public class EventMediaService {

	@Autowired MasterDao masterDao;
	@Autowired EventMediaRepository eventMediaRepository;

	public List<SidebarEventMediaItem> findAllProjectedBySidebar(MasterDao masterDao, Sort sort, List<Integer> eventIdList) {
		
		List<SidebarEventMediaItem> eventSidebarList = masterDao.findAll(EventMediaSpecifications.sidebarSpecification(eventIdList), sort);
		return eventSidebarList;
	}
}
