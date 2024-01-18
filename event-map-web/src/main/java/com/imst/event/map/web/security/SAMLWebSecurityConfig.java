//package com.imst.event.map.web.security;
//
//import java.util.Collections;
//import java.util.Properties;
//
//import org.opensaml.xml.signature.SignatureConstants;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.Resource;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.saml.SAMLBootstrap;
//
//import com.github.choonchernlim.security.adfs.saml2.CustomAuthnContext;
//import com.github.choonchernlim.security.adfs.saml2.DefaultSAMLBootstrap;
//import com.github.choonchernlim.security.adfs.saml2.SAMLConfigBean;
//import com.github.choonchernlim.security.adfs.saml2.SAMLConfigBeanBuilder;
//import com.github.choonchernlim.security.adfs.saml2.SAMLWebSecurityConfigurerAdapter;
//import com.imst.event.map.web.constant.Config;
//import com.imst.event.map.web.security.saml.MySamlUserDetailService;
//import com.imst.event.map.web.utils.DatabasePasswordEncryptUtils;
//import com.imst.event.map.web.utils.PropertiesUtil;
//
//import lombok.extern.log4j.Log4j2;
//
//// Create a Java-based Spring configuration that extends SAMLWebSecurityConfigurerAdapter.
//@Configuration
//@EnableWebSecurity
//@Log4j2
//public class SAMLWebSecurityConfig extends SAMLWebSecurityConfigurerAdapter {
//
//	@Autowired
//	MySamlUserDetailService mySamlUserDetailService;
//	
//	
//    // See `SAMLConfigBean Properties` section below for more info. 
//    @Override    
//    protected SAMLConfigBean samlConfigBean() {
//    	
//    	// Bu esnada spring conponentleri inject olmadığından application properties dosyası manuel okunmak durumunda
//    	log.info("applicationPropertiesLocation: "+ Config.applicationPropertiesLocation);
//    	Properties prop = PropertiesUtil.readPropertiesFile();
//	    	
//		
//		String samlKeystoreLocation = prop.getProperty("saml.keystore.location");
//		String samlIdpServerName = prop.getProperty("saml.idp.server.name");
//		String samlSpServerName = prop.getProperty("saml.sp.server.name");
//		String samlSpContextPath = prop.getProperty("server.servlet.context-path");
//		Integer samlSpHttpsPort = Integer.parseInt(prop.getProperty("server.port"));
//		String samlKeystorePassword = DatabasePasswordEncryptUtils.decriptedPasswordFromApplicationProperties(prop.getProperty("saml.keystore.password"));
//		String samlKeystoreAlias = prop.getProperty("saml.keystore.alias");
//		String samlKeystorePrivatekeyPassword = DatabasePasswordEncryptUtils.decriptedPasswordFromApplicationProperties(prop.getProperty("saml.keystore.privatekey.password"));
//		
//		
//    	Resource cacertResource = PropertiesUtil.getResource(samlKeystoreLocation);
//
//    	
//        return new SAMLConfigBeanBuilder()
//                .withIdpServerName(samlIdpServerName) // idp server adresi
//                .withSpServerName(samlSpServerName) // Bu uygulamanın çalıştığı domain
//                .withSpHttpsPort(samlSpHttpsPort) // bu uygulamanın çalıştığı port
//                .withSpContextPath(samlSpContextPath) // context path buraya gelecek
//                //.withKeystoreResource(new DefaultResourceLoader().getResource("classpath:cacerts.jks")) // sertifikaların bulunduğu dosya                
//                .withKeystoreResource(cacertResource) // sertifikaların bulunduğu dosya
//                .withKeystorePassword(samlKeystorePassword) // cacerts.jks dosyası için kullanılan şifre
//                .withKeystoreAlias(samlKeystoreAlias) // cacerts.jks dosyası içerisinde kendi oluşturduğumuz sertifikanın alias ismi
//                .withKeystorePrivateKeyPassword(samlKeystorePrivatekeyPassword)
//                .withSuccessLoginDefaultUrl("/") // samlda login olduğumuzda yönlenecek sayfa
//                .withSuccessLogoutUrl("/logout") // samlda logout olduğumuzda yönlenecek sayfa
//                .withStoreCsrfTokenInCookie(true)
//                .withSamlUserDetailsService(mySamlUserDetailService)
////                .withUseJdkCacertsForSslVerification(true)
//                .withAuthnContexts((Collections.singleton(CustomAuthnContext.WINDOWS_INTEGRATED_AUTHN_CTX)))// Bu parametre ile credential ekranı sürekli açılmamış oluyor. browser kapansada kendi kendine girebiliyor.
//                .build();
//    }
//    
//
//    // This configuration is not needed if your signature algorithm is SHA256withRSA and 
//    // digest algorithm is SHA-256. However, if you are using different algorithm(s), then
//    // add this bean with the correct algorithms.
//    @Bean
//    public static SAMLBootstrap samlBootstrap() {
//    	
//    	
//    	Properties prop = PropertiesUtil.readPropertiesFile();
//		String samlKeystoreEncryptionMethod = prop.getProperty("saml.keystore.encryption.method").trim();
//		
//		
//		DefaultSAMLBootstrap defaultSAMLBootstrap = new DefaultSAMLBootstrap("RSA",
//                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256,
//                SignatureConstants.ALGO_ID_DIGEST_SHA256);
//		
//		switch (samlKeystoreEncryptionMethod) {
//		case "SHA1":
//			defaultSAMLBootstrap = new DefaultSAMLBootstrap("RSA",
//	                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1,
//	                SignatureConstants.ALGO_ID_DIGEST_SHA1);
//			break;
//		case "SHA384":
//			defaultSAMLBootstrap = new DefaultSAMLBootstrap("RSA",
//	                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384,
//	                SignatureConstants.ALGO_ID_DIGEST_SHA384);
//			break;
//		case "SHA512":
//			defaultSAMLBootstrap = new DefaultSAMLBootstrap("RSA",
//	                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512,
//	                SignatureConstants.ALGO_ID_DIGEST_SHA512);
//			break;
//
//		}
//		
//		
//        return defaultSAMLBootstrap;
//    }
//
//    // call `samlizedConfig(http)` first to decorate `http` with SAML configuration
//    // before configuring app specific HTTP security
//    @Override
//    protected void configure(final HttpSecurity http2) throws Exception {
//    	HttpSecurity http = samlizedConfig(http2);
//    	
////        samlizedConfig(http)
//    	http
//				.authorizeRequests()
//				.antMatchers("fragments/**").permitAll()
//				.antMatchers("/live").permitAll()
//				.antMatchers("/ready").permitAll()
//				.antMatchers("layouts/**").permitAll()
//				.antMatchers("page/**").permitAll()
//				.antMatchers("/login").permitAll()
//		//		.antMatchers("/list").permitAll()
//				.antMatchers("/css/**").permitAll()
//				.antMatchers("/js/**").permitAll()
//				.antMatchers("/image/static/**").permitAll()// Sadece static imagelar public olarak gösterilir
//				.antMatchers("/image/**").permitAll()// Sadece static imagelar public olarak gösterilir
//				.antMatchers("/template/**").permitAll()
//				.antMatchers("/webfonts/**").permitAll()
//				.antMatchers("/i18n/all").permitAll()
//				.antMatchers("/temp/**").permitAll()// Sadece static imagelar public olarak gösterilir
//		//		.antMatchers("/test/**").permitAll()
//		//		.antMatchers("admin/user-list").hasAnyRole("ROLE_USER_LIST")
//				.anyRequest().authenticated();
//        
//    }
//    
//
//    // call `samlizedConfig(web)` first to decorate `web` with SAML configuration 
//    // before configuring app specific web security
//    @Override
//    public void configure(final WebSecurity web) throws Exception {
//        samlizedConfig(web).ignoring().antMatchers("/resources/**");
//    }
//    
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		
//		return new BCryptPasswordEncoder();
//	}
//}