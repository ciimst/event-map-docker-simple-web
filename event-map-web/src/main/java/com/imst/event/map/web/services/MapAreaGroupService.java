package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.repositories.MapAreaGroupRepository;
import com.imst.event.map.web.db.specifications.MapAreaGroupSpecifications;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.vo.MapAreaGroupItem;

@Service
public class MapAreaGroupService {

	@Autowired MasterDao masterDao;
	@Autowired MapAreaGroupRepository mapAreaGroupRepository;

	public List<MapAreaGroupItem> findAll(Sort sort, Integer currentLayerId) {
		
		List<MapAreaGroupItem> mapAreaGroupList = masterDao.findAll(MapAreaGroupSpecifications.mapAreaGroupSpecification(currentLayerId), sort);
		return mapAreaGroupList;
	}
}
