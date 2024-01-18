package com.imst.event.map.web.db.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import com.imst.event.map.web.db.projections.LayerProjection;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.web.db.ProjectionRepository;


public interface LayerRepository extends ProjectionRepository<Layer, Integer> {
	
//	Layer findOneByIdAndIsTemp(Integer id, Boolean isTemp);
	
	Layer findOneByGuidAndIsTemp(String guid, Boolean isTemp);
	
	Layer findOneById(Integer id);
	
	List<Layer> findAllByIdIn(List<Integer> idList);
	
	List<LayerProjection> findAllProjectedByOrderByName();
	
	List<Layer> findAll(Sort sort);
}
