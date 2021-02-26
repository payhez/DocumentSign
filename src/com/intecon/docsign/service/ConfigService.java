package com.intecon.docsign.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
}