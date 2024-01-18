package com.imst.event.map.web.db.repositories.oracle;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.imst.event.map.web.db.projections.EventProjection;
import com.imst.event.map.web.db.repositories.EventRepository;


public interface EventRepositoryOracle extends EventRepository {

	List<EventProjection> findAllProjectedBy(Pageable page);
}
