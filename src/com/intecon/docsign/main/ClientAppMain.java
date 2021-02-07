package com.intecon.docsign.main;

import java.io.File;

import com.intecon.docsign.log.LogCreator;
import com.intecon.socket.client.SocketClient;

public class ClientAppMain {
	private static String SIGNED_URL = "C:/Temp/Signed/";
	private static String UNSIGNED_URL = "C:/Temp/UnSigned/";
	 
	  public static void main(String args[]) {
		  
		  int port = 8080;

		  File theDir = new File(SIGNED_URL);    
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
			theDir = new File(UNSIGNED_URL);
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
		  clearUnsignedFolder(); // Deletes unnecessary folders in the opening
		  //String url = "ws://localhost:{port}/DocumentSigningService/gs-guide-websocket";
		  String url = "ws://10.0.0.68:{port}/demo/gs-guide-websocket";
		  
		  try {
			  AppRunner.runApp();
			  SocketClient sc = new SocketClient();
			  sc.setup(url, port,"furat");
			  sc.start();
			  sc.getSession().send("/app/registration/furat", "FURAT");
			  LogCreator.info("Connected!", ClientAppMain.class.getName());

			} catch (Exception e) {
				LogCreator.error("Not connected! " +e.toString(), ClientAppMain.class.getName());
			}
	  }
	  
	  private static void clearUnsignedFolder() {
		File path = new File(UNSIGNED_URL);
		if(path.exists()) {
			 for (final File dateFolder : path.listFiles()) {
				if(dateFolder.isDirectory() && dateFolder.list().length == 0) {
					dateFolder.delete();
				}
			 }
		 }
	  }
}