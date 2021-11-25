package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import network.Client;

public class Frame extends JFrame {

	private static final long serialVersionUID = -8246131571704187284L;

	public Frame(Client c) {
		setSize(1000, 1000);
		setLocationRelativeTo(null);
		
		getContentPane().setLayout(null);
		
		JTextField t = new JTextField("host IP");
		t.setBounds(100, 100, 800, 50);
		this.add(t);
		
		JTextField t1 = new JTextField("port");
		t1.setBounds(100, 160, 800, 50);
		this.add(t1);
		
		JTextField t2 = new JTextField("preferred name");
		t2.setBounds(100, 220, 800, 50);
		this.add(t2);
		
		JButton b = new JButton("connect");
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(c.connected) {
					b.setText("connect");
					c.disconnect();
				}else {
					b.setText("disconnect");
					c.connect(t.getText(), Integer.parseInt(t1.getText()), t2.getText());
				}
			}
		});
		add(b);
		b.setBounds(400, 850, 200, 50);
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(c.connected)c.disconnect();
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		
		setVisible(true);
	}
	
}
