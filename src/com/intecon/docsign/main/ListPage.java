package com.intecon.docsign.main;

import java.awt.EventQueue;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.apache.commons.io.FilenameUtils;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;

public class ListPage {

	private JFrame frame;
	private java.util.List<DocumentModel> selectedDocs = new ArrayList<>();
	private final String UNSIGNED_URL = "C:/Temp/UnSigned/";

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ListPage window = new ListPage();
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ListPage() {
		initialize();
	}
	
	private void initialize() {
		
		setFrame(new JFrame());
		getFrame().setBounds(100, 100, 713, 549);
		
		JScrollPane scrollPane = new JScrollPane();
		
		scrollPane.setBounds(24, 102, 472, 377);
		
		JPanel panel = new JPanel();
		
		panel.setBounds(36, 105, 455, 370);
		panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
		scrollPane.setViewportView(panel);;
		
		JButton selectAll = new JButton("Tümünü Seç");
		selectAll.setBounds(24, 75, 172, 21);
		
		java.util.List<File> files = null;
		File path = new File(UNSIGNED_URL);
		files = getFilesFromFolder(path);
		java.util.List<DocumentModel> docList = new ArrayList<>();
		
		for(File theFile : files) {
			DocumentModel theDocument = new DocumentModel();
			Date createDate = new Date(theFile.lastModified());
			String pattern = "dd/MM/yyyy HH:mm:ss";
			DateFormat df = new SimpleDateFormat(pattern);
			theDocument.setCrt_date(df.format(createDate));
			theDocument.setClient(ClientAppMain.getMacId());
			theDocument.setDocumentUrl(theFile.getAbsolutePath());
			theDocument.setName(theFile.getName());
			theDocument.setFileExtension("."+FilenameUtils.getExtension(theDocument.getDocumentUrl()));
			docList.add(theDocument);
		}
		
		JButton btnSign = new JButton("Seçiliyi İmzala");
		JButton btnDelete = new JButton("Seçiliyi Çıkar");
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Here is gonna be filled with the commands if the user want to delete a document without signing
			}
		});
		if(docList.isEmpty()) {
			JOptionPane.showMessageDialog(null,"İmza bekleyen dökümanınız bulunmamaktadır!","Klasör Boş",JOptionPane.INFORMATION_MESSAGE);
			frame.dispose();
		}else {
			for(DocumentModel document : docList) {
				JCheckBox cb = new JCheckBox(document.getName().split("_")[0]);
				cb.setFont(new Font("Tahoma", Font.BOLD, 14));
				cb.setSize(118, 37);
				cb.addItemListener(new ItemListener() {
	
				    public void itemStateChanged(ItemEvent e) {
				    	JCheckBox theCheckBox = null;
				    	btnSign.setEnabled(false);
						btnDelete.setEnabled(false);
						for(int i =0 ; i<panel.getComponentCount(); i++) {
							theCheckBox=(JCheckBox)panel.getComponent(i);
							if(theCheckBox.isSelected()) {
								btnSign.setEnabled(true);
								btnDelete.setEnabled(true);
								break;
							}
						}
				    }
				});
				panel.add(cb);
				panel.revalidate();
				panel.repaint();
			}
		}
		btnSign.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				
				JCheckBox theCheckBox = null;
				String selectedItem = null;

				selectedDocs.clear();
				for(int i =0 ; i<panel.getComponentCount(); i++) {
					theCheckBox=(JCheckBox)panel.getComponent(i);
					if(theCheckBox.isSelected()) {
						selectedItem = theCheckBox.getText();
						for(DocumentModel document : docList) {
							if( document.getName().split("_")[0].equals(selectedItem)) {
								selectedDocs.add(document);
							}
						}
					}
				}
				
				if(selectedDocs.size()>1) {
					for (Frame frame : Frame.getFrames()) {
						if(frame.getName().equals(PasswordPage.class.getSimpleName())) {
							frame.dispose();
						}
					}
					EventQueue.invokeLater(new Runnable() {
		    			public void run() {
		    				UIUtils.setPreferredLookAndFeel();
		    				NativeInterface.open();
		    				try {
		    					PasswordPage view = new PasswordPage(selectedDocs,frame);
		    					view.getFrame().setVisible(true);
		    				} catch (Exception e) {
		    					e.printStackTrace();
		    				}
		    			}
		    		});
				}else {
					for (Frame frame : Frame.getFrames()) {
						if(frame.getName().equals(PdfViewPage.class.getSimpleName())) {
							frame.dispose();
						}
					}
					EventQueue.invokeLater(new Runnable() {
		    			public void run() {
		    				NativeInterface.open();
		    				try {
		    					PdfViewPage view = new PdfViewPage(selectedDocs.get(0),frame);
		    					view.getFrame().setVisible(true);
		    				} catch (Exception e) {
		    					e.printStackTrace();
		    				}
		    			}
		    		});
				}
			}
		});
		
		btnSign.setBounds(506, 102, 164, 101);
		btnSign.setFont(new Font("Tahoma", Font.BOLD, 15));
		frame.getContentPane().setLayout(null);
		getFrame().getContentPane().add(btnSign);
		
		btnDelete.setBounds(506, 246, 164, 92);
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 15));
		getFrame().getContentPane().add(btnDelete);
		
		btnSign.setEnabled(false);
		btnDelete.setEnabled(false);
		
		JButton btnExit = new JButton("İPTAL");
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				getFrame().dispose();
			}
		});
		btnExit.setBounds(506, 387, 164, 92);
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 15));
		getFrame().getContentPane().add(btnExit);
		
		JLabel lblNewLabel = new JLabel("İmza Bekleyen Dökümanlar");
		lblNewLabel.setBounds(117, 32, 258, 28);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		getFrame().getContentPane().add(lblNewLabel);
		
		selectAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				JCheckBox theCheckBox = null;
				if(selectAll.getText() =="Tümünü Seç") {
					for(int i =0 ; i<panel.getComponentCount(); i++) {
						theCheckBox=(JCheckBox)panel.getComponent(i);
						theCheckBox.setSelected(true);
					}
					selectAll.setText("Tüm Seçmeleri Kaldır");
					btnSign.setEnabled(true);
					btnDelete.setEnabled(true);
				}else {
					for(int i =0 ; i<panel.getComponentCount(); i++) {
						
						theCheckBox=(JCheckBox)panel.getComponent(i);
						theCheckBox.setSelected(false);
					}
					selectAll.setText("Tümünü Seç");
					btnSign.setEnabled(false);
					btnDelete.setEnabled(false);
				}
			}
		});
		
		frame.getContentPane().add(selectAll);
		frame.getContentPane().add(scrollPane);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
	
	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
		frame.setTitle("Döküman Listesi");
		frame.setName(this.getClass().getSimpleName());
	}
}

