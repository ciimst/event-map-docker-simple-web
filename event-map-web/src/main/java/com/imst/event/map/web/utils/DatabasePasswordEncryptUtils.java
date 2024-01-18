package com.imst.event.map.web.utils;

import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;


public class DatabasePasswordEncryptUtils {
	
	
	public static String encryptedPassword(String password) {

		Properties prop = PropertiesUtil.readPropertiesFile();
		String encryptionKey = prop.getProperty("jasypt.encryptor.password").trim();
		
	    return encryptedPassword(password, encryptionKey);
	}
	
	private static String encryptedPassword(String password, String encryptionKey) {

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	    encryptor.setPassword(encryptionKey);
	    encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
		encryptor.setIvGenerator(new RandomIvGenerator());
		
		password = encryptor.encrypt(password);
	    return password;
	}
	
	public static String decriptedPasswordFromApplicationProperties(String password) {
		
		Properties prop = PropertiesUtil.readPropertiesFile();
		String encryptionKey = prop.getProperty("jasypt.encryptor.password").trim();
		
		return decriptedPasswordFromApplicationProperties(password, encryptionKey);
	}
	
	private static String decriptedPasswordFromApplicationProperties(String password, String encryptionKey) {
			
		if(password.startsWith("ENC(")) {
			
			password = password.substring(4,password.length()-1);
		
			return decriptedPassword(password, encryptionKey);
			
		}else {
			return password;
		}
		
	}
	
	
	public static String decriptedPassword(String password) {
		
		Properties prop = PropertiesUtil.readPropertiesFile();
		String encryptionKey = prop.getProperty("jasypt.encryptor.password").trim();
		
		return decriptedPassword(password, encryptionKey);
	}
	
	private static String decriptedPassword(String password, String encryptionKey) {
			
			
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(encryptionKey);
		encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
		encryptor.setIvGenerator(new RandomIvGenerator());
		
		password = encryptor.decrypt(password);
		return password;
	}
}
