package com.imst.event.map.web.db.repositories;

import java.util.List;

import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserGroupId;
import com.imst.event.map.web.db.ProjectionRepository;


public interface UserGroupIdRepository extends ProjectionRepository<UserGroupId, Long> {
	
	List<UserGroupId> findAllByUser(User user);
}
