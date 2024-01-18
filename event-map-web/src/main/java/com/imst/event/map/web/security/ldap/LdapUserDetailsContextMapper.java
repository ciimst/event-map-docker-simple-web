package com.imst.event.map.web.security.ldap;


import java.util.Collection;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import com.imst.event.map.web.security.UserDetailsServiceImpl;
import com.imst.event.map.web.security.UserItemDetails;
import com.imst.event.map.web.utils.ApplicationContextUtils;




public class LdapUserDetailsContextMapper extends LdapUserDetailsMapper{
	
	
	@SuppressWarnings("unchecked")
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
		
		UserDetails mapUserFromContext = super.mapUserFromContext(ctx, username, authorities);
		Collection<GrantedAuthority> userAuthorities =  (Collection<GrantedAuthority>)mapUserFromContext.getAuthorities();
		if (Validate.isNullOrEmpty(userAuthorities) || Validate.isNullOrEmpty(mapUserFromContext.getUsername())) {
			throw new BadCredentialsException("authenticationFailed");
		}
		
		UserDetailsServiceImpl userDetailsServiceImpl = ApplicationContextUtils.getBean(UserDetailsServiceImpl.class);
		
		UserItemDetails loadedUserItemDetails = userDetailsServiceImpl.loadUserByUsernameLdap(mapUserFromContext.getUsername());
		
		return loadedUserItemDetails;
	}
	
	
}