package com.intecon.docsign.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Base64;

import com.google.gson.Gson;
import com.intecon.docsign.log.LogCreator;
import com.intecon.socket.client.SocketClient;

public class ClientAppMain {
	private static String SIGNED_URL = "C:/Temp/Signed/";
	private static String UNSIGNED_URL = "C:/Temp/UnSigned/";
	private static String macId="";
	
	public static String getMacId() {
		return macId;
	}

	public static void setMacId(String macId) {
		ClientAppMain.macId = macId;
	}

	public static void main(String args[]) {
		  int port = 8080;
		  //String macId = "";
		  String url = "ws://10.0.0.68:{port}/DocumentSignService/gs-guide-websocket";
		  try {
				InetAddress localHost = InetAddress.getLocalHost();
				NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
				macId = ni.getHardwareAddress().toString();
			}catch(Exception e) {
				LogCreator.error("Couldn't get MAC Address due to: "+e.toString(), ClientAppMain.class.getName());
			}

		  File theDir = new File(SIGNED_URL);    
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
			theDir = new File(UNSIGNED_URL);
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
		  clearUnsignedFolder(); // Deletes unnecessary folders in the opening
		  
		  try {
			  AppRunner.runApp();
			  SocketClient sc = new SocketClient();
			  sc.setup(url, port,macId);
			  sc.start();
			  sc.getSession().send("/app/registration/"+macId, "intecon");
			  LogCreator.info("Connected!", ClientAppMain.class.getName());
			  checkUnsignedDocumentsFromServer();
			} catch (Exception e) {
				LogCreator.error("Not connected due to:" +e.toString(), ClientAppMain.class.getName());
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
	  
	  
	  
	  private static void  checkUnsignedDocumentsFromServer() {
		  File path = new File(UNSIGNED_URL);
		  if(path.exists()) {
				 for (final File dateFolder : path.listFiles()) {
					if(dateFolder.isDirectory()) {
						for(final File document : dateFolder.listFiles()) {
							String trid = trimTrid(document.getName());
							try {
								DocumentModel theDocument = getDocumentByTrid(trid);
								if(!theDocument.isSignedFlag()) {
									
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				 }
			 }
	  }
	  private static final String USER_AGENT = "Mozilla/5.0";
	    
	  private static final String GET_URL = "http://10.0.0.68:{port}/DocumentSignService/documentController/getDocumentModelFromClientByTrid/";
	  private static DocumentModel getDocumentByTrid(String trid) throws IOException {
	 		
	 		Gson gson = new Gson();
	 		URL url = new URL(GET_URL+trid);
	 		HttpURLConnection con = (HttpURLConnection) url.openConnection();
	 		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	 		con.setRequestProperty("Accept", "application/json");
	 		con.setRequestProperty("User-Agent", USER_AGENT);
	 		con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			System.out.println("GET Response Code :: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				DocumentModel responseDoc = null;
				try {
					responseDoc = gson.fromJson(response.toString(), DocumentModel.class);
				} catch(Exception e) {
					e.printStackTrace();
				}
				return responseDoc;
			} else {
				LogCreator.error("getDocumentModelFromClientByTrid did not work!", ClientAppMain.class.getName());
				return null;
			}
	 	}
	  
	  private static String trimTrid(String docName) {
			String tridWithExt = docName.split("_")[1];
			String trid = "";
			if (tridWithExt.indexOf(".") > 0)
				trid = tridWithExt.substring(0, tridWithExt.lastIndexOf("."));
			return trid;
		}
}