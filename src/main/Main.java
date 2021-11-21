package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
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
			File f = new File("rsc\\serverLog.txt");
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			FileOutputStream fout;
			
			try {
				fout = new FileOutputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			
			Server.setLogger(fout);
			Server.log("Session: " + new Timestamp(System.currentTimeMillis()));
			Server.laufen();
			
			try {
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (in.equals("c")) {

			System.out.print("Host IP:");
			String host = sc.nextLine();
			System.out.print("Port:");
			int port = Integer.valueOf(sc.nextLine());
			System.out.print("Preferred Name:");
			String name = sc.nextLine();

			Client c = new Client();
			c.registerToServer(host, port, name);
			Client.ClientThread ct = new ClientThread(c);
			ct.start();

//			c.send("Hello, World!");

			while (true) {
				String s = sc.nextLine();
				if (s.startsWith("-f")) {
					try {
						Scanner fsc = new Scanner(new File(s.substring(2).strip()));
						s = "";
						while (fsc.hasNextLine()) {
							s += "\n" + fsc.nextLine();
						}
					} catch (FileNotFoundException e) {
						System.err.println("Error: Could not find file \"" + s.substring(2).strip() + "\"");
						e.printStackTrace();
					}
				}

				c.send(s);

				if (s.equals("q") || !c.isWaiting())
					break;

			}
		} else {
			System.out.println("that was not one of the options!");
		}

		sc.close();
	}

}
