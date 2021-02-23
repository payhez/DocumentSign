package com.intecon.docsign.view;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.intecon.docsign.model.DocumentModel;
import com.intecon.docsign.service.ApplicationService;
import com.intecon.docsign.service.ConfigService;
import com.intecon.log.LogCreator;
import com.intecon.socket.client.SocketClient;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;

import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;

public class ListPage {

	private JFrame frame;
	private JTable table;
	private java.util.List<DocumentModel> selectedDocs = new ArrayList<>();
	private final String UNSIGNED_URL = ConfigService.getUnsignedPath();
	private final String selectAllText = "Tümünü Seç";
	private final String unSelectAllText = "Tüm Seçmeleri Kaldır";
	ApplicationService appService = new ApplicationService();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ListPage window = new ListPage();
					window.frame.setVisible(true);
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
		setFrame( new JFrame());
		String column[]={"SEÇ","DÖKÜMAN ADI","DÖKÜMAN TARİHİ","DÖKÜMAN SAHİBİ"};
		DefaultTableModel model = new DefaultTableModel(column, 0) {
			
			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		      if(column == 0) {
		    	  return true;
		      }
		       return false;
		    }
			
			@Override
			public Class getColumnClass(int column) {
				if(column == 0) {
			    	  return Boolean.class;
			      }else {
			    	  return String.class;
			      }
			  }
		};
		
		File path = new File(UNSIGNED_URL);
		java.util.List<File> files = getFilesFromFolder(path);
		java.util.List<DocumentModel> docList = null;
		
		try {
			docList = SocketClient.getUnsignedDocuments();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			LogCreator.error("getUnsignedDocuments did not work due to: " + e1.toString(), ListPage.class.getName());
		}
		appService.checkUnsignedDocumentsFromServer();
		
		final java.util.List<DocumentModel> documentList = docList;
		
		for (DocumentModel documentModel : documentList) {
			Object [] row  = {Boolean.FALSE, documentModel.getName(),documentModel.getCrt_date(), documentModel.getClient()};
			model.addRow(row);
			for(File theFile : files) {
				if(appService.trimTrid(theFile.getName()).equals(documentModel.getTrid())){
					documentModel.setDocumentUrl(theFile.getAbsolutePath());
				}
			}
		}
		frame.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 61, 925, 434);
		frame.getContentPane().add(scrollPane);
		
		JButton btnSign = new JButton("Seçiliyi İmzala");
		btnSign.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnSign.setEnabled(false);
		btnSign.setBounds(419, 505, 164, 101);

		btnSign.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				String selectedItem = null;

				selectedDocs.clear();
				int[] selectedRowIndices = table.getSelectedRows();
				for (int i = 0; i < table.getSelectedRowCount(); i++) {
					selectedItem=(String) table.getValueAt(selectedRowIndices[i],1);
					for(DocumentModel document : documentList) {
						if( document.getName().equals(selectedItem)) {
							selectedDocs.add(document);
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
		    					PasswordPage view = new PasswordPage(selectedDocs);
		    					view.getFrame().setVisible(true);
		    					frame.dispose();
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
		    					PdfViewPage view = new PdfViewPage(selectedDocs.get(0));
		    					view.getFrame().setVisible(true);
		    				} catch (Exception e) {
		    					e.printStackTrace();
		    				}
		    			}
		    		});
				}
			}
		});
		frame.getContentPane().add(btnSign);
		
		JButton btnDelete = new JButton("Seçiliyi Çıkar");
		btnDelete.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnDelete.setEnabled(false);
		btnDelete.setBounds(595, 505, 164, 101);
		frame.getContentPane().add(btnDelete);
		
		JButton btnExit = new JButton("İPTAL");
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnExit.setBounds(771, 505, 164, 101);
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				frame.dispose();
			}
		});
		frame.getContentPane().add(btnExit);
		
		JButton selectAll = new JButton(selectAllText);
		selectAll.setFont(new Font("Tahoma", Font.BOLD, 12));
		selectAll.setBounds(10, 506, 172, 41);
		frame.getContentPane().add(selectAll);
		
		JLabel lblImzaBekleyenDkmanlar = new JLabel("İmza Bekleyen Dökümanlar");
		lblImzaBekleyenDkmanlar.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblImzaBekleyenDkmanlar.setBounds(305, 24, 343, 27);
		frame.getContentPane().add(lblImzaBekleyenDkmanlar);
		selectAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if(selectAll.getText().equals(selectAllText)) {
					table.selectAll();
					selectAll.setText(unSelectAllText);
					btnSign.setEnabled(true);
					btnDelete.setEnabled(true);
					for (int row : table.getSelectedRows()) {
		        		table.setValueAt(Boolean.TRUE, row, 0);
					}
				}else {
					table.clearSelection();
					selectAll.setText(selectAllText);
					btnSign.setEnabled(false);
					btnDelete.setEnabled(false);
					for (int i =0; i<table.getRowCount(); i++) {
		        		table.setValueAt(Boolean.FALSE, i, 0);
					}
				}
				
			}
		});
		
		if(docList.isEmpty()) {
			JOptionPane.showMessageDialog(null,"İmza bekleyen dökümanınız bulunmamaktadır!","Klasör Boş",JOptionPane.INFORMATION_MESSAGE);
			frame.dispose();
		}else {
			table = new JTable(model);
			table.getColumnModel().getColumn(0).setPreferredWidth(20);
			table.getColumnModel().getColumn(1).setPreferredWidth(320);
			table.getColumnModel().getColumn(2).setPreferredWidth(200);
			table.getColumnModel().getColumn(3).setPreferredWidth(100);
			table.setRowHeight(table.getRowHeight() + 14);
			scrollPane.setViewportView(table);
			table.setFont(new Font("Tahoma", Font.BOLD, 14));
			table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
		        public void valueChanged(ListSelectionEvent event) {
		        	if(table.getSelectionModel().isSelectionEmpty()) {
		        		btnSign.setEnabled(false);
		        		btnDelete.setEnabled(false);
		        		selectAll.setText(selectAllText);
		        		for (int i =0; i<table.getRowCount(); i++) {
			        		table.setValueAt(Boolean.FALSE, i, 0);
						}
		        	}else {
		        		btnSign.setEnabled(true);
		        		btnDelete.setEnabled(true);
		        		selectAll.setText(unSelectAllText);
		        		for (int i =0; i<table.getRowCount(); i++) {
			        		table.setValueAt(Boolean.FALSE, i, 0);
						}
		        		int[] selectedRowIndices = table.getSelectedRows();
		        		for (int indice : selectedRowIndices) {
			        		table.setValueAt(Boolean.TRUE, indice, 0);

						}
		        	}
		        }
		    });
		}
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
		frame.setBounds(100, 100, 963, 661);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Döküman Listesi");
		frame.setName(this.getClass().getSimpleName());
	}
}
