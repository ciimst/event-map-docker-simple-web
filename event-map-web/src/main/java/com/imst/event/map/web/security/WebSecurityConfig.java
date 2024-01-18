package com.imst.event.map.web.security;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

import com.imst.event.map.web.EventMapWebApplication;
import com.imst.event.map.web.security.ldap.LdapUserSearchConfig;

@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(3)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private BaseAuthenticationProvider baseAuthenticationProvider;
	
	@Autowired
	LdapUserSearchConfig ldapUserSearchConfig;
	
	@Autowired
	MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
	
	
	@Bean
	public SpringSecurityDialect securityDialect() {
		return new SpringSecurityDialect();
	}
	
	@Bean
	@ConfigurationProperties(prefix="ldap.context-source")
	public LdapContextSource contextSource() {
		LdapContextSource contextSource = new LdapContextSource();
		return contextSource;
	}


	@Bean
	public AuthenticationProvider ldapAuthenticationProvider() throws Exception {

		LdapContextSource contextSource = contextSource();
		return ldapUserSearchConfig.getProvider(contextSource);
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.requestMatchers().anyRequest();
		
		http
			.authorizeRequests()
				.antMatchers("fragments/**").permitAll()
				.antMatchers("/live").permitAll()
				.antMatchers("/ready").permitAll()
				.antMatchers("layouts/**").permitAll()
				.antMatchers("page/**").permitAll()
//				.antMatchers("/login").permitAll()
//				.antMatchers("/sso/login").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/js/**").permitAll()
				.antMatchers("/image/static/**").permitAll()// Sadece static imagelar public olarak gösterilir
				.antMatchers("/template/**").permitAll()
				.antMatchers("/webfonts/**").permitAll()
				.antMatchers("/i18n/all").permitAll()
				.antMatchers("/temp/**").permitAll()// Sadece static imagelar public olarak gösterilir
//				.antMatchers("/test/**").permitAll()
				.anyRequest().authenticated();
		
		
		
		http.formLogin()
				.defaultSuccessUrl("/home", true)
				.loginPage("/login")
				//.successHandler(new MyAuthenticationSuccessHandler())
				.failureUrl("/login?error")
				.permitAll();
		
		http.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout**"))
				.deleteCookies("JSESSIONID", "SESSION")
				.invalidateHttpSession(true)//invalid urlye gitmesin diye false yapılcak
				.logoutSuccessHandler(new MyLogoutSuccessHandler())
				.permitAll();
		
		http.exceptionHandling()
				.accessDeniedPage("/denied");
		
		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				.maximumSessions(1)
				.maxSessionsPreventsLogin(false)
				.expiredUrl("/expired?expired=true")
//				.and()
//				.sessionAuthenticationFailureHandler(myAuthenticationSuccessHandler)
				
		;
	}
	
	// Güvenilir olmayan sertifikayı proje bazlı tanıtmak için kullanılır
//    @Bean
//    public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider()
//            throws IOException {
//        
//    	InputStream inputStream = null;
//    	
//    	try {
//    		
//            File jks = File.createTempFile("cacerts", "jks");
//            jks.deleteOnExit();
//            
//            try {
//            	
//            	//URL location = getClass().getProtectionDomain().getCodeSource().getLocation().toString();
//            	
//                
//                ApplicationHome home = new ApplicationHome(EventMapWebApplication.class);
//                home.getDir();    // returns the folder where the jar is. This is what I wanted.
//                
//                System.out.println("home.getDir(); : " + home.getDir() + "\\cacerts.jks");
//                
//                //inputStream = new FileInputStream(home.getDir() + "\\cacerts.jks");
//                inputStream = new FileInputStream("C:/cacerts.jks");
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    			 inputStream = new DefaultResourceLoader().getResource("classpath:cacerts.jks").getInputStream();
//    		}
//            
//            try (InputStream fromJks = inputStream) {
//                FileCopyUtils.copy(FileCopyUtils.copyToByteArray(fromJks), jks);
//            }
//
//            System.setProperty("javax.net.ssl.trustStore", jks.getPath());
//            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//		} catch (Exception e) {
//
//		} finally {
//			if (inputStream != null) {
//				inputStream.close();
//			}
//		}
//    	
//
//
//        ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider = 
//                new ActiveDirectoryLdapAuthenticationProvider("imst.local", "ldaps://177.177.1.25:636");
//        
//        return activeDirectoryLdapAuthenticationProvider;
//    }
    
    
//@Bean
//public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider()
//        throws IOException {
//    
//    File jks = File.createTempFile("cacerts", "jks");
//    jks.deleteOnExit();
//    
//    try (InputStream fromJks = WebSecurityConfig.class.getResource("/cacerts.jks").openStream()) {
//        FileCopyUtils.copy(FileCopyUtils.copyToByteArray(fromJks), jks);
//    }
//
//    System.setProperty("javax.net.ssl.trustStore", jks.getPath());
//    System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//
//    ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider = 
//            new ActiveDirectoryLdapAuthenticationProvider("imst.local", "ldaps://winsrv1.imst.local:636/");
//    
//    return activeDirectoryLdapAuthenticationProvider;
//}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.authenticationProvider(baseAuthenticationProvider);
//		auth.authenticationProvider(ldapAuthenticationProvider());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
}