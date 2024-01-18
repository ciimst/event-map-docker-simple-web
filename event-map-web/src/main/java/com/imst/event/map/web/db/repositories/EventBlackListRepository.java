package com.imst.event.map.web.db.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.imst.event.map.hibernate.entity.EventBlackList;
import com.imst.event.map.web.db.ProjectionRepository;
@Transactional
public interface EventBlackListRepository extends ProjectionRepository<EventBlackList, Integer> {
	
	List<EventBlackList> findAllByEventId(Integer id);

	List<EventBlackList> findAllByEventIdAndBlackListId(Integer eventId, Integer blackListId);
	List<EventBlackList> findAllByEventIdInAndBlackListId(List<Integer> eventIds, Integer blackListId);
	List<EventBlackList> findAllByEventIdInAndBlackListIdIsNot(List<Integer> eventIds, Integer blackListId);
	
	@Modifying
	@Query("delete from EventBlackList eb where eb.event.id in :eventIdList AND eb.blackList.id = :blackListId")
	void eventBlackListDeleted(@Param("eventIdList") List<Integer> eventIdList, @Param("blackListId") Integer blackListId);
	
	
	@Modifying
	@Query("delete from EventBlackList eb where eb.event.id = :eventId ")
	void eventBlackListDeletedEventIdIn(@Param("eventId") Integer eventId);
	
	

}
