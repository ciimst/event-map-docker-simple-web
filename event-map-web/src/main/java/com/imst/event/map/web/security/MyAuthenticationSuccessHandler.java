package com.imst.event.map.web.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.imst.event.map.web.constant.LogTypeE;
import com.imst.event.map.web.services.DBLogger;
import com.imst.event.map.web.utils.ApplicationContextUtils;


@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, 
			HttpServletResponse response, Authentication authentication) throws IOException {


		HttpSession session = request.getSession(false);
		if (session != null) {

			UserItemDetails userItemDetails = (UserItemDetails) authentication.getDetails();	

			Map<String, Object> logMap = new HashMap<>();
			logMap.put("giriş", "harita web uygulaması");
			logMap.put("username", userItemDetails.getUsername());
			logMap.put("id", userItemDetails.getUserId());

			DBLogger dbLogger = ApplicationContextUtils.getBean(DBLogger.class);
			dbLogger.log(logMap, LogTypeE.LOGIN);
		}


		redirectStrategy.sendRedirect(request, response, "/home");
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

	}
}



