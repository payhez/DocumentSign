package com.intecon.docsign.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Properties;

import com.intecon.log.LogCreator;

public class ConfigService {
	
	private static Properties prop = new Properties();
	
	private static Properties read() {
		
		try (FileInputStream ip = new FileInputStream("C:/Temp/Resources/config.properties")) {
			prop.load(ip);
		} catch (IOException io) {
            io.printStackTrace();
        }
		
		return prop;
	}
	public static String getDateTimeStyle() {
		return read().getProperty("DATETIME-STYLE");
	}
	public static String getVersion() {
		return read().getProperty("VERSION");
	}
	public static String getServerIp() {
		return read().getProperty("SERVER-IP");
	}
	public static String getServerPort() {
		return read().getProperty("SERVER-PORT");
	}
	public static String getSignedPath() {
		return read().getProperty("SIGNED-PATH");
	}
	public static String getUnsignedPath() {
		return read().getProperty("UNSIGNED-PATH");
	}
	public static String getLogPath() {
		return read().getProperty("LOG-PATH");
	}
	public static  String getResoucesPath() {
		return read().getProperty("RESOURCES-PATH");
	}
	public static String getDeleteOption() { // true if it is being deleted, path name if otherwise
		return read().getProperty("UNSIGNED-DELETE-OPTION");
	}
	public static String getDeletePath() {
		return read().getProperty("DELETE-PATH");
	}
	public static String getSmallIconPath() {
		return read().getProperty("SMALL-ICON-PATH");
	}
	public static String getConfigFilePath() {
		return read().getProperty("CONFIG-FILE-PATH");
	}
	
	public static String getMacId() {
		String macId = null;
		
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
			macId = ni.getHardwareAddress().toString();
			macId= "furat";
		}catch(Exception e) {
			LogCreator.error("Couldn't get MAC Address due to: "+e.toString(), ConfigService.class.getName());
		}
		
		return macId;
	}
}