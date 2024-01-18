package com.imst.event.map.web.db.repositories.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.imst.event.map.hibernate.entity.redis.UserLastPage;


@Repository
public interface UserLastPageRepository extends CrudRepository<UserLastPage, Integer> {
	

}