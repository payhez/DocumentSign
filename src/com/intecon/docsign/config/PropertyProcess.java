package com.intecon.docsign.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyProcess {
	
	private Properties prop = new Properties();
	private String macId;
	private String username;
	private String password;
	
	
	private Properties read() {
		
		try (FileInputStream ip = new FileInputStream("config.properties")) {
			prop.load(ip);
		} catch (IOException io) {
            io.printStackTrace();
        }
		
		return prop;
	}
	
	public boolean write(String macId, String username, String password) {
		
		try (OutputStream output = new FileOutputStream("config.properties")) {
            // set the properties value
            prop.setProperty("MACID", macId);
            prop.setProperty("USERNAME", username);
            prop.setProperty("PASSWORD", password);
 
            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
            return false;
        }
		return true;
	}

	public String getMacId() {
		
		return read().getProperty("MACID");
	}


	public String getUsername() {
		return read().getProperty("USERNAME");
	}


	public String getPassword() {
		return read().getProperty("PASSWORD");
	}
	
}