package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.repositories.TagRepository;
import com.imst.event.map.web.db.specifications.EventTagSpecifications;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.vo.SidebarTagItem;

@Service
public class TagService {
	
	@Autowired MasterDao masterDao;
	@Autowired TagRepository tagRepository;

	public List<SidebarTagItem> findAllProjectedBySidebar(MasterDao masterDao, Sort sort, List<Integer> eventIdList) {
		
		List<SidebarTagItem> eventSidebarList = masterDao.findAll(EventTagSpecifications.sidebarSpecification(eventIdList), sort);
		return eventSidebarList;
	}
}
