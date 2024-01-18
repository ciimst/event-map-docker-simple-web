package com.imst.event.map.web.security.keycloak;


import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.imst.event.map.web.security.UserItemDetails;

import java.security.Principal;
import java.util.Collection;


//public class KeycloakUserDetailsAuthenticationToken extends KeycloakAuthenticationToken
//{
//
//    private UserDetails userDetails;
//
//    public KeycloakUserDetailsAuthenticationToken(UserDetails userDetails, OidcKeycloakAccount account,
//            Collection<? extends GrantedAuthority> authorities, boolean interactive) {
//        super(account, interactive);
//        Assert.notNull(userDetails, "UserDetails required");
//        this.userDetails = userDetails;
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return userDetails;
//    }
//
//}


public class KeycloakUserDetailsAuthenticationToken extends KeycloakAuthenticationToken
{

    private UserItemDetails userDetails;
    
    private Principal principal;
    private boolean interactive;

    public KeycloakUserDetailsAuthenticationToken(UserItemDetails userDetails, OidcKeycloakAccount account,
            Collection<? extends GrantedAuthority> authorities, boolean interactive,  boolean isAuthenticated) {
    	
//        super(account, interactive);
    	 super(account, interactive, authorities);
        Assert.notNull(userDetails, "UserDetails required");
        this.userDetails = userDetails;
        
        
        
       
        Assert.notNull(account, "KeycloakAccount cannot be null");
        Assert.notNull(account.getPrincipal(), "KeycloakAccount.getPrincipal() cannot be null");
        this.principal = account.getPrincipal();
        this.setDetails(account);
        this.interactive = interactive;
        setAuthenticated(isAuthenticated);
        
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
    
    
//    public UserItemDetails getaaa() {
//    	return userDetails;
//    }

    

//    @Override
//    public Object getPrincipal() {
//        return principal;
//    }

    public OidcKeycloakAccount getAccount() {
        return (OidcKeycloakAccount) this.getDetails();
    }

    public boolean isInteractive() {
        return interactive;
    }
}