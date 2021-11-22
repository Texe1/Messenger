package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
	
	public static ServerSocket server;
	
	public static CopyOnWriteArrayList<String> clientNames = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Socket> clients = new CopyOnWriteArrayList<>();
	
	public static int clientCounter = 0;
	
	public static OutputStream logger;
	
	public static void start(int port) {
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(300000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setLogger(OutputStream out) {
		logger = out;
		log("Session: " + new Timestamp(System.currentTimeMillis()));
	}
	
	
	public static void log(String s) {
		if(logger == null)logger = System.out;
		
		s = "<" + new Timestamp(System.currentTimeMillis()) + ">\t" + s + "\n";
		
		try {
			logger.write(s.getBytes());
			logger.flush();
		} catch (IOException e) {
			System.err.println("Could not access logger, switching to command prompt.");
			logger = System.out;
			System.out.println(s);
		}
	}
	
	public static void startSession() {
		if(logger == null)logger = System.out;
		
		String s = "____________________\n<" + new Timestamp(System.currentTimeMillis()) + ">\tNew session started\n";
		
		try {
			logger.write(s.getBytes());
			logger.flush();
		} catch (IOException e) {
			System.err.println("Could not access logger, switching to command prompt.");
			logger = System.out;
			System.out.println(s);
		}
	}
	
	
	public static void laufen() { 
		while(true) {
			try {
				Socket client = server.accept();
				DataInputStream in = new DataInputStream(client.getInputStream());
				String name = in.readUTF();
				
				addClient(client, name);
				
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
	
	public static void addClient(Socket client, String name) {
		if(clientNames.contains(name)) {
			while(clientNames.contains(Integer.toString(++clientCounter))) {
				;
			}
			name = Integer.toString(clientCounter);
		}
		
		clients.add(client);
		clientNames.add(name);
		
		log(name + " at [" + client.getInetAddress() + ":" + client.getLocalPort() + "] has connected to the server");
		
		try {
			new DataOutputStream(client.getOutputStream()).writeUTF("n" + name);
		} catch (IOException e) {
			System.err.println("Could not send name verification message to client at [" + client.getInetAddress() + ":" + client.getLocalPort() + "]");
			e.printStackTrace();
		}
		
		new ServerThread(name, client).start();
		updateContacts();
	}
	
	public static void updateContacts() {// sends updated contacts to every client
		String namesList;
		for (int i = 0; i < clientNames.size(); i++) {
			namesList = ">c";
			for (String name : Server.clientNames) {
				if (!name.equals(clientNames.get(i)))
					namesList += " " + name;
			}
			
			try {
				new DataOutputStream(clients.get(i).getOutputStream()).writeUTF(namesList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
