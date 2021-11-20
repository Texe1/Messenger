package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;

import aes.Decryption;
import aes.KeySchedule;

public class Server {
	
	public static ServerSocket server;
	
	public static void start(int port) {
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(100000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void laufen() { 
		while(true) {
			try {
				Socket client = server.accept();
				System.out.println("Input detected...\nDecrypting...");
				DataInputStream in = new DataInputStream(client.getInputStream());
				String s = in.readUTF();
				char[] key = s.substring(0, 8).toCharArray();
				s = s.substring(8);
				String binStrKey = "";
				for (char c : key) {
					String bits = Integer.toBinaryString(c);
					while (bits.length() < 16) bits = "0" + bits;
					binStrKey += bits;
				}
				
				String msg = Decryption.decrypt(s, binStrKey);
				System.out.println("Decrypted messsage:\n" + msg);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
