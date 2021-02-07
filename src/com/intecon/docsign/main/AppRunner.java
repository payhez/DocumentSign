package com.intecon.docsign.main;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.intecon.docsign.config.ConfigPage;
import com.intecon.docsign.log.LogPage;

public class AppRunner {
	
	private static Image image = Toolkit.getDefaultToolkit().getImage("C:/temp/resources/icon.png");
    private static PopupMenu popup = new PopupMenu();
	private static TrayIcon trayIcon = new TrayIcon(image, "Intecon Döküman İmzalama", popup);
	
	public static TrayIcon getTrayIcon() {
		return trayIcon;
	}
	
	public static void runApp() {
		 Runnable runner = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(SystemTray.isSupported()) {
						
						  final SystemTray tray = SystemTray.getSystemTray();
				          
				          MenuItem item = new MenuItem("İmza Bekleyen Dökümanlar");
				          item.addActionListener(new ActionListener() {
				        	  public void actionPerformed(ActionEvent e) {
			        			  EventQueue.invokeLater(new Runnable() {
					    			public void run() {
					    				try {
					    					ListPage listPage = new ListPage();
					    					listPage.getFrame().setVisible(true);
					    				} catch (Exception e) {
					    					e.printStackTrace();
					    				}
					    			}
			        			  });
			        		  }
				          });
						  popup.add(item);
						  
				          item = new MenuItem("Ayarlar");
				          item.addActionListener(new ActionListener() {
				        	  public void actionPerformed(ActionEvent e) {
				        		  EventQueue.invokeLater(new Runnable() {
						    			public void run() {
						    				try {
						    					ConfigPage configWindow = new ConfigPage();
						    					configWindow.getFrame().setVisible(true);
						    				} catch (Exception e) {
						    					e.printStackTrace();
						    				}
						    			}
						    		});
			        		  }
				          });
						  popup.add(item);
						  
						  item = new MenuItem("LOG");
				          item.addActionListener(new ActionListener() {
				        	  public void actionPerformed(ActionEvent e) {
				        		  EventQueue.invokeLater(new Runnable() {
						    			public void run() {
						    				try {
						    					LogPage logWindow = new LogPage();
						    					logWindow.getFrame().setVisible(true);
						    				} catch (Exception e) {
						    					e.printStackTrace();
						    				}
						    			}
						    		});
			        		  }
				          });
						  popup.add(item);
						  
				          item = new MenuItem("Kapat");
				          item.addActionListener(new ActionListener() {
				        	  public void actionPerformed(ActionEvent e) {
				        		  tray.remove(trayIcon);
			        		  }
				          });
				          popup.add(item);
				          
				          trayIcon.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
					        	EventQueue.invokeLater(new Runnable() {
					    			public void run() {
					    				try {
					    					ListPage listPage = new ListPage();
					    					listPage.getFrame().setVisible(true);
					    				} catch (Exception e) {
					    					e.printStackTrace();
					    				}
					    			}
					    		});
							}
				          });
				          
				          try {
				            tray.add(trayIcon);
				          } catch (AWTException e) {
				            System.err.println("Can't add to tray");
				          }
					} else {
				          System.err.println("Tray unavailable");
			        }
				}
		    };
		    EventQueue.invokeLater(runner);
	}

}