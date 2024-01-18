package com.imst.event.map.web.db.repositories;

import java.util.List;

import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserEventGroupPermission;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.UserEventGroupPermissionProjection2;

public interface UserEventGroupPermissionRepository extends ProjectionRepository<UserEventGroupPermission, Long>{
	
	List<UserEventGroupPermissionProjection2> findAllProjectedByUser(User user);
}
