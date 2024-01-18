package com.imst.event.map.web.db.repositories;

import java.util.List;

import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserUserId;
import com.imst.event.map.web.db.ProjectionRepository;

public interface UserUserIdRepository extends ProjectionRepository<UserUserId, Long>{
	List<UserUserId> findAllByUser(User user);
}
