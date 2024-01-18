package com.imst.event.map.web.db.repositories;

import java.util.List;

import com.imst.event.map.hibernate.entity.Profile;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.ProfileProjection;

public interface ProfileRepository extends ProjectionRepository<Profile, Long> {
	
	List<ProfileProjection> findAllProjectedBy();
	List<Profile> findByIsDefault(Boolean isDefault);
	
}
