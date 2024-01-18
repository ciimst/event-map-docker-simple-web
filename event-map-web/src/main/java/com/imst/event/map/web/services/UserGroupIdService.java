package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserGroupId;
import com.imst.event.map.web.db.repositories.UserGroupIdRepository;

@Service
public class UserGroupIdService {

	
	@Autowired UserGroupIdRepository userGroupIdRepository;

	public List<UserGroupId> findAllByUser(User user) {
		
		return userGroupIdRepository.findAllByUser(user);
	}
}
