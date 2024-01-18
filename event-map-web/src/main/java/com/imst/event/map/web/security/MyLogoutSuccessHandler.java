package com.imst.event.map.web.security;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.imst.event.map.web.constant.LogTypeE;
import com.imst.event.map.web.services.DBLogger;
import com.imst.event.map.web.utils.ApplicationContextUtils;

@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		
		HttpSession session = request.getSession();
		if (session != null) {

			try {
				UserItemDetails userItemDetails = (UserItemDetails) authentication.getPrincipal();		

				LinkedHashMap<String, Object> logMap = new LinkedHashMap<>();
				logMap.put("çıkış", "harita web uygulaması");
				logMap.put("id", userItemDetails.getUserId());
				logMap.put("username", userItemDetails.getUsername());

				DBLogger dbLogger = ApplicationContextUtils.getBean(DBLogger.class);
				dbLogger.logWithUser(userItemDetails, logMap, LogTypeE.LOGOUT);
	
			} catch (Exception e) {
				
				redirectStrategy.sendRedirect(request, response, "/login?error");
				return;
			}
		}

		redirectStrategy.sendRedirect(request, response, "/login");
	}
}



