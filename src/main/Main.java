package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import network.client.Chat;
import network.client.Client;
import network.server.Server;

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

			try {
				Client c = new Client("localhost", 1337, "B");
				Client d = new Client("localhost", 1337, "A");
				
				Thread.sleep(10000);
				
				c.send("mA\\Hallo");
				c.send("mA\\Hallo");
				c.send("mA\\Hallo");
				c.send("mA\\Hallo");

				Thread.sleep(10000);
				
				System.out.println(c.chats.size());
				
				Chat chat = c.chats.get(0);
				
				System.out.println("messages from 'B' to 'A':" + chat.messages.size());
				for (String s : chat.messages) {
					System.out.println(s);
				}
				System.out.println();
				
			} catch (Exception e) {
				e.printStackTrace();
				sc.close();
				return;
			}
			
		} else {
			System.out.println("that was not one of the options!");
		}

		sc.close();
	}

}
