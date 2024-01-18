package com.imst.event.map.web.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imst.event.map.hibernate.entity.redis.UserLastPage;
import com.imst.event.map.web.db.repositories.redis.UserLastPageRepository;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.utils.ApplicationContextUtils;
import com.imst.event.map.web.utils.DateUtils;

@Controller
@RequestMapping("/redis")
public class UserLastPageWithRedisController {

	@Autowired private UserLastPageRepository userLastPageRepository;
	
	
	@RequestMapping(value = "/userLastPage", method = RequestMethod.GET)
	@ResponseBody
	public boolean userLastPage(
			  @RequestParam(name = "locationUrl") String locationUrl) {

		
		UserItemDetails sessionUser = ApplicationContextUtils.getUser();
		
		Date date = DateUtils.now();
		
		Optional<UserLastPage> optUserLastPage = userLastPageRepository.findById(sessionUser.getUserId());
		UserLastPage userLastPage;
		
		if(optUserLastPage.isPresent()) {//edit
			userLastPage = optUserLastPage.get();
			userLastPage.setUpdateDate(DateUtils.formatWithCurrentLocale(date));
			userLastPage.setUrl(locationUrl);
		}else {//add
			
			userLastPage = new UserLastPage(sessionUser.getUserId(), locationUrl, DateUtils.formatWithCurrentLocale(date));
		}
		
		userLastPageRepository.save(userLastPage);
		
		return true;
	}
	
}
