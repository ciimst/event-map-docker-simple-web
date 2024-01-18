package com.imst.event.map.web.db.repositories;

import java.util.List;
import com.imst.event.map.hibernate.entity.Settings;
import com.imst.event.map.web.db.ProjectionRepository;

public interface SettingsRepository extends ProjectionRepository<Settings, Integer> {

	List<Settings> findAllProjectedBySettingsValueAndGroupName(String deger, String groupName);
}
