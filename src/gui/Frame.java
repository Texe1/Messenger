package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import network.Client;

public class Frame extends JFrame{
	
	Client client;
	
	public Frame(Client client) {
		setSize(1000, 1000);
		setLocationRelativeTo(null);
		setVisible(true);
		
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
				client.deRegister();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
	}
}
