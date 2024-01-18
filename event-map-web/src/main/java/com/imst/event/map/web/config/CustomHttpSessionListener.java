package com.imst.event.map.web.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.boot.convert.DurationUnit;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CustomHttpSessionListener implements HttpSessionListener {
	
	@DurationUnit(ChronoUnit.MINUTES)
	private Duration timeout = Duration.ofMinutes(60);
	
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		
		event.getSession().setMaxInactiveInterval(Math.toIntExact(timeout.getSeconds()));
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		
	}
}
