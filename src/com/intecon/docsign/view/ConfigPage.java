package com.intecon.docsign.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import com.intecon.docsign.service.ConfigService;
import com.intecon.log.LogCreator;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Desktop;

public class ConfigPage {

	private JFrame frmAyarlarVeLog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigPage window = new ConfigPage();
					window.frmAyarlarVeLog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ConfigPage() {
		initialize();
	}
	
	private void initialize() {
		frmAyarlarVeLog = new JFrame();
		frmAyarlarVeLog.setTitle("Ayarlar ve Log");
		frmAyarlarVeLog.setBounds(100, 100, 558, 217);
		frmAyarlarVeLog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAyarlarVeLog.getContentPane().setLayout(null);
		
		JButton btnProp = new JButton("AYARLAR");
		btnProp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				EventQueue.invokeLater(new Runnable() {
	    			public void run() {
	    				try  
	    				{  
	    				//constructor of file class having file as argument  
	    				File file = new File(ConfigService.getConfigFilePath());   
	    				if(!Desktop.isDesktopSupported())//check if Desktop is supported by Platform or not  
	    				{  
		    				LogCreator.error("Desktop is not supported for file opening.", ConfigPage.class.getName());
		    				return;  
	    				}  
	    				Desktop desktop = Desktop.getDesktop();  
	    				if(file.exists())         //checks file exists or not  
	    					desktop.open(file);              //opens the specified file  
	    				}  
	    				catch(Exception e)  
	    				{  
		    				LogCreator.error("Could not open the file due to : " +e.toString(), ConfigPage.class.getName());
	    				}  
	    			}
	    		});
			}
		});
		btnProp.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnProp.setBounds(60, 72, 148, 35);
		frmAyarlarVeLog.getContentPane().add(btnProp);
		
		JLabel lblVersion = new JLabel("Version:");
		lblVersion.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblVersion.setBounds(60, 30, 74, 21);
		frmAyarlarVeLog.getContentPane().add(lblVersion);
		
		JButton btnLog = new JButton("LOG");
		btnLog.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				File file = new File(ConfigService.getLogPath()); //TODO Client server log
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnLog.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnLog.setBounds(255, 72, 148, 35);
		frmAyarlarVeLog.getContentPane().add(btnLog);
		
		JButton btnExit = new JButton("ÇIKIŞ");
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				frmAyarlarVeLog.dispose();
			}
		});
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnExit.setBounds(384, 130, 148, 35);
		frmAyarlarVeLog.getContentPane().add(btnExit);
		
		JLabel versionLabel = new JLabel(ConfigService.getVersion());
		versionLabel.setForeground(Color.RED);
		versionLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		versionLabel.setBounds(130, 30, 74, 21);
		frmAyarlarVeLog.getContentPane().add(versionLabel);
		
		frmAyarlarVeLog.setResizable(false);
        frmAyarlarVeLog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public Window getFrame() {
		// TODO Auto-generated method stub
		return frmAyarlarVeLog;
	}
}
