package com.intecon.docsign.view;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.Font;

public class ProgressPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JProgressBar progressBar;

	/**
	 * Create the panel.
	 */
	public ProgressPanel(int min, int max) {
		setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
		progressBar.setBounds(10, 87, 433, 28);
		add(progressBar);
		
		JLabel lblDkmanlarImzalanyorLtren = new JLabel("Dökümanlar imzalanıyor. Lütren Bekleyiniz...");
		lblDkmanlarImzalanyorLtren.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblDkmanlarImzalanyorLtren.setBounds(67, 42, 332, 13);
		add(lblDkmanlarImzalanyorLtren);
	}
	
	public void updateBar(int newValue) {
		progressBar.setValue(newValue);
	  }
}
