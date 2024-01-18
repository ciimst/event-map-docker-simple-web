package com.imst.event.map.web.db.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.imst.event.map.hibernate.entity.Event;

@Repository
public interface TestRepository extends CrudRepository<Event, Integer> {
	
	List<Event> findAll();
}
