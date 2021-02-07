package com.intecon.docsign.main;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.google.gson.Gson;
import com.intecon.docsign.log.LogCreator;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.OcspClientBouncyCastle;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

import driver.Driver;

import java.awt.EventQueue;
import java.awt.Font;
import java.security.PrivateKey;
import java.security.Provider;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class PasswordPage {

	private JFrame frmifreGiri;
	private JFrame pdfViewFrame = null;
	private JFrame listView = null;
	private String SIGNED_URL = "C:/Temp/Signed/";
	private String UNSIGNED_URL = "C:/Temp/UnSigned/";
	private JPasswordField passwordField;
	
	public PasswordPage(List<DocumentModel> documents, JFrame listView) {
		initialize(documents);
		this.listView = listView;
	}
	
	public PasswordPage(DocumentModel document, JFrame listView, JFrame pdfViewFrame) { // Alternative constructor for PDF View purpose
		
		List<DocumentModel> documents = new ArrayList<>();
		documents.add(document);
		this.pdfViewFrame = pdfViewFrame;
		this.listView = listView;
		initialize(documents);
	}

	private void initialize(List<DocumentModel> documents) {
		File theDir = new File(SIGNED_URL);
		if (!theDir.exists()){
		    theDir.mkdirs();
		}
		theDir = new File(UNSIGNED_URL);
		if (!theDir.exists()){
		    theDir.mkdirs();
		}
		
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
		        	int dialogResult = JOptionPane.showConfirmDialog(null, "Döküman(lar) imzalanacak. Onaylıyor musunuz?", "İmza Onayı", JOptionPane.YES_NO_OPTION);
					if(dialogResult == JOptionPane.YES_OPTION){
						for (DocumentModel document: documents) {
							document.setTrid(trimTrid(document.getName()));
							String pathWithDate = SIGNED_URL+LocalDate.now()+"/";
							File theDir = new File(pathWithDate);
							if(!theDir.exists()){
								theDir.mkdirs();
							}
							signDocument("C:/temp/", "ALADDIN", thePassword, document.getDocumentUrl(), pathWithDate+document.getName(), false);
							if(!signDocument("C:/temp/", "ALADDIN", thePassword, document.getDocumentUrl(), pathWithDate+document.getName(),true)) {
								JOptionPane.showMessageDialog(null,document.getName()+" dökümanı imzalanamamıştır!","İmzalama Başarısız!",JOptionPane.ERROR_MESSAGE);
							}else {
								
								DocumentModel signedDocument = new DocumentModel();
								signedDocument.setSigned(true);
								signedDocument.setTrid(trimTrid(document.getName()));
								signedDocument.setClient(document.getClient());
								signedDocument.setCrt_date(LocalDate.now().toString());
								signedDocument.setName(document.getName().split("_")[0]);
								signedDocument.setFileExtansion(document.getFileExtansion());
								//signedDocument.setDocumentUrl(document.getDocumentUrl()); // TODO this is gonna be changed with the one below
								signedDocument.setDocumentUrl(pathWithDate+document.getName());
								//signedDocument.setErpID(document.getErpID());
								//signedDocument.setKey(document.getKey());
								postFileToServer(signedDocument);
								if(listView != null) {
									listView.dispose();
									EventQueue.invokeLater(new Runnable() {
						    			public void run() {
						    				try {
						    					ListPage listPage = new ListPage();
						    					listPage.getFrame().setVisible(true);
						    					listPage.getFrame().toBack();
						    					Thread.sleep(500);
						    					listPage.getFrame().toFront();
						    				} catch (Exception e) {
						    					e.printStackTrace();
						    				}
						    			}
						    		});
								}
								if(pdfViewFrame != null) {
									pdfViewFrame.dispose();
									Thread.sleep(500);
								}
								deleteFileFromDirectory(document.getDocumentUrl());
								LogCreator.info("The document:"+signedDocument.getName() +" is successfully signed!", PasswordPage.class.getName());
								JOptionPane.showMessageDialog(null,document.getName()+" dökümanı başarıyla imzalanmıştır.","İmzalama Başarılı",JOptionPane.INFORMATION_MESSAGE);
							}
						}
					} 
				}catch(Exception e1) {
					LogCreator.error("The document couldn't be signed due to:" +e1.toString(), PasswordPage.class.getName());
					JOptionPane.showMessageDialog(null,e1.toString(),"İmzalama Başarısız",JOptionPane.ERROR_MESSAGE);
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
	
	private static final String USER_AGENT = "Mozilla/5.0";
	//private static final String POST_URL = "http://localhost:8080/DocumentSigningService/documentController/saveDocumentFromClient/";

	private static final String POST_URL = "http://10.0.0.68:8080/demo/documentController/saveDocumentFromClient/";
	
	private byte[] convertToBytes(String url) {
		Path pdfPath = Paths.get(url);
		
		byte[] pdf = null;
		try {
			pdf = Files.readAllBytes(pdfPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return pdf;
	}
	
	private void postFileToServer(DocumentModel document) throws IOException {
		
		document.setBinaryData(convertToBytes(document.getDocumentUrl()));
		
		Gson gson = new Gson();
		URL url = new URL(POST_URL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		
		String json = gson.toJson(document);
		wr.write(json.getBytes());
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} else {
			LogCreator.error("POST request for sending the signed document did not work due to response code "+responseCode, PasswordPage.class.getName());
		}
	}
	
	private boolean signDocument(String contextpath, String cardType, String password, String sourcePath, String loadPath, boolean action){
		
		try {
			Driver driver = new Driver(contextpath,cardType);
			java.security.cert.Certificate[] chain = driver.createCertificateChain(password);
			if(chain == null && action) {
				LogCreator.error("Chain returned null!", PasswordPage.class.getName());
			}
			PrivateKey pk = driver.getPrivateKey(password);
			Provider provider = driver.getPKCSProvider();
			List<CrlClient> crlList = driver.getCrlList(chain);
			OcspClient ocspClient = new OcspClientBouncyCastle();
			TSAClient tsaClient = driver.getTSAClient(chain);
			if(action) {
				driver.sign(sourcePath, loadPath, chain, pk, DigestAlgorithms.SHA256, provider.getName(), CryptoStandard.CMS, "Sign", "intecon", crlList, ocspClient, tsaClient, 0);
			}
			
		}catch(Exception e) {
			if(action) {
				LogCreator.error(e.toString(), PasswordPage.class.getName());
			}
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
	
	private String trimTrid(String docName) {
		String tridWithExt = docName.split("_")[1];
		String trid = "";
		if (tridWithExt.indexOf(".") > 0)
			trid = tridWithExt.substring(0, tridWithExt.lastIndexOf("."));
		return trid;
	}
	
	private void deleteFileFromDirectory(String directory) {
		try
        { 
            Files.deleteIfExists(Paths.get(directory)); 
        } 
        catch(NoSuchFileException e1) 
        { 
        	LogCreator.error("Döküman imzalandı fakat dosya yerelden silinemedi(UnSigned folder):" + e1.toString(), PasswordPage.class.getName());
            System.out.println("No such file/directory exists"); 
        } 
        catch(DirectoryNotEmptyException e1) 
        { 
        	LogCreator.error("Döküman imzalandı fakat dosya yerelden silinemedi(UnSigned folder):" + e1.toString(), PasswordPage.class.getName());
            System.out.println("Directory is not empty."); 
        } 
        catch(IOException e1) 
        { 
        	LogCreator.error("Döküman imzalandı fakat dosya yerelden silinemedi(UnSigned folder):" + e1.toString(), PasswordPage.class.getName());
            e1.printStackTrace();
        } 
	}
}
