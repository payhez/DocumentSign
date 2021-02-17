package com.intecon.socket.client;

import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
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
import com.intecon.docsign.log.LogCreator;
import com.intecon.docsign.main.AppRunner;
import com.intecon.docsign.main.DocumentModel;
import com.intecon.socket.client.TestSessionHandler;

public class SocketClient {

	 SockJsClient sockJsClient;
	 WebSocketStompClient stompClient;
	 StompSession session;
	 StompSessionHandler handler;
	 String url;
	 public static String port = "8080";
	 public static String ipAddress = "10.0.0.68";
	 final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
	 String username;
	 
     public void setup(String url, String username) {
    	this.url = url;
    	this.username = username;
    	 
    	//final CountDownLatch latch = new CountDownLatch(1);
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
						System.out.println("getpayloadtype");
						return DocumentModel.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						DocumentModel theDocument= (DocumentModel)payload;
						try {
							System.out.println("Response payload : " + theDocument.getClient());
							convertBytesToFile(getBytesByTrid(theDocument.getTrid()),theDocument.getName() + "_" + theDocument.getTrid());
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
    	 if(checkUnsignedFiles()) {
 			AppRunner.getTrayIcon().displayMessage("İmzalanmayı Bekleyen Dökümanlarınız Var!", "İmzalamak için uygulama iconuna çift tıklayınız!" ,TrayIcon.MessageType.INFO);
     	 }
    	 return session;
     }
     
     public void start()  throws InterruptedException, ExecutionException {
    	 session = stompClient.connect(url, headers, handler, port).get();
    	 stompClient.start();
     }
     public void stop() {
    	 stompClient.stop();
     }
     
     public static void convertBytesToFile(byte[] bytes, String fileName) {
    	String pathWithDate = "C:/Temp/UnSigned/" + LocalDate.now().toString() + "/";
    	File theDir = new File(pathWithDate);
 		if (!theDir.exists()){
 		    theDir.mkdirs();
 		}
    	 try (FileOutputStream fos = new FileOutputStream(pathWithDate+fileName+".pdf")) {
    		   fos.write(bytes);
    		} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     }
     
     public static byte[] convertDocToByteArray(String path)throws FileNotFoundException, IOException{
	        File file = new File(path);

	        FileInputStream fis = new FileInputStream(file);
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] buf = new byte[1024];
	        try {
	            for (int readNum; (readNum = fis.read(buf)) != -1;) {
	                bos.write(buf, 0, readNum);
	            }
	        } catch (IOException ex) {
	        }
	        byte[] bytes = bos.toByteArray();
	        return bytes;
	 }
     
    private static final String USER_AGENT = "Mozilla/5.0";
    
    
 	//private static final String GET_URL = "http://localhost:8080/DocumentSigningService/documentController/getDocumentModelFromClientByTrid/";
 	private static final String GET_URL = "http://"+ipAddress+":"+port+"/DocumentSignService/documentController/getDocumentModelFromClientByTrid/";
 	
 	public static byte[] getBytesByTrid(String trid) throws IOException {
 		
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
			byte[] newBytes = Base64.getDecoder().decode(responseDoc.getBytesData());
			LogCreator.info("Unnsigned document("+responseDoc.getName()+") is saved!", SocketClient.class.getName());
			return newBytes;
		} else {
			LogCreator.error("getDocumentModelFromClientByTrid did not work!",SocketClient.class.getName());
			return null;
		}
 	}
 	
 	private boolean checkUnsignedFiles() {
		 File path = new File("C:/Temp/UnSigned");
		 if(!path.exists() || path.list().length == 0) {
			 return false;
		 }
		 return true;
 	}
}
