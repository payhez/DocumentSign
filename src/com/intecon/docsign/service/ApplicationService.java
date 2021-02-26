package com.intecon.docsign.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.intecon.docsign.main.ClientAppMain;
import com.intecon.docsign.model.DocumentModel;
import com.intecon.docsign.view.PasswordPage;
import com.intecon.log.LogCreator;
import com.intecon.socket.client.SocketClient;

public class ApplicationService {
	
	public static final String SIGNED_URL = ConfigService.getSignedPath();
	public static final String UNSIGNED_URL = ConfigService.getUnsignedPath();
	
	  public void clearUnsignedFolder() {
		File path = new File(UNSIGNED_URL);
		if(path.exists()) {
			 for (final File dateFolder : path.listFiles()) {
				if(dateFolder.isDirectory() && dateFolder.list().length == 0) {
					dateFolder.delete();
				}
			 }
		 }
	  }
	  
	  public List<DocumentModel>  checkUnsignedDocumentsFromServer() {
		  
		  List<String> tridList = getExistingTridList();
		  List<DocumentModel> docList = new ArrayList<DocumentModel>();
		  try {
			  	docList = SocketClient.getUnsignedDocuments();
				for(DocumentModel doc : docList) {
					boolean found = false;
					for(String theTrid : tridList) {
						if(doc.getTrid().equals(theTrid)) {
							found = true;
						}
					}
					if(!found) {
						convertBytesToFile(SocketClient.getBytesByTrid(doc.getTrid()), doc.getName() + "_" + doc.getTrid());
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LogCreator.error("checkUnsignedDocumentsFromServer did not work due to: " + e.toString(), ClientAppMain.class.getName());
				return null;
			}
		  return docList;
	  }
	  
	  public  String trimTrid(String docName) {
		  if(docName.indexOf("_") != -1) {
			String tridWithExt = docName.split("_")[1];
			String trid = "";
			if (tridWithExt.indexOf(".") > 0) {
				trid = tridWithExt.substring(0, tridWithExt.lastIndexOf("."));
			}
			return trid;
		  }
		  LogCreator.error("Tridsiz dosya ("+docName+ ") var.",ClientAppMain.class.getSimpleName());
		  return null;
		} 
	  
	  private  List<String> getExistingTridList(){
		  File path = new File(UNSIGNED_URL);
		  List<String> tridList = new ArrayList<String>();
		  if(path.exists()) {
			 for (final File dateFolder : path.listFiles()) {
				if(dateFolder.isDirectory()) {
					for(final File document : dateFolder.listFiles()) {
						String trid = trimTrid(document.getName());
						if(trid != null) {
							tridList.add(trid);
						}
					}
				}
			 }
		  }
		  
		  return tridList;
	  }
	  
	  public void createFolders() {
		  File theDir = new File(SIGNED_URL);    
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
			theDir = new File(UNSIGNED_URL);
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
	  }
	  
	  public void deleteFileFromDirectory(String directory) {
			try
	        { 
	            Files.deleteIfExists(Paths.get(directory)); 
	        } 
	        catch(Exception e1) 
	        { 
	        	LogCreator.error("Döküman imzalandı fakat dosya yerelden silinemedi(UnSigned folder):" + e1.toString(), PasswordPage.class.getName());
	        }
		}
	  
	  public boolean checkUnsignedFiles() {
			 File path = new File(ConfigService.getUnsignedPath());
			 if(!path.exists() || path.list().length == 0) {
				 return false;
			 }
			 return true;
	 	}
	  
	  public java.util.List<File> getFilesFromFolder(File folder) {
			
			java.util.List<File> files = new ArrayList<File>();
		    for (final File fileEntry : folder.listFiles()) {
		        if (fileEntry.isDirectory()) {
		        	files.addAll(getFilesFromFolder(fileEntry));
		        } else {
		        	files.add(fileEntry);
		        }
		    }
			return files;
		}
	  
	  public void convertBytesToFile(byte[] bytes, String fileName) {
	    	String pathWithDate = ConfigService.getUnsignedPath() + LocalDate.now().toString() + "/";
	    	File theDir = new File(pathWithDate);
	 		if (!theDir.exists()){
	 		    theDir.mkdirs();
	 		}
	    	 try (FileOutputStream fos = new FileOutputStream(pathWithDate+fileName+".pdf")) {
	    		   fos.write(bytes);
	    		} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	     }
	     
     public byte[] convertDocToByteArray(String path)throws FileNotFoundException, IOException{
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
     
     public String getMacId() {
 		String macId = null;
 		
 		try {
 			InetAddress localHost = InetAddress.getLocalHost();
 			NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
 			macId = ni.getHardwareAddress().toString();
 			macId= "furat";
 		}catch(Exception e) {
 			LogCreator.error("Couldn't get MAC Address due to: "+e.toString(), ConfigService.class.getName());
 		}
 		
 		return macId;
 	}
}
