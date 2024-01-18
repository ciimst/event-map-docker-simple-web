package com.imst.event.map.web.db.repositories;

import java.util.List;

import com.imst.event.map.hibernate.entity.EventType;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.EventTypeProjection;
import com.imst.event.map.web.vo.EventTypeItem;


public interface EventTypeRepository extends ProjectionRepository<EventType, Long> {
	
	List<EventType> findAll();
	List<EventTypeItem> findAllProjectedByIdIn(List<Integer> eventTypeItemIdList);
	
	List<EventTypeItem> findByIdIn(List<Integer> idList);
	
	List<EventTypeProjection> findAllProjectedBy();
	
}