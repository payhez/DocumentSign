package com.intecon.docsign.main;

import java.awt.EventQueue;
import java.awt.Frame;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.intecon.docsign.model.DocumentModel;
import com.intecon.docsign.service.ApplicationService;
import com.intecon.docsign.service.ConfigService;
import com.intecon.docsign.view.ListPage;
import com.intecon.docsign.view.PasswordPage;
import com.intecon.docsign.view.ProgressPanel;
import com.intecon.log.LogCreator;
import com.intecon.socket.client.SocketClient;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.OcspClientBouncyCastle;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

import driver.Driver;

public class SigningThread extends Thread implements Runnable {
	 public Object lock = this;
	 public boolean pause = false;
	 
	 private JFrame frame;
	 private List<DocumentModel> documents;
	 private String thePassword;
	 private String pathWithDate;
	 private ProgressPanel it;
	 private ApplicationService appService = new ApplicationService();
	 
	 Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
	        @Override
	        public void uncaughtException(Thread th, Throwable ex) {
	        	for (Frame frame : Frame.getFrames()) {
					frame.dispose();
				}
	        	LogCreator.error("The document couldn't be signed due to: " +ex.toString(), PasswordPage.class.getName());
				JOptionPane.showMessageDialog(null,ex.toString(),"İmzalama Başarısız",JOptionPane.ERROR_MESSAGE);
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(1);
	        }
	 };
	 
	 public SigningThread(JFrame frame, List<DocumentModel> documents, String thePassword, String pathWithDate, ProgressPanel it) {
		 this.frame = frame;
		 this.documents =documents;
		 this.thePassword = thePassword;
		 this.pathWithDate = pathWithDate;
		 this.it =it;
		 this.setUncaughtExceptionHandler(h);
	 }
	 
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
					signedDocument.setClient(appService.getMacId());
					try {
						SocketClient.postFileToServer(signedDocument);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						LogCreator.error("postFileToServer did not work due to:" + e.toString(), PasswordPage.class.getName());
					}
					if(ConfigService.getDeleteOption().equals("true")) {
						appService.deleteFileFromDirectory(document.getDocumentUrl());
					}
					
					LogCreator.info("The document:"+document.getName() +" is successfully signed!", PasswordPage.class.getName());
					if(documents.size() == 1) {
						JOptionPane.showMessageDialog(null,document.getName()+" dökümanı başarıyla imzalanmıştır.","İmzalama Başarılı",JOptionPane.INFORMATION_MESSAGE);
					}else if(documents.get(documents.size()-1) == document) {
						JOptionPane.showMessageDialog(null,"Seçilen dökümanlar başarıyla imzalanmıştır.","İmzalama Başarılı",JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			for (Frame frame : Frame.getFrames()) {
				frame.dispose();
			}
			EventQueue.invokeLater(new Runnable() {
 			public void run() {
 				try {
 					ListPage listPage = new ListPage();
 					listPage.getFrame().setVisible(true);
 					listPage.getFrame().toFront();
 				} catch (Exception e) {
 					LogCreator.error("Couldn't open ListPage due to:" +e.toString(), PasswordPage.class.getName());
 				}
 			}
 		});
     }
	 
	 private boolean signDocument(String contextpath, String cardType, String password, String sourcePath, String loadPath){
			
			try {
				Driver driver = new Driver(contextpath,cardType);
				Provider provider = driver.getPKCSProvider();
				java.security.cert.Certificate[] chain = driver.createCertificateChain(password);
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
}
