package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.specifications.MapAreaSpecifications;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.vo.MapAreaItem;

@Service
public class MapAreaService {
	
	@Autowired MasterDao masterDao;

	public List<MapAreaItem> findAllProjectedBySidebar(Sort sort, Integer currentLayerId) {
		
		List<MapAreaItem> list = masterDao.findAll(MapAreaSpecifications.sidebarSpecification(currentLayerId), sort);
		return list;
	}
}
