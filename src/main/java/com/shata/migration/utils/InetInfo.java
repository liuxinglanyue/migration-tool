package com.shata.migration.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InetInfo {
	private final static Logger log = LoggerFactory.getLogger(InetInfo.class);
	
	public final static String DEVICE_NAME = getDeviceName();

	public static String getHostIp() {
		Enumeration<NetworkInterface> netInterfaces = null;  
		try {  
		    netInterfaces = NetworkInterface.getNetworkInterfaces();  
		    while (netInterfaces.hasMoreElements()) {  
		        NetworkInterface ni = netInterfaces.nextElement();  
		        Enumeration<InetAddress> ips = ni.getInetAddresses();  
		        while (ips.hasMoreElements()) { 
		        	String ip = ips.nextElement().getHostAddress();
		        	if(-1 != ip.indexOf(":") || ip.startsWith("127") || ip.startsWith("169")) {
		        		continue;
		        	}
		        	return ip;
		        }  
		    }  
		} catch (Exception e) {  
		    log.error("获取本机ip错误！", e);
		} 
		return "127.0.0.1";
	}
	
	public static String getHostName() {
		String hostName; 
        try { 
             InetAddress addr = InetAddress.getLocalHost(); 
             hostName = addr.getHostName(); 
        }catch(Exception ex){ 
            hostName = ""; 
        } 
          
        return hostName; 
	}
	
	public static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();  
        String name = runtime.getName(); // format: "pid@hostname"  
        try {  
            return Integer.parseInt(name.substring(0, name.indexOf('@')));  
        } catch (Exception e) {  
            return -1;  
        }  
    }  
	
	private static String getDeviceName() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();  
        return runtime.getName() + "@" + getHostIp();
	}
	
	public static void main(String[] args) {
		System.out.println(getHostIp());
		System.out.println(getHostName());
		System.out.println(getPid());
		System.out.println(getDeviceName());
	}
}
