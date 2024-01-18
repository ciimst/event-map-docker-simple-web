package com.imst.event.map.web.security.ldap;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.stereotype.Component;

import com.imst.event.map.web.security.ldap.UserLdapAuthoritiesPopulator.Populator;



@Component
@ConfigurationProperties(prefix="ldap.user-search")
public class LdapUserSearchConfig {


	@Autowired
	UserLdapAuthoritiesPopulator basePopulator;
	
	private String base;
	private String filter;
	private String defaultRole;
	private Boolean searchSubtree;
	private Boolean resultException;

	public LdapAuthenticationProvider getProvider(LdapContextSource contextSource) {

		Populator populator = basePopulator.build();		
		FilterBasedLdapUserSearch ldapUserSearch = new FilterBasedLdapUserSearch(base, filter, contextSource);

		BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource);
		bindAuthenticator.setUserSearch(ldapUserSearch);
		LdapAuthenticationProvider provider = new LdapAuthenticationProvider(bindAuthenticator, populator);
		provider.setUserDetailsContextMapper(new LdapUserDetailsContextMapper());
		
		
		return provider;
	}

	public String getBase() {

		return base;
	}

	public void setBase(String base) {

		this.base = base;
	}

	public String getFilter() {

		return filter;
	}

	public void setFilter(String filter) {

		this.filter = filter;
	}

	public String getDefaultRole() {

		return defaultRole;
	}

	public void setDefaultRole(String defaultRole) {

		this.defaultRole = defaultRole;
	}

	public Boolean getSearchSubtree() {

		return searchSubtree;
	}

	public void setSearchSubtree(Boolean searchSubtree) {

		this.searchSubtree = searchSubtree;
	}

	public Boolean getResultException() {

		return resultException;
	}

	public void setResultException(Boolean resultException) {

		this.resultException = resultException;
	}
}
