package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Scanner;

import gui.Frame;
import network.Client;
import network.Client.ClientThread;
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
			Server.laufen();
			
			try {
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (input.equals("c")) {

			Client c = new Client();
			
			Frame f = new Frame(c);

//			while (true) {
//				String s = sc.nextLine();
//				if (s.startsWith("-f")) {
//					try {
//						Scanner fsc = new Scanner(new File(s.substring(2).strip()));
//						s = "";
//						while (fsc.hasNextLine()) {
//							s += "\n" + fsc.nextLine();
//						}
//					} catch (FileNotFoundException e) {
//						System.err.println("Error: Could not find file \"" + s.substring(2).strip() + "\"");
//						e.printStackTrace();
//					}
//				}
//
//				c.send(s);
//
//				if (s.equals("q") || !c.isWaiting())
//					break;
//
//			}
		} else {
			System.out.println("that was not one of the options!");
		}

		sc.close();
	}

}
