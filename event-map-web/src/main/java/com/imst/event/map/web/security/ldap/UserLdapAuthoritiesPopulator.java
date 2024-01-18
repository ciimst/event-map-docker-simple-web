package com.imst.event.map.web.security.ldap;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.web.db.repositories.ProfileRepository;
import com.imst.event.map.web.db.repositories.UserRepository;
import com.imst.event.map.web.services.ProfilePermissionService;
import com.imst.event.map.web.vo.ProfilePermissionItem;

@Component
@ConfigurationProperties(prefix="ldap.user-search")
public class UserLdapAuthoritiesPopulator {
	
	
	public static final String DEFAULT_USER_ROLE = "DEFAULT_ROLE_USED_FOR_NOTHING";
	
	@Autowired
	private LdapContextSource contextSource;
	
	@Autowired ProfileRepository profileRepository;
	
	@Autowired UserRepository userRepository;
	
	@Autowired ProfilePermissionService profilePermissionService;
	
	
	
	private String base;
	private String filter;
	private String defaultRole;
	private Boolean searchSubtree;
	private Boolean resultException;
	
	public Populator build() {
		
		Populator populator = new Populator(contextSource, base);
		populator.setDefaultRole(DEFAULT_USER_ROLE);
		populator.setSearchSubtree(searchSubtree);
		populator.setIgnorePartialResultException(resultException);
		return populator;
	}
	
	public class Populator  extends DefaultLdapAuthoritiesPopulator {
	
		public Populator(ContextSource contextSource, String groupSearchBase) {
			super(contextSource, groupSearchBase);
		}
		
		
		@Override
		public Set<GrantedAuthority> getGroupMembershipRoles(String userDn, String username) {
			
			Set<GrantedAuthority> groupMembershipRoles = super.getGroupMembershipRoles(userDn, username);
			return groupMembershipRoles;
		}
		
		@Override
		protected Set<GrantedAuthority> getAdditionalRoles(DirContextOperations userData, String username) {
			
			Set<GrantedAuthority> roles = new HashSet<>();
			
			User user = userRepository.findOneByUsername(username);
			if(user == null) {

				throw new UsernameNotFoundException("Kullanıcı adı veya şifre yanlış");
			}else {
				
				if(!user.getState()) {
					throw new UsernameNotFoundException("Kullanıcı adı veya şifre yanlış");
				}
				
				
				List<ProfilePermissionItem> profilePermissionList = profilePermissionService.findByProfileProjectedByPermission(user.getProfile().getId());
				for (ProfilePermissionItem profilePermission : profilePermissionList) {
					

					roles.add(new SimpleGrantedAuthority(profilePermission.getPermissionName()));
				}										
				
				roles.add(new SimpleGrantedAuthority("DEFAULT_ROLE_USED_FOR_NOTHING"));
			}
			

			return roles;
		}
		
		
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
