package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import gui.general.Frame;
import network.Client;
import network.Server;

public class Main {
	
	public static void main(String[] args) {
		
		System.out.println("s => Server\nc => Client");

		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();
		if (input.equals("s")) {
			Server.start(1337);
			File f = new File("rsc\\serverLog.txt");
			byte[] bs = new byte[0];
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				FileInputStream in;
				try {
					in = new FileInputStream(f);
					bs = in.readAllBytes();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			FileOutputStream fout;
			
			try {
				fout = new FileOutputStream(f);
				fout.write(bs);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			Server.setLogger(fout);
			Server.startSession();
			Server.run();
			
			try {
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (input.equals("c")) {

			Client c = new Client();
			File logFile = new File("rsc\\clientLog.txt");
			
			for(int i = 0; true; i++) {
				logFile = new File("rsc\\clientLog" + i + ".txt");
				if(!logFile.exists())
					break;
			}
			
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				logFile = null;
			}
			if(logFile == null)	c.setLogger(System.out);
			else 				c.setLogger(logFile);
			
			Frame f = new Frame(c);
			f.requestFocus();
			
		} else {
			System.out.println("that was not one of the options!");
		}

		sc.close();
	}

}
