package com.imst.event.map.web.db.repositories;

import java.util.List;

import com.imst.event.map.hibernate.entity.EventLink;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.vo.EventLinkItem;

public interface EventLinkRepository extends ProjectionRepository<EventLink, Integer> {
		
	List<EventLinkItem> findAllByEventColumnIdIn(List<Integer> columnId);
	
	
 }
