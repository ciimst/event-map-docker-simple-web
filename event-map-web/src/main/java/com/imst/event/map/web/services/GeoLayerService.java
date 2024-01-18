package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.repositories.GeoLayerRepository;
import com.imst.event.map.web.db.specifications.GeoLayerSpecifications;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.vo.GeoLayerItem;

@Service
public class GeoLayerService {
	
	@Autowired MasterDao masterDao;
	@Autowired GeoLayerRepository geoLayerRepository;

	public List<GeoLayerItem> findAllProjectedBySidebar(Sort sort,Integer currentLayerId) {
		
		List<GeoLayerItem> list = masterDao.findAll(GeoLayerSpecifications.sidebarSpecification(currentLayerId), sort);
		return list;
	}
}
