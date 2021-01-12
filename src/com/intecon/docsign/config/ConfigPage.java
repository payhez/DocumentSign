package com.intecon.docsign.config;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.intecon.docsign.config.PropertyProcess;

import java.awt.Font;
import java.awt.Window;

import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConfigPage {

	private JFrame frmAyarlar;
	private JTextField macIdText;
	private JTextField usernameText;
	private JTextField passwordText;
	private PropertyProcess prop = new PropertyProcess();
	private JLabel lblKul;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigPage window = new ConfigPage();
					window.frmAyarlar.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ConfigPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAyarlar = new JFrame();
		frmAyarlar.setTitle("Ayarlar");
		frmAyarlar.setBounds(100, 100, 495, 311);
		frmAyarlar.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAyarlar.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("MacID:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setBounds(94, 87, 74, 19);
		frmAyarlar.getContentPane().add(lblNewLabel);
		
		macIdText = new JTextField();
		macIdText.setFont(new Font("Tahoma", Font.PLAIN, 15));
		macIdText.setBounds(178, 88, 201, 19);
		frmAyarlar.getContentPane().add(macIdText);
		macIdText.setColumns(10);
		macIdText.setText(prop.getMacId());
		
		usernameText = new JTextField();
		usernameText.setFont(new Font("Tahoma", Font.PLAIN, 15));
		usernameText.setColumns(10);
		usernameText.setBounds(178, 129, 201, 19);
		frmAyarlar.getContentPane().add(usernameText);
		usernameText.setText(prop.getUsername());
		
		JLabel lblKullancAd = new JLabel("Kullanıcı Adı:");
		lblKullancAd.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblKullancAd.setBounds(44, 128, 142, 19);
		frmAyarlar.getContentPane().add(lblKullancAd);
		
		passwordText = new JTextField();
		passwordText.setFont(new Font("Tahoma", Font.PLAIN, 15));
		passwordText.setColumns(10);
		passwordText.setBounds(178, 170, 201, 19);
		frmAyarlar.getContentPane().add(passwordText);
		passwordText.setText(prop.getPassword());
		
		JLabel lblifre = new JLabel("Şifre:");
		lblifre.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblifre.setBounds(112, 169, 74, 19);
		frmAyarlar.getContentPane().add(lblifre);
		
		JButton updateButton = new JButton("Güncelle");
		updateButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure on update?");
				if(dialogResult == JOptionPane.YES_OPTION){
				  prop.write(macIdText.getText(), usernameText.getText(), passwordText.getText());
				  frmAyarlar.dispose();
				}
			}
		});
		updateButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		updateButton.setBounds(178, 218, 133, 36);
		frmAyarlar.getContentPane().add(updateButton);
		
		lblKul = new JLabel("Kullanıcı Bilgileri");
		lblKul.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblKul.setBounds(168, 34, 172, 33);
		frmAyarlar.getContentPane().add(lblKul);
		
		frmAyarlar.setResizable(false);
        frmAyarlar.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public Window getFrame() {
		// TODO Auto-generated method stub
		return frmAyarlar;
	}

}
