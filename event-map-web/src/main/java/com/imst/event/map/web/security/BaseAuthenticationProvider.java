package com.imst.event.map.web.security;


import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.constant.AuthenticationExceptionE;

@Service
public class BaseAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String rawPassword = (String) authentication.getCredentials();
		
		if (StringUtils.isEmpty(username)) {
			throw new BadCredentialsException(AuthenticationExceptionE.USERNAME_REQUIRED.name());
		}
		
		if (StringUtils.isEmpty(rawPassword)) {
			
			throw new BadCredentialsException(AuthenticationExceptionE.PASSWORD_REQUIRED.name());
		}
		UserItemDetails userItemDetails = userDetailsService.loadUserByUsername(username);
		if (!userItemDetails.isEnabled()) {
			throw new DisabledException(AuthenticationExceptionE.USER_DISABLED.name());
		}
		

		if (!passwordEncoder.matches(rawPassword, userItemDetails.getPassword())) {
			throw new BadCredentialsException(AuthenticationExceptionE.AUTHENTICATION_FAILED.name());
		}
		
		Collection<? extends GrantedAuthority> authorities = userItemDetails.getAuthorities();
		userItemDetails.setPassword(null); //Session'a gitmeden önce şifreyi temizle.
		
		return new UsernamePasswordAuthenticationToken(userItemDetails, null, authorities);
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
