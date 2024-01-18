package com.imst.event.map.web.utils;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class IPUtils {

	private static List<String> ipAddresses = null;
	private static String ipAddress = "";
	public static List<String> getIpAddresses(){

		if(ipAddresses == null){
			
			ipAddresses = new ArrayList<>();
			try {
				
				
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
				while(networkInterfaces.hasMoreElements()){
					
					NetworkInterface nextElement = networkInterfaces.nextElement();
					if(nextElement.isUp()){
						
						List<InterfaceAddress> interfaceAddresses = nextElement.getInterfaceAddresses();
						for (InterfaceAddress interfaceAddress : interfaceAddresses) {
							
							String addr = interfaceAddress.getAddress().getHostAddress();
							if(addr.contains(":")) continue;
							ipAddresses.add(addr);
						}
					}
				}
			} catch (Exception e) {
				
				log.debug(e);
			}
		}
		
		return ipAddresses;
	}
	
	public static boolean isIpContaining(String ip) {
		
		List<String> ipAddressList = getIpAddresses();
		return ipAddressList.contains(ip);
	}
	
	public static String getIpAddress() {
		
		if(ipAddress.equals("")) {
			
			return ipAddress;
		}
		
		try {
			
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			
			System.out.println(networkInterfaces.hasMoreElements());
			while(networkInterfaces.hasMoreElements()){
				NetworkInterface nextElement = networkInterfaces.nextElement();
				if(nextElement.isUp() && !nextElement.getName().equals("lo")){
					
					List<InterfaceAddress> interfaceAddresses = nextElement.getInterfaceAddresses();
					ipAddress = interfaceAddresses.get(0).getAddress().getHostAddress();
					
					break;
				}
				
				

			}
			
		}catch (Exception e) {
			
			log.debug(e);
		}
		
		return ipAddress;
	}
	
	
	

}
