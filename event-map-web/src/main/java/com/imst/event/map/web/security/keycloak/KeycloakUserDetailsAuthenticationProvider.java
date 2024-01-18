package com.imst.event.map.web.security.keycloak;

import java.util.Collection;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.imst.event.map.web.constant.AuthenticationExceptionE;
import com.imst.event.map.web.security.UserDetailsServiceImpl;
import com.imst.event.map.web.security.UserItemDetails;

@Service

public class KeycloakUserDetailsAuthenticationProvider implements AuthenticationProvider {
    private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Autowired
	private UserDetailsServiceImpl userDetailsService;
    
    public void setGrantedAuthoritiesMapper(GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
        this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        
	      if (token == null) {
	          return null;
	      }
      
      
      Assert.notNull(token, "KeycloakAuthenticationToken required");
      Assert.notNull(token.getAccount(), "KeycloakAuthenticationToken.getAccount() cannot be return null");
      OidcKeycloakAccount account = token.getAccount();
        
        String username = "";
      if (account.getPrincipal() instanceof KeycloakPrincipal) {
          KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) account.getPrincipal();
          
          username = kp.getKeycloakSecurityContext().getIdToken().getPreferredUsername();
      }
        UserItemDetails userItemDetails = userDetailsService.loadUserByUsernameKeycloak(username);
      
        if (userItemDetails  == null || !userItemDetails.isEnabled()) {
        	
        	throw new BadCredentialsException(AuthenticationExceptionE.AUTHENTICATION_FAILED.name());
		}
        
        Collection<? extends GrantedAuthority> authorities = userItemDetails.getAuthorities();
        userItemDetails.setPassword(null);

        KeycloakAuthenticationToken keycloakAuthenticationToken =  new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), mapAuthorities(authorities));
        return new KeycloakUserDetailsAuthenticationToken(userItemDetails, token.getAccount(), authorities, token.isInteractive(), keycloakAuthenticationToken.isAuthenticated());
    }

    private Collection<? extends GrantedAuthority> mapAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return grantedAuthoritiesMapper != null
            ? grantedAuthoritiesMapper.mapAuthorities(authorities)
            : authorities;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return KeycloakAuthenticationToken.class.isAssignableFrom(aClass);
    }
}


