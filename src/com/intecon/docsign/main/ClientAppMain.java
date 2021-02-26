package com.intecon.docsign.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import com.intecon.docsign.service.ApplicationService;
import com.intecon.docsign.service.ConfigService;
import com.intecon.log.LogCreator;
import com.intecon.socket.client.SocketClient;

public class ClientAppMain {
	
	static ApplicationService appService = new ApplicationService();
	private static String macId= appService.getMacId();

	public static void main(String args[]) {
		  String url = "ws://"+ConfigService.getServerIp()+":"+ConfigService.getServerPort()+"/DocumentSignService/gs-guide-websocket";
		  
		  appService.createFolders(); // Creates necessary folders(SIGNED, UNSIGNED)
		  appService.clearUnsignedFolder(); // Deletes unnecessary date folders
		  
		  try {
			  AppRunner.runApp(); // Runs the app with TrayIcon components
			  SocketClient sc = new SocketClient();
			  sc.setup(url, macId);
			  sc.start();
			  sc.getSession().send("/app/registration/"+macId, "aa11");
			  
			  SimpleDateFormat formatter = new SimpleDateFormat(ConfigService.getDateTimeStyle());
			  LogCreator.info("Connected! Date: " + formatter.format(new Date()) , ClientAppMain.class.getName());
			  appService.checkUnsignedDocumentsFromServer();
			} catch (Exception e) {
				LogCreator.error("Not connected due to:" +e.toString(), ClientAppMain.class.getName());
				JOptionPane.showMessageDialog(null,"Sunucu("+ConfigService.getServerIp()+":"+ConfigService.getServerIp()+") ile bağlantı sağlanamamıştır. Lütfen sunucu bağlantılarınızı kontrol edin!","Döküman İmzalama - Bağlantı Hatası",JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
	  }
}