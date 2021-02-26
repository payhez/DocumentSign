package com.intecon.docsign.view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.intecon.docsign.main.SigningThread;
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
	private Thread t;
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
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frmifreGiri.setSize(493, 276);
		frmifreGiri.setLocation(dim.width/2-frmifreGiri.getSize().width/2, dim.height/2-frmifreGiri.getSize().height/2);
		frmifreGiri.getContentPane().setLayout(null);
		frmifreGiri.setResizable(false);
        frmifreGiri.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frmifreGiri.addWindowListener(new java.awt.event.WindowAdapter() {
	        @Override
	        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
	        	for (Frame frame : Frame.getFrames()) {
					if(frame.getName().equals(ListPage.class.getSimpleName()) || frame.getName().equals(PdfViewPage.class.getSimpleName())) {
						frame.setEnabled(true);
						frame.toFront();
					}
				}
	        }
	    });
        
        final ProgressPanel it = new ProgressPanel(0,documents.size());
	    JFrame frame = new JFrame("İmzalanıyor...");
		frame.setResizable(false);
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
		submit.setEnabled(false);
		passwordField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(passwordField.getPassword().length == 0) {
					submit.setEnabled(false);
				}else {
					submit.setEnabled(true);
				}
				
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		/*passwordField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(passwordField.getPassword().length == 0) {
					submit.setEnabled(false);
				}else {
					submit.setEnabled(true);
				}
			}
		});*/
		submit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(submit.isEnabled()) {
					String thePassword = new String(passwordField.getPassword());
					try {
			        	int dialogResult = JOptionPane.showConfirmDialog(null, "Döküman(lar) imzalanacak. Onaylıyor musunuz?", "İmza Onayı", JOptionPane.YES_NO_OPTION);
						if(dialogResult == JOptionPane.YES_OPTION){
							frame.setVisible(true);
							for (Frame frame : Frame.getFrames()) {
								if(frame.getName().equals(PdfViewPage.class.getSimpleName())) {
									frame.dispose();
								}
							}
							String pathWithDate = SIGNED_URL+LocalDate.now()+"/";
							File theDir = new File(pathWithDate);
							if(!theDir.exists()){
								theDir.mkdirs();
							}
							
							SigningThread thread = new SigningThread(frame, documents, thePassword, pathWithDate, it);
						    thread.start();
						} 
					}catch(Exception e1) {
						LogCreator.error("The document couldn't be signed due to: " +e1.toString(), PasswordPage.class.getName());
						JOptionPane.showMessageDialog(null,e1.toString(),"İmzalama Başarısız",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		submit.setBounds(47, 167, 145, 48);
		frmifreGiri.getContentPane().add(submit);
	}
	
	private boolean signDocument(String contextpath, String cardType, String password, String sourcePath, String loadPath){
		
		try {
			Driver driver = new Driver(contextpath,cardType);
			int a =1;
			if(a==1) {
				return false;
			}
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
	
	public JFrame getFrame() {
		return frmifreGiri;
	}

	public void setFrame(JFrame frame) {
		this.frmifreGiri = frame;
	}
}
