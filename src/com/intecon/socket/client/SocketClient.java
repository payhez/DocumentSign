package com.intecon.socket.client;

import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intecon.docsign.main.AppRunner;
import com.intecon.docsign.main.ClientAppMain;
import com.intecon.docsign.model.DocumentModel;
import com.intecon.docsign.model.LogModel;
import com.intecon.docsign.service.ApplicationService;
import com.intecon.docsign.service.ConfigService;
import com.intecon.docsign.view.LogPage;
import com.intecon.docsign.view.PasswordPage;
import com.intecon.log.LogCreator;

public class SocketClient {

	 SockJsClient sockJsClient;
	 WebSocketStompClient stompClient;
	 StompSession session;
	 StompSessionHandler handler;
	 String url;
	 final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
	 String username;
	 static ApplicationService appService = new ApplicationService();
	 
     public void setup(String url, String username) {
    	this.url = url;
    	this.username = username;
    	 
		final AtomicReference<Throwable> failure = new AtomicReference<>();
		List<Transport> transports = new ArrayList<>();
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		sockJsClient = new SockJsClient(transports);
		
		stompClient = new WebSocketStompClient(sockJsClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		  
		handler = new TestSessionHandler(failure) {
			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/greetings/"+username, new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return DocumentModel.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						DocumentModel theDocument = (DocumentModel)payload;
						try {
							System.out.println("Response payload : " + theDocument.getClient());
							appService.convertBytesToFile(getBytesByTrid(theDocument.getTrid()),theDocument.getName() + "_" + theDocument.getTrid());
							AppRunner.getTrayIcon().displayMessage("İmzalanmayı Bekleyen Dökümanlarınız Var!", "İmzalamak için uygulama iconuna çift tıklayınız!" ,TrayIcon.MessageType.INFO);
						} catch (Throwable t) {
							failure.set(t);
						}
					}
				});
			}
		};
     }
     
     public StompSession getSession() {
    	 if(appService.checkUnsignedFiles()) {
 			AppRunner.getTrayIcon().displayMessage("İmzalanmayı Bekleyen Dökümanlarınız Var!", "İmzalamak için uygulama iconuna çift tıklayınız!" ,TrayIcon.MessageType.INFO);
     	 }
    	 return session;
     }
     
     public void start()  throws InterruptedException, ExecutionException {
    	 session = stompClient.connect(url, headers, handler, ConfigService.getServerPort()).get();
    	 stompClient.start();
     }
     public void stop() {
    	 stompClient.stop();
     }
     
    private static final String USER_AGENT = "Mozilla/5.0";
 	private static final String GET_URL_TRID = "http://"+ConfigService.getServerIp()+":"+ConfigService.getServerPort()+"/DocumentSignService/documentController/getDocumentModelFromClientByTrid/";
 	
 	public static byte[] getBytesByTrid(String trid) throws IOException {
 		
 		Gson gson = new Gson();
 		URL url = new URL(GET_URL_TRID+trid);
 		HttpURLConnection con = (HttpURLConnection) url.openConnection();
 		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
 		con.setRequestProperty("Accept", "application/json");
 		con.setRequestProperty("User-Agent", USER_AGENT);
 		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(),"UTF-8"));
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
			byte[] newBytes = Base64.getDecoder().decode(responseDoc.getBytesData());
			LogCreator.info("Unnsigned document("+responseDoc.getName()+") is saved!", SocketClient.class.getName());
			return newBytes;
		} else {
			LogCreator.error("getDocumentModelFromClientByTrid did not work!",SocketClient.class.getName());
			return null;
		}
 	}
 	
	static String signed = "false";
	private static final String GET_URL_UNSIGNED = "http://"+ConfigService.getServerIp()+":"+ConfigService.getServerPort()+"/DocumentSignService/documentController/getDocumentModelFromClient/"+appService.getMacId()+"/"+signed;
	  
	public static List<DocumentModel> getUnsignedDocuments() throws IOException {
	 		
 		Gson gson = new Gson();
 		URL url = new URL(GET_URL_UNSIGNED);
 		HttpURLConnection con = (HttpURLConnection) url.openConnection();
 		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
 		con.setRequestProperty("Accept", "application/json");
 		con.setRequestProperty("User-Agent", USER_AGENT);
 		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(),"UTF-8"));
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
	
	private static final String POST_URL = "http://"+ConfigService.getServerIp()+":"+ConfigService.getServerPort()+"/DocumentSignService/documentController/saveDocumentFromClient/";
	
	public static void postFileToServer(DocumentModel document) throws IOException {
		
		document.setBinaryData(appService.convertDocToByteArray(document.getDocumentUrl()));
		
		Gson gson = new Gson();
		URL url = new URL(POST_URL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		
		String json = gson.toJson(document);
		wr.write(json.getBytes());
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(),"UTF-8"));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} else {
			LogCreator.error("POST request for sending the signed document did not work due to response code: "+responseCode, PasswordPage.class.getName());
		}
	}
	
	private static final String GET_URL_LOG = "http://"+ConfigService.getServerIp()+":"+ConfigService.getServerPort()+"/DocumentSignService/documentController/getAllLogsForUser/"+appService.getMacId();
	
	public static List<LogModel> getLogsForUser() throws IOException {
	 		
 		Gson gson = new Gson();
 		URL url = new URL(GET_URL_LOG);
 		HttpURLConnection con = (HttpURLConnection) url.openConnection();
 		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
 		con.setRequestProperty("Accept", "application/json");
 		con.setRequestProperty("User-Agent", USER_AGENT);
 		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			List<LogModel> logList = new ArrayList<LogModel>();
			try {
				logList = gson.fromJson(response.toString(), new TypeToken<List<LogModel>>(){}.getType());
			} catch(Exception e) {
				e.printStackTrace();
			}
			return logList;
		} else {
			LogCreator.error("getLogsForUser did not work! Response code: " + responseCode, LogPage.class.getName());
			return null;
		}
 	}
}
