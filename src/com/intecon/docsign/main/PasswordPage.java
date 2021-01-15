package com.intecon.docsign.main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.OcspClientBouncyCastle;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

import driver.Driver;

import java.awt.Font;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.List;

import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class PasswordPage {

	private JFrame frmifreGiri;
	private String loadPath = "C:/temp/";
	private JPasswordField passwordField;
	private Logger logger = LogManager.getLogger(PasswordPage.class);
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PasswordPage window = new PasswordPage(null);
					window.frmifreGiri.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PasswordPage(DocumentToSign document) {

		initialize(document);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(DocumentToSign document) {
		frmifreGiri = new JFrame();
		frmifreGiri.setTitle("Şifre Giriş");
		frmifreGiri.setBounds(100, 100, 493, 273);
		frmifreGiri.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmifreGiri.getContentPane().setLayout(null);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Tahoma", Font.BOLD, 15));
		passwordField.setBounds(120, 110, 316, 19);
		frmifreGiri.getContentPane().add(passwordField);
		passwordField.setEchoChar('*');
		
		JButton submit = new JButton("İmzala");
		submit.setFont(new Font("Tahoma", Font.BOLD, 15));
		submit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String thePassword = new String(passwordField.getPassword());
				try {
		        	int dialogResult = JOptionPane.showConfirmDialog(null, "Döküman imzalanacak. Onaylıyor musunuz?", "İmza Onayı", JOptionPane.YES_NO_OPTION);
		        	System.out.println(dialogResult);
					if(dialogResult == JOptionPane.YES_OPTION){
						if(signDocument("C:/temp/", "ALADDIN", thePassword, document.getPath(), loadPath+"signed_"+document.getTransactionId())) {
							System.out.println("Girdi if");
							JOptionPane.showMessageDialog(null,"Döküman başarıyla imzalanmıştır.","İmzalama Başarılı",JOptionPane.INFORMATION_MESSAGE);
						}else {
							JOptionPane.showMessageDialog(null,"Döküman imzalanamamıştır!","İmzalama Başarısız!",JOptionPane.ERROR_MESSAGE);

						}
					} 
				}catch(Exception e1) {
					JOptionPane.showMessageDialog(null,e1.toString(),"İmzalama Başarısız",JOptionPane.ERROR_MESSAGE);
					logger.error(e1.toString());
				}
				frmifreGiri.dispose();
			}
		});
		submit.setBounds(47, 167, 145, 48);
		frmifreGiri.getContentPane().add(submit);
		
		JButton cancelBtn = new JButton("İPTAL");
		cancelBtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		cancelBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				frmifreGiri.dispose();
			}
		});
		cancelBtn.setBounds(291, 167, 145, 48);
		frmifreGiri.getContentPane().add(cancelBtn);
		
		JLabel lblNewLabel = new JLabel("Şifre:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setBounds(47, 111, 45, 13);
		frmifreGiri.getContentPane().add(lblNewLabel);
		
		JLabel lblLtfenifreniziGiriniz = new JLabel("Lütfen şifrenizi giriniz.");
		lblLtfenifreniziGiriniz.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblLtfenifreniziGiriniz.setBounds(142, 53, 210, 25);
		frmifreGiri.getContentPane().add(lblLtfenifreniziGiriniz);
		
		frmifreGiri.setResizable(false);
        frmifreGiri.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	private boolean signDocument(String contextpath, String cardType, String password, String sourcePath, String loadPath){
		
		try {
			Driver driver = new Driver(contextpath,cardType);
			
			java.security.cert.Certificate[] chain = driver.createCertificateChain(password);
			System.out.println("wlrg");
			if(chain==null) {
				return false;
			}
			PrivateKey pk = driver.getPrivateKey(password);
			Provider provider = driver.getPKCSProvider();
			List<CrlClient> crlList = driver.getCrlList(chain);
			OcspClient ocspClient = new OcspClientBouncyCastle();
			TSAClient tsaClient = driver.getTSAClient(chain);
			driver.sign(sourcePath, loadPath, chain, pk, DigestAlgorithms.SHA256, provider.getName(), CryptoStandard.CMS, "Sign", "intecon", crlList, ocspClient, tsaClient, 0);

		}catch(Exception e) {
			JOptionPane.showMessageDialog(null,e.toString(),"İmzalama Başarısız",JOptionPane.ERROR_MESSAGE);
			logger.error(e.toString());
			return false;
		}
		
		return true;
	}
	
	public JFrame getFrame() {
		return frmifreGiri;
	}

	public void setFrame(JFrame frame) {
		this.frmifreGiri = frame;
	}
}
