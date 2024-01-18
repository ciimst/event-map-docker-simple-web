package com.imst.event.map.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserUserId;
import com.imst.event.map.web.db.repositories.UserUserIdRepository;

@Service
public class UserUserIdService {
	
	@Autowired UserUserIdRepository userUserIdRepository;

	public List<UserUserId> findAllByUser(User user) {
		
		return userUserIdRepository.findAllByUser(user);
	}
}
