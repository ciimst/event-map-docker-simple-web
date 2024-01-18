package com.imst.event.map.web.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.db.repositories.LayerRepository;
import com.imst.event.map.web.db.specifications.LayerSpecification;
import com.imst.event.map.web.db.dao.MasterDao;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.vo.LayerSimpleItem;
import com.imst.event.map.web.vo.UserLayerPermissionItem;

@Service
public class UserLayerPermissionService {

	@Autowired MasterDao masterDao;
	@Autowired LayerRepository layerRepository;

		
	public List<UserLayerPermissionItem> findAllByUser(Sort sort,User user) {
				
		List<UserLayerPermissionItem> layerList = masterDao.findAll(LayerSpecification.layerSpecification(user), sort);
		return layerList;
	}
	
	public LayerSimpleItem checkLayerPermissionAndGet(String currentLayerGuid) {
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		Optional<UserLayerPermissionItem> userLayerPermissionItem = sessionUser.getUserLayerPermissionList().stream().filter(x -> x.getLayerGuid().equals(currentLayerGuid)).findFirst();
		
		if(userLayerPermissionItem.isPresent()) {
			return new LayerSimpleItem(userLayerPermissionItem.get());
		}
		
		try {
			Layer layer = layerRepository.findOneByGuidAndIsTemp(currentLayerGuid, true);	
			boolean hasPermission = layer.getIsTemp() == null ? false : layer.getIsTemp();
			if(hasPermission) {
				return new LayerSimpleItem(layer, true);
			}
		} catch (Exception e) { }
				
		return null;
	}
	
	public boolean checkEventGroupPermission(LayerSimpleItem layerSimpleItem, List<String> eventGroupDbNameIdList) {
		
		if(layerSimpleItem.getHasFullPermission()) {
			return true;
		}
		
		List<String[]> eventGroupDbNameIdArrList = eventGroupDbNameIdList.stream().map(item -> item.split("_")).collect(Collectors.toList());
		boolean anyMatch = eventGroupDbNameIdArrList.stream().anyMatch(item -> !(item[0].equalsIgnoreCase("default")));
		if (anyMatch) {// diğer veritabanlarında gelen group id varsa
			return false;
		}
		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		List<String> userSessionEventGroupPermissionList = sessionUser.getUserEventGroupPermissionList().stream().map(item -> item.getEventGroupId().toString()).collect(Collectors.toList());
		List<String> eventGroupIdList = eventGroupDbNameIdArrList.stream().map(item -> item[1]).collect(Collectors.toList());
		boolean hasPermission = userSessionEventGroupPermissionList.containsAll(eventGroupIdList);
		
		return hasPermission;
	}
}
