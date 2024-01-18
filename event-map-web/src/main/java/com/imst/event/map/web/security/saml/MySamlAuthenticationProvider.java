//package com.imst.event.map.web.security.saml;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.saml.SAMLAuthenticationProvider;
//import org.springframework.security.saml.SAMLCredential;
//import org.springframework.security.saml.context.SAMLMessageContext;
//import org.springframework.security.saml.log.SAMLLogger;
//import org.springframework.stereotype.Service;
//
//import lombok.extern.log4j.Log4j2;
//
//@Service
//@Log4j2
//public class MySamlAuthenticationProvider extends SAMLAuthenticationProvider {
//
//	@Bean
//	public SAMLLogger provideSamlLogger() {
//		
//		SAMLLogger samlLogger = new SAMLLogger() {
//			
//			@Override
//			public void log(String operation, String result, SAMLMessageContext context, Authentication a, Exception e) {
//				log.debug(result);
//			}
//			
//			@Override
//			public void log(String operation, String result, SAMLMessageContext context, Exception e) {
//				log.debug(result);
//			}
//			
//			@Override
//			public void log(String operation, String result, SAMLMessageContext context) {
//				log.debug(result);
//			}
//		};
//		
//		return samlLogger;
//	}
//	
//	@Override
//	protected Object getUserDetails(SAMLCredential credential) {
//		return super.getUserDetails(credential);
//	}
//	
//	@Override
//    public boolean isForcePrincipalAsString() {
//        return false;
//    }
//	
//}
