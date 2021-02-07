package com.intecon.docsign.log;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import com.intecon.docsign.config.ConfigPage;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Desktop;

public class LogPage {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogPage window = new LogPage();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public LogPage() {
		initialize();
	}
	
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 534, 208);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnProp = new JButton("AYARLAR");
		btnProp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
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
		btnProp.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnProp.setBounds(60, 72, 148, 35);
		frame.getContentPane().add(btnProp);
		
		JLabel lblVersion = new JLabel("Version:");
		lblVersion.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblVersion.setBounds(60, 30, 74, 21);
		frame.getContentPane().add(lblVersion);
		
		JButton btnLog = new JButton("LOG");
		btnLog.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				File file = new File("logs/exceptions.log");
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnLog.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnLog.setBounds(276, 72, 148, 35);
		frame.getContentPane().add(btnLog);
		
		JButton btnExit = new JButton("ÇIKIŞ");
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				frame.dispose();
			}
		});
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnExit.setBounds(372, 125, 148, 35);
		frame.getContentPane().add(btnExit);
		
		JLabel versionLabel = new JLabel("version");
		versionLabel.setForeground(Color.RED);
		versionLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		versionLabel.setBounds(130, 30, 74, 21);
		frame.getContentPane().add(versionLabel);
		
		frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public Window getFrame() {
		// TODO Auto-generated method stub
		return frame;
	}
}
