package com.intecon.docsign.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.intecon.docsign.model.DocumentModel;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Color;
import java.awt.SystemColor;

public class PdfViewPage {

	private JFrame frmDkmanGrntleme;
	private JButton signButton;
	
	public PdfViewPage(DocumentModel document) {
		initialize(document);
	}
	
	private void initialize(DocumentModel document) {
		frmDkmanGrntleme = new JFrame();
		frmDkmanGrntleme.setName(this.getClass().getSimpleName());
		frmDkmanGrntleme.setTitle("Döküman Görüntüleme");
		frmDkmanGrntleme.setBounds(100, 70, 954, 782);
		frmDkmanGrntleme.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 98, 930, 646);
		frmDkmanGrntleme.getContentPane().add(panel);
		
		final JWebBrowser browser = new JWebBrowser();
		browser.setBounds(1, 1, 965, 770);
		browser.navigate(document.getDocumentUrl());
		browser.setStatusBarVisible(false);
		browser.setMenuBarVisible(false);
		browser.setLocationBarVisible(false);
		browser.setButtonBarVisible(false);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(browser, BorderLayout.CENTER);
		
		signButton = new JButton("İMZALA");
		signButton.setBackground(Color.GREEN);
		signButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		signButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				for (Frame frame : Frame.getFrames()) {
					if(frame.getName().equals(PasswordPage.class.getSimpleName()) || frame.getName().equals(ListPage.class.getSimpleName())) {
						frame.dispose();
					}
				}
				EventQueue.invokeLater(new Runnable() {
	    			public void run() {
	    				UIUtils.setPreferredLookAndFeel();
	    				NativeInterface.open();
	    				try {
	    					PasswordPage passwordPage = new PasswordPage(document);
	    					passwordPage.getFrame().setVisible(true);
	    				} catch (Exception e) {
	    					e.printStackTrace();
	    				}
	    			}
	    		});
				
			}
		});
		signButton.setBounds(797, 10, 130, 36);
		frmDkmanGrntleme.getContentPane().add(signButton);
		
		JButton btnExit = new JButton("İPTAL");
		btnExit.setBackground(Color.RED);
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frmDkmanGrntleme.dispose();
			}
		});
		btnExit.setBounds(797, 56, 130, 32);
		frmDkmanGrntleme.getContentPane().add(btnExit);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.info);
		panel_1.setBounds(10, 10, 759, 78);
		frmDkmanGrntleme.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Döküman Bilgisi");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel.setBounds(0, 0, 105, 16);
		panel_1.add(lblNewLabel);
		
		JLabel lblDkmanAd = new JLabel("Döküman Adı:");
		lblDkmanAd.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblDkmanAd.setBounds(34, 26, 105, 16);
		panel_1.add(lblDkmanAd);
		
		JLabel lblDkmanSahibi = new JLabel("Döküman Sahibi:");
		lblDkmanSahibi.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblDkmanSahibi.setBounds(323, 26, 110, 16);
		panel_1.add(lblDkmanSahibi);
		
		JLabel lblTarih = new JLabel("Tarih:");
		lblTarih.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblTarih.setBounds(598, 26, 59, 16);
		panel_1.add(lblTarih);
		
		JLabel label = new JLabel("");
		label.setBounds(78, 26, 45, 13);
		panel_1.add(label);
		
		JLabel docName = new JLabel("ad");
		docName.setForeground(Color.RED);
		docName.setFont(new Font("Tahoma", Font.BOLD, 13));
		docName.setBounds(34, 52, 307, 16);
		panel_1.add(docName);
		docName.setText(document.getName());
		
		JLabel docOwner = new JLabel("sahip");
		docOwner.setForeground(Color.RED);
		docOwner.setFont(new Font("Tahoma", Font.BOLD, 13));
		docOwner.setBounds(323, 52, 171, 16);
		panel_1.add(docOwner);
		docOwner.setText(document.getClient());
		
		JLabel date = new JLabel("tarih");
		date.setForeground(Color.RED);
		date.setFont(new Font("Tahoma", Font.BOLD, 13));
		date.setBounds(598, 52, 161, 16);
		panel_1.add(date);
		date.setText(document.getCrt_date());
		
		frmDkmanGrntleme.setResizable(false);
        frmDkmanGrntleme.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	}
	
	public JFrame getFrame() {
		return frmDkmanGrntleme;
	}

	public void setFrame(JFrame frame) {
		this.frmDkmanGrntleme = frame;
	}
}
