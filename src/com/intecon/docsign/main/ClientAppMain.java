package com.intecon.docsign.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intecon.docsign.log.LogCreator;
import com.intecon.socket.client.SocketClient;

public class ClientAppMain {
	private static String SIGNED_URL = "C:/Temp/Signed/";
	private static String UNSIGNED_URL = "C:/Temp/UnSigned/";
	private static String macId= "furat";
	
	public static String getMacId() {
		return macId;
	}

	public static void setMacId(String macId) {
		ClientAppMain.macId = macId;
	}

	public static void main(String args[]) {
		  //String url = "ws://localhost:{port}/DocumentSigningService/gs-guide-websocket";
		  String url = "ws://"+SocketClient.ipAddress+":"+SocketClient.port+"/DocumentSignService/gs-guide-websocket";
		  try {
				InetAddress localHost = InetAddress.getLocalHost();
				NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
				macId = ni.getHardwareAddress().toString();
				LogCreator.info("MacId: "+macId , ClientAppMain.class.getName());
				macId= "furat";
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
			  sc.setup(url, macId);
			  sc.start();
			  sc.getSession().send("/app/registration/"+macId, "aa11");
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
		  List<String> tridList = new ArrayList<String>();
		  if(path.exists()) {
			 for (final File dateFolder : path.listFiles()) {
				if(dateFolder.isDirectory()) {
					for(final File document : dateFolder.listFiles()) {
						String trid = trimTrid(document.getName());
						if(trid != null) {
							tridList.add(trid);
						}
					}
				}
			 }
		  }
		  try {
			  	List<DocumentModel> docList = getUnsignedDocuments();
				for(DocumentModel doc : docList) {
					boolean found = false;
					for(String theTrid : tridList) {
						if(doc.getTrid().equals(theTrid)) {
							found = true;
						}
					}
					if(!found) {
						SocketClient.convertBytesToFile(SocketClient.getBytesByTrid(doc.getTrid()),doc.getName() + "_" + doc.getTrid());
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LogCreator.error("checkUnsignedDocumentsFromServer did not work due to: " + e.toString(), ClientAppMain.class.getName());
			}
		  
	  }
	  private static final String USER_AGENT = "Mozilla/5.0";
	  static String signed = "false";
	  //private static final String GET_URL = "http://localhost:8080/DocumentSigningService/documentController/getDocumentModelFromClient/"+macId+"/"+signed;
	  private static final String GET_URL = "http://"+SocketClient.ipAddress+":"+SocketClient.port+"/DocumentSignService/documentController/getDocumentModelFromClient/"+macId+"/"+signed;
	  private static List<DocumentModel> getUnsignedDocuments() throws IOException {
	 		
	 		Gson gson = new Gson();
	 		URL url = new URL(GET_URL);
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
				List<DocumentModel> documentList = new ArrayList<DocumentModel>();
				try {
					documentList = gson.fromJson(response.toString(), new TypeToken<List<DocumentModel>>(){}.getType());
				} catch(Exception e) {
					e.printStackTrace();
				}
				return documentList;
			} else {
				LogCreator.error("getUnsignedDocuments did not work!", ClientAppMain.class.getName());
				return null;
			}
	 	}
	  
	  private static String trimTrid(String docName) {
		  if(docName.indexOf("_") != -1) {
			String tridWithExt = docName.split("_")[1];
			String trid = "";
			if (tridWithExt.indexOf(".") > 0) {
				trid = tridWithExt.substring(0, tridWithExt.lastIndexOf("."));
			}
			return trid;
		  }
		  LogCreator.error("Tridsiz dosya ("+docName+ ") var.",ClientAppMain.class.getSimpleName());
		  return null;
		} 
}