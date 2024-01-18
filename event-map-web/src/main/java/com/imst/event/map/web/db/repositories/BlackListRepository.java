package com.imst.event.map.web.db.repositories;

import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.vo.BlackListItem;

import java.util.List;

import com.imst.event.map.hibernate.entity.BlackList;

public interface BlackListRepository extends ProjectionRepository<BlackList, Integer> {
	
	
	BlackListItem findOneByIdAndStateIdIn(Integer id, List<Integer> stateIds);
	
	BlackList findByIdAndStateIdIn(Integer id, List<Integer> stateIds);
}


