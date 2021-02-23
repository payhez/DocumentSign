package com.intecon.docsign.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.intecon.docsign.model.LogModel;
import com.intecon.docsign.service.ConfigService;
import com.intecon.log.LogCreator;
import com.intecon.socket.client.SocketClient;

import javax.swing.JLabel;

public class LogPage {

	private JFrame frame;
	private JTable table;
	
	public static final Color RED= new Color(250,166,166);
	public static final Color GREEN= new Color(197, 250, 166);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogPage window = new LogPage();
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
	public LogPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Kullanıcı İşlem Geçmişi");
		frame.setBounds(0, 0, 1424, 700);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setName(this.getClass().getSimpleName());
		frame.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 77, 1335, 572);
		frame.getContentPane().add(scrollPane);
		
		String column[]={"İŞLEM","İŞLEM TARİHİ","İŞLEM SONUCU", "İŞLEM NO", "İŞLEM DETAYI"};
		DefaultTableModel model = new DefaultTableModel(column, 0) {
			
			private static final long serialVersionUID = 1L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		
		List<LogModel> logList = null;
		try {
			logList = SocketClient.getLogsForUser();
		} catch (IOException e) {

			LogCreator.error("Could not get log list due to : " + e.toString(), LogPage.class.getName());
		}
		SimpleDateFormat formatter = new SimpleDateFormat(ConfigService.getDateTimeStyle());
		Collections.sort(logList,Collections.reverseOrder()); // Sorts the list by IssueTime
		for (LogModel logModel : logList) {
			Object [] row  = {logModel.getIssue(), formatter.format(logModel.getIssueTime()), logModel.getIssueResult(), logModel.getTrid(), logModel.getDetails()};
			model.addRow(row);
		}
		
		table = new JTable(model);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public Component getTableCellRendererComponent(JTable table,
		            Object value, boolean isSelected, boolean hasFocus, int row, int col) {

		        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

		        String status = (String)table.getModel().getValueAt(row, 2);
		        if ("BAŞARILI".equals(status)) {
		            setBackground(GREEN);
		        } else {
		            setBackground(RED);
		        }       
		        return this;
		    }   
		});
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(300);
		table.getColumnModel().getColumn(4).setPreferredWidth(560);
		table.setRowHeight(table.getRowHeight() + 16);
		scrollPane.setViewportView(table);
		table.setFont(new Font("Arial", Font.BOLD, 14));
		
		JLabel lblIlemGemii = new JLabel("İşlem Geçmişi");
		lblIlemGemii.setFont(new Font("Arial Unicode MS", Font.BOLD, 30));
		lblIlemGemii.setBounds(596, 10, 223, 35);
		frame.getContentPane().add(lblIlemGemii);
	}
	
	public JFrame getFrame() {
		return frame;
	}
}
