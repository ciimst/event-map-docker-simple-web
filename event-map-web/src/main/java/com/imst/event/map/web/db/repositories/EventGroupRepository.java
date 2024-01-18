package com.imst.event.map.web.db.repositories;

import java.util.List;

import com.imst.event.map.hibernate.entity.EventGroup;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.EventGroupProjection;


public interface EventGroupRepository extends ProjectionRepository<EventGroup, Long> {
	
	List<EventGroup> findAllByLayerIdIn(List<Integer> layerIdList);
	List<EventGroup> findAllProjectedByIdIn(List<Integer> eventGroupItemIdList);
	List<EventGroupProjection> findAllProjectedByOrderByName();
	List<EventGroupProjection> findAllProjectedByLayerOrderByName(Layer layerId);
	List<EventGroupProjection> findAllProjectedByLayerIdInOrderByName(List<Integer> layerIdList);
}
