package com.imst.event.map.web.db.repositories;

import java.util.List;

import com.imst.event.map.hibernate.entity.UserSettingsType;
import com.imst.event.map.web.db.ProjectionRepository;

public interface UserSettingsTypeRepository extends ProjectionRepository<UserSettingsType, Integer> {

	//List<UserSettingsProjection> findAllProjectedBy();
	
	UserSettingsType findOneBySettingsKey(String settingsKey);
	
	List<UserSettingsType> findAllByIsLayer(boolean isLayer);
	
	List<UserSettingsType> findAllProjectedByGroupName(String groupName);
}
