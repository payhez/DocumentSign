package com.intecon.docsign.view;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.intecon.docsign.model.DocumentModel;
import com.intecon.docsign.service.ApplicationService;
import com.intecon.docsign.service.ConfigService;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
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
	public ListPage() {
		initialize();
	}
	
	private void initialize() {
		setFrame( new JFrame());
		
		JScrollPane scrollPane = new JScrollPane();
		JButton btnSign = new JButton("Seçiliyi İmzala");
		JButton btnDelete = new JButton("Seçiliyi Çıkar");
		JButton selectAll = new JButton(selectAllText);
		
		String column[]={"SEÇ","DOKÜMAN ADI","DOKÜMAN TARİHİ","DOKÜMAN SAHİBİ"};
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
		
		java.util.List<DocumentModel> docList = appService.checkUnsignedDocumentsFromServer();
		File path = new File(UNSIGNED_URL);
		java.util.List<File> files = appService.getFilesFromFolder(path);
		
		if(docList.isEmpty()) {
			JOptionPane.showMessageDialog(null,"İmza bekleyen dokümanınız bulunmamaktadır!","Klasör Boş",JOptionPane.INFORMATION_MESSAGE);
			frame.dispose();
		}else {
			table = new JTable(model);
			table.getColumnModel().getColumn(0).setPreferredWidth(20);
			table.getColumnModel().getColumn(0).setResizable(false);
			table.getColumnModel().getColumn(1).setPreferredWidth(320);
			table.getColumnModel().getColumn(2).setPreferredWidth(200);
			table.getColumnModel().getColumn(3).setPreferredWidth(100);
			table.setRowHeight(table.getRowHeight() + 14);
			scrollPane.setViewportView(table);
			table.setFont(new Font("Tahoma", Font.BOLD, 14));
			table.setRowSelectionAllowed(false);
			table.getModel().addTableModelListener(new TableModelListener() {
				
				@Override
				public void tableChanged(TableModelEvent e) {
					btnSign.setEnabled(false);
	        		btnDelete.setEnabled(false);
	        		selectAll.setText(selectAllText);
					for (int i = 0; i < table.getRowCount(); i++) {
						if(table.getValueAt(i, 0) == Boolean.TRUE) {
							btnSign.setEnabled(true);
			        		btnDelete.setEnabled(true);
			        		selectAll.setText(unSelectAllText);
						}
					}
					
				}
			});
		}
		
		for (DocumentModel documentModel : docList) {
			Object [] row  = {Boolean.FALSE, documentModel.getName(),documentModel.getCrt_date(), documentModel.getClient()};
			model.addRow(row);
			for(File theFile : files) {
				if(appService.trimTrid(theFile.getName()).equals(documentModel.getTrid())){
					documentModel.setDocumentUrl(theFile.getAbsolutePath());
				}
			}
		}
		frame.getContentPane().setLayout(null);
		
		scrollPane.setBounds(10, 61, 925, 434);
		frame.getContentPane().add(scrollPane);
		
		btnSign.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnSign.setEnabled(false);
		btnSign.setBounds(419, 505, 164, 101);

		btnSign.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				if(btnSign.isEnabled()) {
					String selectedItem = null;
					selectedDocs.clear();
					for (int i = 0; i < table.getRowCount(); i++) {
						if(table.getValueAt(i, 0) == Boolean.TRUE) {
							selectedItem=(String) table.getValueAt(i,1);
							for(DocumentModel document : docList) {
								if( document.getName().equals(selectedItem)) {
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
			    					PasswordPage view = new PasswordPage(selectedDocs);
			    					view.getFrame().setVisible(true);
			    					frame.setEnabled(false);
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
			    					frame.setEnabled(false);
			    				} catch (Exception e) {
			    					e.printStackTrace();
			    				}
			    			}
			    		});
					}
				}
			}
		});
		frame.getContentPane().add(btnSign);
		
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
					selectAll.setText(unSelectAllText);
					for (int i =0; i<table.getRowCount(); i++) {
		        		table.setValueAt(Boolean.TRUE, i, 0);
					}
				}else {
					selectAll.setText(selectAllText);
					for (int i =0; i<table.getRowCount(); i++) {
		        		table.setValueAt(Boolean.FALSE, i, 0);
					}
				}
			}
		});
	}
	
	public JFrame getFrame() {
		return frame;
	}
	public void setFrame(JFrame frame) {
		this.frame = frame;
		frame.setSize(963, 661);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Döküman Listesi");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		frame.setName(this.getClass().getSimpleName());
	}
}
