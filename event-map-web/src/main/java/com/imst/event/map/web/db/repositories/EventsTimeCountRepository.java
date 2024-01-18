package com.imst.event.map.web.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.imst.event.map.hibernate.entity.EventsTimeCount;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.EventsTimeCountProjection;


public interface EventsTimeCountRepository extends ProjectionRepository<EventsTimeCount, Integer> {

	@Query("SELECT CAST(event_date as date), e.eventGroup.id, g.layer.id, COUNT(*) FROM Event e join EventGroup g on g.id = e.eventGroup.id WHERE e.state.id = :stateId AND e.eventGroup.layer.id = :layerId GROUP BY  CAST(event_date as date), e.eventGroup.id, g.layer.id")
	List<Object[]> eventCountsInDateTimes(Integer stateId, Integer layerId);
	
	EventsTimeCount findOneByEventGroupIdAndLayerIdAndEventDayAndEventMonthAndEventYear(Integer eventGroupId, Integer layerId, Integer day, Integer month, Integer year);
	
	List<EventsTimeCountProjection> findAllByEventGroupIdInAndEventMonthAndEventYear(List<Integer> eventGroupIds, Integer month, Integer year);
	
	List<EventsTimeCountProjection> findAllByEventGroupIdInAndEventYear(List<Integer> eventGroupIds, Integer year);
	
	List<EventsTimeCountProjection> findAllByEventGroupIdIn(List<Integer> eventGroupIds);
	
	List<EventsTimeCountProjection> findAllByLayerId(Integer layerId);
	
	
	@Query("SELECT SUM(cet.eventCount), cet.eventYear from EventsTimeCount cet WHERE cet.eventGroup.id in :eventGroupIds GROUP BY cet.eventYear")
	List<Object[]> eventCountsInYears(@Param("eventGroupIds") List<Integer> eventGroupIds);
	
}
