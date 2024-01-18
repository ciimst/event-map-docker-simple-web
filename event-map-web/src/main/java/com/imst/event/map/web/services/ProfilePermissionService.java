package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.db.specifications.ProfilePermissionSpecifications;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.vo.ProfilePermissionItem;

@Service
public class ProfilePermissionService {
	
	@Autowired MasterDao masterDao;

	public List<ProfilePermissionItem> findByProfileProjectedByPermission(Integer profileId) {
		
		List<ProfilePermissionItem> profilePermissionItemList = masterDao.findAll(ProfilePermissionSpecifications.permissionNameSpecification(profileId), Sort.by("id"));
		return profilePermissionItemList;
	}
}
