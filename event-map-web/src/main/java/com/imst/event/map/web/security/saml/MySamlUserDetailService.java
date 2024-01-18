package com.imst.event.map.web.security.saml;

import org.opensaml.saml2.core.NameID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import com.imst.event.map.web.security.UserDetailsServiceImpl;
import com.imst.event.map.web.security.UserItemDetails;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MySamlUserDetailService  implements SAMLUserDetailsService{
	
	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Override
	public UserItemDetails loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
		
		NameID nameID = credential.getNameID();
		
		String username = nameID.getValue();
			
		try {
			
			UserItemDetails userItemDetails = userDetailsServiceImpl.loadUserByUsernameLdap(username);
			return userItemDetails;	
		} catch (Exception e) {
			log.catching(e);
		}
		return null;
	}
	
}
