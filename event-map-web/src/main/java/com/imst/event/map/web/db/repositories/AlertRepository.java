package com.imst.event.map.web.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.imst.event.map.hibernate.entity.Alert;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.AlertProjection;
import com.vividsolutions.jts.geom.Geometry;


public interface AlertRepository extends ProjectionRepository<Alert, Integer> {
	
	Alert findByIdAndLayerAndUser(Integer id, Layer layer, User user);
	
	List<Alert> findAllByLayerAndUserOrderByIdDesc(Layer layer, User user);
	
	@Query(value = "select a from Alert a WHERE (contains(a.polygonCoordinate, :point) = true or polygon_coordinate is null) and (fk_event_type_id = :eventTypeId or fk_event_type_id is null) and"
			+ " ((fk_event_group_id = :eventGroupId and event_group_db_name = :tenantName) or fk_event_group_id is null) and a.layer.id = :layerId")
	List<Alert> findByPolygonContains(Geometry point, Integer layerId, Integer eventTypeId, Integer eventGroupId, String tenantName);
	
	
	Alert findAllById(Integer id);
	
	List<AlertProjection> findAllProjectedByLayerAndUserOrderByIdDesc(Layer layer, User user);
}
