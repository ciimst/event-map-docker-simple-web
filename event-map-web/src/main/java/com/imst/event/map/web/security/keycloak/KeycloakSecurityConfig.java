package com.imst.event.map.web.security.keycloak;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.keycloak.adapters.springsecurity.filter.QueryParamPresenceRequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

//import com.imst.event.map.admin.security.KeycloakUserDetailsAuthenticationProvider;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
@Order(2)
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

//	@Autowired
//	private BaseAuthenticationProvider baseAuthenticationProvider;
	
//	@Autowired
//	MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
	
	@Autowired private MyAuthenticationFailureHandler myAuthenticationFailureHandler;
	
	@Autowired
	private KeycloakUserDetailsAuthenticationProvider keycloakUserDetailsAuthenticationProvider;
	
	
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        
        http.requestMatchers().antMatchers("/sso/login");
        
        http
		.authorizeRequests()
			.antMatchers("fragments/**").permitAll()
			.antMatchers("layouts/**").permitAll()
			.antMatchers("page/**").permitAll()
//			.antMatchers("/login").permitAll()
			.antMatchers("/keycloaklogout").permitAll()
			.antMatchers("/css/**").permitAll()
			.antMatchers("/js/**").permitAll()
			.antMatchers("/image/static/**").permitAll()// Sadece static imagelar public olarak gösterilir
			.antMatchers("/template/**").permitAll()
			.antMatchers("/webfonts/**").permitAll()
			.antMatchers("/i18n/all").permitAll()
			.antMatchers("/temp/**").permitAll()// Sadece static imagelar public olarak gösterilir
			.antMatchers("/live").permitAll()
			.antMatchers("/ready").permitAll()			
//			.antMatchers("/test/**").permitAll()
			.anyRequest().authenticated();
	

	
	http.formLogin()
			.defaultSuccessUrl("/home", true)
//			.failureHandler(myAuthenticationSuccessHandler)
//			.loginPage("/login")
			//.successHandler(new MyAuthenticationSuccessHandler())
			.failureUrl("/error")
			.permitAll();
	
	http.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout**"))
			.deleteCookies("JSESSIONID", "SESSION")
			.invalidateHttpSession(true)//invalid urlye gitmesin diye false yapılcak
			.logoutSuccessHandler(new KeycloakMyLogoutSuccessHandler())
			
			.permitAll();
	
	http.exceptionHandling()
			.accessDeniedHandler(null)
			.accessDeniedPage("/denied");
	
	http.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
			.maximumSessions(1)
			.maxSessionsPreventsLogin(false)
			.expiredUrl("/expired?expired=true")
//			.and()
//			.sessionAuthenticationFailureHandler(myAuthenticationSuccessHandler)
			
	;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	
//        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
//        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
//        auth.authenticationProvider(keycloakAuthenticationProvider);
//        
        
        
//        keycloakUserDetailsAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    
    	
//    	auth.authenticationProvider(baseAuthenticationProvider);
        auth.authenticationProvider(keycloakUserDetailsAuthenticationProvider);
    	
      
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }
    
//    @Bean
//    protected AuthenticationFailureHandler myAuthenticationFailureHandler() {
//        return myAuthenticationFailureHandler;
//    }
    
    // necessary due to http://www.keycloak.org/docs/latest/securing_apps/index.html#avoid-double-filter-bean-registration
    @Bean
    public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(KeycloakAuthenticationProcessingFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
    // necessary due to http://www.keycloak.org/docs/latest/securing_apps/index.html#avoid-double-filter-bean-registration
    @Bean
    public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(KeycloakPreAuthActionsFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
    
    @Bean
    @Override
    protected KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter() throws Exception {
        RequestMatcher requestMatcher =
                new OrRequestMatcher(
                        new AntPathRequestMatcher("/sso/login"),
                        new QueryParamPresenceRequestMatcher(OAuth2Constants.ACCESS_TOKEN),
                        // We're providing our own authorization header matcher
                        new IgnoreKeycloakProcessingFilterRequestMatcher()
                );
        
        KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter = new KeycloakAuthenticationProcessingFilter(authenticationManagerBean(), requestMatcher);
        
        keycloakAuthenticationProcessingFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
        
        return keycloakAuthenticationProcessingFilter;
    }

    // Matches request with Authorization header which value doesn't start with "Basic " prefix
    private class IgnoreKeycloakProcessingFilterRequestMatcher implements RequestMatcher {
        IgnoreKeycloakProcessingFilterRequestMatcher() {
        }

        public boolean matches(HttpServletRequest request) {
            String authorizationHeaderValue = request.getHeader("Authorization");
            return authorizationHeaderValue != null && !authorizationHeaderValue.startsWith("Basic ");
        }
    }
    
    
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}

}