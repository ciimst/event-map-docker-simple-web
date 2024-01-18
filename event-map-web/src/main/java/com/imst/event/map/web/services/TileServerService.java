package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.specifications.TileServerSpecifications;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.vo.TileServerItem;

@Service
public class TileServerService {
	
	@Autowired MasterDao masterDao;

	public List<TileServerItem> findAllProjectedBySidebar(Sort sort) {
		
		List<TileServerItem> list = masterDao.findAll(TileServerSpecifications.sidebarSpecification(), sort);
		return list;
	}
}
