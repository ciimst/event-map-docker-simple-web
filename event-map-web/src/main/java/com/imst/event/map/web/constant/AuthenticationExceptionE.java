package com.imst.event.map.web.constant;


import java.util.HashMap;

public enum AuthenticationExceptionE {
	
	NONE, USERNAME_REQUIRED, PASSWORD_REQUIRED, USER_DISABLED, AUTHENTICATION_FAILED,
	DISABLED, LOCKED, DENIED, INVALID, EXPIRED, CREDENTIALS_EXPIRED, AUTH_FAILED, SERVER_ERROR;
	
	private static HashMap<String, AuthenticationExceptionE> map = new HashMap<>();
	
	
	public static String getMessage(AuthenticationExceptionE authExceptionE) {
		
		switch (authExceptionE) {
			case USER_DISABLED:
			case DISABLED:
			case LOCKED:
				return "Sisteme giriş yetkiniz bulunmuyor.";
			case CREDENTIALS_EXPIRED:
			case EXPIRED:
				return "Başka bir konumdan giriş yapıldığı için oturumunuz kapanmıştır.";
			case INVALID:
				return "Oturumunuz sonlandırılmıştır. Lütfen tekrar giriş yapınız.";
			case USERNAME_REQUIRED:
			case PASSWORD_REQUIRED:
			case AUTHENTICATION_FAILED:
			default:
				return "Kullanıcı adı veya şifre hatalı.";
		}
	}
	
	public String getMessage() {
		
		return getMessage(this);
	}
	
	public static String getMessage(String msg) {
		
		AuthenticationExceptionE authExceptionE = getAuthException(msg.toUpperCase());
		return getMessage(authExceptionE);
	}
	
	public static AuthenticationExceptionE getAuthException(String key) {
		return map.get(key) == null ? NONE : map.get(key);
	}
	
	static {
		
		for (AuthenticationExceptionE authenticationExceptionE : AuthenticationExceptionE.values()) {
			map.put(authenticationExceptionE.name(), authenticationExceptionE);
		}
	}
	
}
