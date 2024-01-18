package com.imst.event.map.web.db.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.hibernate.entity.Event;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.EventProjection;

@Transactional
public interface EventRepository extends ProjectionRepository<Event, Integer> {
	
	List<EventProjection> findAllProjectedBy();
	
	List<EventProjection> findAllProjectedBy(Pageable page);
	
	List<Event> findAllByIdIn(List<Integer> ids);
	
	Event findByIdAndStateIdIn(Integer id, List<Integer> stateIds);
	

	@Modifying
	@Query("update Event u set u.state.id = :a where u.id in :b")
	void updateBatchOperationsEventState(@Param("b") List<Integer> eventIdList, @Param("a") Integer stateId);
	
	@Query("SELECT event.eventType.id FROM Event event WHERE event.eventGroup.layer.id = :layerId AND event.userId in :userIdList AND event.groupId in :groupIdList AND event.state.id = :stateId GROUP BY event.eventType.id")
	List<Integer> findPermissionedEventTypeIdAndCountByAndStateId(Integer layerId, List<Integer> userIdList, List<Integer> groupIdList, Integer stateId);	
	
	
	@Modifying
	@Query("update Event u set u.state.id = :stateId where u.id in :idList")
	void batchOperationsUpdateEventBlackList(@Param("stateId") Integer stateId, @Param("idList") List<Integer> eventIdList);
}
