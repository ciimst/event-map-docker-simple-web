package com.imst.event.map.web.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.imst.event.map.hibernate.entity.AlertEvent;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.AlertEventCountProjection;


public interface AlertEventRepository extends ProjectionRepository<AlertEvent, Integer> {
	
	AlertEvent findAllById(Integer id);
	
	List<AlertEvent> findAllByAlertId(Integer id);
	
//	long countByUserAndLayerAndReadStateIsFalse(User user, Layer layer);
	long countByUserAndReadStateIsFalse(User user);
	
	@Query("SELECT COUNT(*) as count, ae.alert.id as alertId from AlertEvent ae WHERE ae.alert.id in :alertIds GROUP BY ae.alert.id")
	List<AlertEventCountProjection> findAllByAlertEventCount(@Param("alertIds") List<Integer> alertIds);
}
