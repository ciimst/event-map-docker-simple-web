package com.imst.event.map.web.db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.db.ProjectionRepository;
import com.imst.event.map.web.db.projections.UserProjection;

public interface UserRepository extends ProjectionRepository<User, Long> {
	
	User findByUsername(String username);
	User findByUsernameAndIsDbUser(String username, boolean isDbUser);
	
	User findByUsernameAndIsDbUserAndState(String username, boolean isDbUser, boolean state);
	
	
	User findById(Integer id);
	
	Page<UserProjection> findAllProjectedBy(Pageable pageable);
	
	User findOneByUsername(String username);
	
	
}
