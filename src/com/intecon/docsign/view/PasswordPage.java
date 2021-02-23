package com.intecon.docsign.view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.intecon.docsign.model.DocumentModel;
import com.intecon.docsign.service.ApplicationService;
import com.intecon.docsign.service.ConfigService;
import com.intecon.log.LogCreator;
import com.intecon.socket.client.SocketClient;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.OcspClientBouncyCastle;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

import driver.Driver;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.security.PrivateKey;
import java.security.Provider;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

public class PasswordPage {

	private JFrame frmifreGiri;
	private String SIGNED_URL = ConfigService.getSignedPath();
	private JPasswordField passwordField;
	ApplicationService appService =  new ApplicationService();
	
	public PasswordPage(List<DocumentModel> documents) {
		initialize(documents);
	}
	
	public PasswordPage(DocumentModel document) { // Alternative constructor for PDF View purpose
		
		List<DocumentModel> documents = new ArrayList<>();
		documents.add(document);
		initialize(documents);
	}

	private void initialize(List<DocumentModel> documents) {
		
		frmifreGiri = new JFrame();
		frmifreGiri.setName(this.getClass().getSimpleName());
		frmifreGiri.setTitle("Şifre Giriş");
		frmifreGiri.setBounds(100, 100, 493, 276);
		frmifreGiri.getContentPane().setLayout(null);
		frmifreGiri.setResizable(false);
        frmifreGiri.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        final ProgressPanel it = new ProgressPanel(0,documents.size());
	    JFrame frame = new JFrame("İmzalanıyor...");
		frame.setResizable(false);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(460, 200);
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setContentPane(it);
	    frame.setVisible(false);
		
		JLabel lblNewLabel = new JLabel("Şifre:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setBounds(47, 111, 45, 13);
		frmifreGiri.getContentPane().add(lblNewLabel);
		
		JLabel theLabel = new JLabel("Lütfen şifrenizi giriniz.");
		theLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		theLabel.setBounds(142, 53, 210, 25);
		frmifreGiri.getContentPane().add(theLabel);
        
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Tahoma", Font.BOLD, 15));
		passwordField.setBounds(120, 110, 316, 19);
		frmifreGiri.getContentPane().add(passwordField);
		passwordField.setEchoChar('*');
		
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
		
		JButton submit = new JButton("İmzala");
		submit.setFont(new Font("Tahoma", Font.BOLD, 15));
		submit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String thePassword = new String(passwordField.getPassword());
				try {
		        	int dialogResult = JOptionPane.showConfirmDialog(null, "Döküman(lar) imzalanacak. Onaylıyor musunuz?", "İmza Onayı", JOptionPane.YES_NO_OPTION);
					if(dialogResult == JOptionPane.YES_OPTION){
						frmifreGiri.dispose();
						frame.setVisible(true);
						
						String pathWithDate = SIGNED_URL+LocalDate.now()+"/";
						File theDir = new File(pathWithDate);
						if(!theDir.exists()){
							theDir.mkdirs();
						}
						for (Frame frame : Frame.getFrames()) {
							if(frame.getName().equals(PasswordPage.class.getSimpleName())) {
								frame.dispose();
							}else if(frame.getName().equals(ListPage.class.getSimpleName()) || frame.getName().equals(PdfViewPage.class.getSimpleName())) {
								frame.toBack();
								frame.setEnabled(false);
							}
						}

						Runnable runner = new Runnable()
					    {
					        public void run() {
					        	int i = 0;
					        	
								for (DocumentModel document: documents) {
									frame.toFront();
									if(!signDocument("C:/temp/", "ALADDIN", thePassword, document.getDocumentUrl(), pathWithDate+document.getName()+document.getFileExtension())) {
										JOptionPane.showMessageDialog(null,document.getName()+" dökümanı imzalanamamıştır!","İmzalama Başarısız!",JOptionPane.ERROR_MESSAGE);
									}else {
										i++;
										final int increase = i;
										try {
											SwingUtilities.invokeLater(new Runnable() {
										          public void run() {
										            it.updateBar(increase);
										          }
										        });
										} catch (Exception e) {
											LogCreator.error("RealTime GUI(ProgressBar) update error due to:" + e.toString(), PasswordPage.class.getName());
										}
										
										DocumentModel signedDocument = new DocumentModel();
										signedDocument.setTrid(document.getTrid());
										signedDocument.setDocumentUrl(pathWithDate+document.getName()+document.getFileExtension());
										try {
											SocketClient.postFileToServer(signedDocument);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											LogCreator.error("postFileToServer did not work due to:" + e.toString(), PasswordPage.class.getName());
										}
										if(ConfigService.getDeleteOption().equals("true")) {
											appService.deleteFileFromDirectory(document.getDocumentUrl());
										}
										
										LogCreator.info("The document:"+signedDocument.getName() +" is successfully signed!", PasswordPage.class.getName());
										if(documents.size() == 1) {
											JOptionPane.showMessageDialog(null,document.getName()+" dökümanı başarıyla imzalanmıştır.","İmzalama Başarılı",JOptionPane.INFORMATION_MESSAGE);
										}else if(documents.get(documents.size()-1) == document) {
											JOptionPane.showMessageDialog(null,"Seçilen dökümanlar başarıyla imzalanmıştır.","İmzalama Başarılı",JOptionPane.INFORMATION_MESSAGE);
										}
									}
								}
								frame.dispose();
								EventQueue.invokeLater(new Runnable() {
					    			public void run() {
					    				try {
					    					ListPage listPage = new ListPage();
					    					listPage.getFrame().setVisible(true);
					    					listPage.getFrame().toBack();
					    					Thread.sleep(100);
					    					listPage.getFrame().toFront();
					    				} catch (Exception e) {
					    					LogCreator.error("Couldn't open ListPage due to:" +e.toString(), PasswordPage.class.getName());
					    				}
					    			}
					    		});
					        }
					    };
					    Thread t = new Thread(runner, "Code Executer");
					    t.start();
					} 
				}catch(Exception e1) {
					LogCreator.error("The document couldn't be signed due to: " +e1.toString(), PasswordPage.class.getName());
					JOptionPane.showMessageDialog(null,e1.toString(),"İmzalama Başarısız",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		submit.setBounds(47, 167, 145, 48);
		frmifreGiri.getContentPane().add(submit);
	}
	
	private boolean signDocument(String contextpath, String cardType, String password, String sourcePath, String loadPath){
		
		try {
			Driver driver = new Driver(contextpath,cardType);
			Provider provider = driver.getPKCSProvider();
			java.security.cert.Certificate[] chain = driver.createCertificateChain(password);
			if(chain == null) {
				LogCreator.error("Chain returned null!", PasswordPage.class.getName());
			}
			PrivateKey pk = driver.getPrivateKey(password);
			OcspClient ocspClient = new OcspClientBouncyCastle();
			List<CrlClient> crlList = driver.getCrlList(chain);
			TSAClient tsaClient = driver.getTSAClient(chain);
			
			driver.sign(sourcePath, loadPath, chain, pk, DigestAlgorithms.SHA256, provider.getName(), CryptoStandard.CMS, "Sign", "intecon", crlList, ocspClient, tsaClient, 0);
		}catch(Exception e) {
			LogCreator.error("The method signDocument did not work due to :" +e.toString(), PasswordPage.class.getName());
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
