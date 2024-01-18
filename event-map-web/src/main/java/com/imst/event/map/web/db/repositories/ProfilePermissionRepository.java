package com.imst.event.map.web.db.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.imst.event.map.hibernate.entity.ProfilePermission;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.ProfilePermissionProjection;

public interface ProfilePermissionRepository extends ProjectionRepository<ProfilePermission, Integer> {
	
	
	Page<ProfilePermissionProjection> findAllProjectedBy(Pageable pageable);
	
	List<ProfilePermission> findProfileByProfileId(Integer id);
	
	List<ProfilePermission> findAllByProfileId(Integer id);
	
	List<ProfilePermission> findAllByProfileIdAndPermissionId(Integer profileId, Integer permissionId);
	
	List<ProfilePermission> findPermissionIdByProfileId(Integer id);
}
