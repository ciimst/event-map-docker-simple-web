package com.imst.event.map.web.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class HeaderExtractorService {

	private String clientIpAddress;

	@Autowired
	public HeaderExtractorService(HttpServletRequest request) {

		this.clientIpAddress = request.getRemoteAddr();
	}

	public String getClientIpAddress() {
		
		return StringUtils.isEmpty(this.clientIpAddress) ? "" : this.clientIpAddress;
	}
}
