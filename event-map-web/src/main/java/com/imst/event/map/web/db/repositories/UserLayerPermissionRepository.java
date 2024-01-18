package com.imst.event.map.web.db.repositories;

import java.util.List;

import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserLayerPermission;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.UserLayerPermissionProjection2;

public interface UserLayerPermissionRepository  extends ProjectionRepository<UserLayerPermission, Long>{

	List<UserLayerPermission> findAllByLayerId(Integer layerId);
	List<UserLayerPermissionProjection2> findAllProjectedByUser(User user);
}
