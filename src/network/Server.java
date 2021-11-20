package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
	
	public static ServerSocket server;
	
	public static HashMap<String, Socket> clients = new HashMap<String, Socket>();
	
	public static int clientCounter = 0;
	
	public static void start(int port) {
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(300000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void laufen() { 
		while(true) {
			try {
				Socket client = server.accept();
//				System.out.println("Input detected...\nDecrypting...");
				DataInputStream in = new DataInputStream(client.getInputStream());
				String s = in.readUTF();
				
				new ServerThread(s, client).start();
				
				if(clients.containsKey(s)) {
					while(!clients.containsKey(Integer.toString(++clientCounter))) {
						clients.put(Integer.toString(clientCounter), client);
					}
					new DataOutputStream(client.getOutputStream()).writeUTF("n" + clientCounter);
				}else {
					clients.put(s, client);
					new DataOutputStream(client.getOutputStream()).writeUTF("n" + s);
				}
				
//				System.out.println(s);
//				char[] key = s.substring(0, 8).toCharArray();
//				s = s.substring(8);
//				String binStrKey = "";
//				for (char c : key) {
//					String bits = Integer.toBinaryString(c);
//					while (bits.length() < 16) bits = "0" + bits;
//					binStrKey += bits;
//				}
//				
//				String msg = Decryption.decrypt(s, binStrKey);
//				System.out.println("Decrypted messsage:\n" + msg);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
