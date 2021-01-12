package com.intecon.docsign.main;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

import java.awt.List;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Font;

public class ListPage {

	private JFrame frame;
	private DocumentToSign selectedDoc = null;
	private String folderPath = "C:/temp";

	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the application.
	 */
	public ListPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		setFrame(new JFrame());
		getFrame().setBounds(100, 100, 713, 488);
		getFrame().getContentPane().setLayout(null);
		
		List list = new List();
		list.setFont(new Font("Dialog", Font.BOLD, 18));
		list.setBounds(33, 61, 437, 377);
		getFrame().getContentPane().add(list);
		
		final File pathname = new File(folderPath); // For now it fetches the objects from a folder
		
		java.util.List<File> files = getFilesFromFolder(pathname);
		java.util.List<DocumentToSign> docList = new ArrayList<>();
		
		for(File theFile : files) {
			docList.add(new DocumentToSign("1",folderPath + "/" + theFile.getName(),theFile.getName(), java.time.LocalDate.now(), "rubar" , false));
		}
		
		for(DocumentToSign document : docList) {
			list.add(document.getName());
		}
		
		JButton btnSign = new JButton("Seçiliyi İmzala");
		btnSign.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnSign.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				EventQueue.invokeLater(new Runnable() {
	    			public void run() {
	    				UIUtils.setPreferredLookAndFeel();
	    				NativeInterface.open();
	    				try {
	    					PdfViewPage view = new PdfViewPage(selectedDoc);
	    					view.getFrame().setVisible(true);
	    				} catch (Exception e) {
	    					e.printStackTrace();
	    				}
	    			}
	    		});
			}
		});
		btnSign.setBounds(520, 61, 164, 101);
		getFrame().getContentPane().add(btnSign);
		
		JButton btnDelete = new JButton("Seçiliyi Çıkar");
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// will be coded for deleting documents
			}
		});
		btnDelete.setBounds(520, 207, 164, 92);
		getFrame().getContentPane().add(btnDelete);
		
		btnSign.setEnabled(false);
		btnDelete.setEnabled(false);
		
		JButton btnExit = new JButton("İPTAL");
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				getFrame().dispose();
			}
		});
		btnExit.setBounds(520, 346, 164, 92);
		getFrame().getContentPane().add(btnExit);
		
		JLabel lblNewLabel = new JLabel("İmza Bekleyen Dökümanlar");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setBounds(117, 27, 258, 28);
		getFrame().getContentPane().add(lblNewLabel);
		
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		list.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				btnSign.setEnabled(true);
				btnDelete.setEnabled(true);
				int index = list.getSelectedIndex();
				if(index>-1) {
					String selectedItem = list.getItem(index);
					for(DocumentToSign document : docList) {
						if( document.getName().equals(selectedItem)) {
							selectedDoc = document;
						}
					}
				}
			}
			
		});
	}
	
	public java.util.List<File> getFilesFromFolder(File folder) {
		
		java.util.List<File> files = new ArrayList<File>();
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	getFilesFromFolder(fileEntry);
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
	}
}
