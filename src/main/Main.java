package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import network.Client;
import network.Client.ClientThread;
import network.Server;

public class Main {

	public static void main(String[] args) {
		System.out.println("s => Server\nc => Client");

		Scanner sc = new Scanner(System.in);
		String in = sc.nextLine();
		if (in.equals("s")) {
			Server.start(1337);
			Server.laufen();
		} else if (in.equals("c")) {
//			String s = "Hallo, Welt";
//
//			// encryption
//			String encrypted = Encryption.encrypt(s);
////			System.out.println(encrypted);
//
//			// binaryString to char[] conversion
//			char[] keyAschars = new char[8];
//
//			String[] splitKey = KeySchedule.Key.split("(?<=\\G.{16})");
//
//			for (int i = 0; i < 8; i++) {
//				keyAschars[i] = (char) Integer.parseInt(splitKey[i], 2);
//				for (int j = 0; j < 16; j++) {
////					System.out.print(((keyAschars[i] & (1 << (15 - j))) != 0) ? "1" : "0");
//				}
//			}
//			
////			System.out.println(Decryption.decrypt(encrypted, KeySchedule.Key));
//			
//			String send = "meaes";
//			
//			for(char c : keyAschars) {
//				send += "" + c;
//			}
//			
////			System.out.println(send.length());
//			
//			send += encrypted;
//			
////			System.out.println(send.length());
////			
////			
////			System.out.println();
////
////			System.out.println();
////			for(char c : send.toCharArray()) {
////				System.out.println((int)c);
////			}
////			System.out.println();
////			System.out.println();
			
			System.out.print("Host IP:");
			String host = sc.nextLine();
			System.out.print("Port:");
			int port = Integer.valueOf(sc.nextLine());
			System.out.print("Prefered Name:");
			String name = sc.nextLine();
			
			Client c = new Client();
			c.registerToServer(host, port, name);
			Client.ClientThread ct = new ClientThread(c);
			ct.start();
			
//			c.send("Hello, World!");
			
			while(true) {
				String s = sc.nextLine();
				System.out.println(s);
				if(s.startsWith("-f")) {
					try {
						Scanner fsc = new Scanner(new File(s.substring(2).strip()));
						s = "";
						while (fsc.hasNextLine()) {
							s += fsc.nextLine();
						}
					} catch (FileNotFoundException e) {
						System.err.println("Error: Could not find file \"" + s.substring(2).strip() + "\"");
						e.printStackTrace();
					}
				}

				c.send(s);
				
				if(s.equals("q") || !c.isWaiting()) break;
				
			}
		}else {
			System.out.println("that was not one of the options!");
		}
		
		sc.close();
	}

}
