package com.intecon.docsign.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogCreator {
	
	private static String path;
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	private static LocalDateTime now;
	
	private static void setup() {
		
		path = "C:/Temp/logs/"+LocalDate.now().toString()+"/";
		File folder = new File(path);
		if(!folder.exists()){
			folder.mkdirs();
		}
		
		File file = new File(folder, "log.txt"); // put the file inside the folder
		 try {
			 if (file.createNewFile()) {
		        System.out.println("File created: " + file.getName());
		      } else {
		        System.out.println("File already exists.");
		      }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		now = LocalDateTime.now(); 
	}
	
	public static void error(String theLogText, String theClass) {
		setup();
		
		try(FileWriter fw = new FileWriter(path+"log.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(dtf.format(now)+" ["+ theClass+"]  ERROR: " +theLogText);
			} catch (IOException e) {
			    e.printStackTrace();
			}
	}
	
	public static void info(String theLogText, String theClass) {
		setup();
		try(FileWriter fw = new FileWriter(path+"log.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(dtf.format(now)+" ["+ theClass+"]  INFO: " +theLogText);
			} catch (IOException e) {
			    e.printStackTrace();
			}
	}
}
