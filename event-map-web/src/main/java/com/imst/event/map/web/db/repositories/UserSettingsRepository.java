package com.imst.event.map.web.db.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserSettings;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.vo.UserSettingsTypeItem;

public interface UserSettingsRepository extends ProjectionRepository<UserSettings, Integer> {
	
	List<UserSettingsTypeItem> findAllProjectedByUser(User user);
	
	List<UserSettings> findAllProjectedByUserAndUserSettingsTypeGroupName(User user, String groupName);
	List<UserSettings> findAllByUser(User user);
	List<UserSettings> findAllByLayerAndUser(Layer layer, User user);
	
	List<UserSettingsTypeItem> findAllByUserAndLayer(User user, Layer layer);
	List<UserSettingsTypeItem> findAllByUserAndLayerNotNull(User user);
	
	List<UserSettingsTypeItem> findAllByUserAndUserSettingsTypeGroupName(User user, String groupName);
	List<UserSettings> findAllByUser(Sort sort,User user);

}
