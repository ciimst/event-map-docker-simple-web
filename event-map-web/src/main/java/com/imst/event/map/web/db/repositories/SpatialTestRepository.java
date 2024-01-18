package com.imst.event.map.web.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.imst.event.map.hibernate.entity.SpatialTest;
import com.imst.event.map.web.db.ProjectionRepository;
import com.vividsolutions.jts.geom.Geometry;


public interface SpatialTestRepository extends ProjectionRepository<SpatialTest, Integer> {
	

	@Query(value = "select s from SpatialTest s WHERE contains(s.polyTest, :point) = true")
	List<SpatialTest> findByPolyTestContains(Geometry point);
	
}
