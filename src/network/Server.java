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
	public static CopyOnWriteArrayList<ServerThread> threads = new CopyOnWriteArrayList<>();
	
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
	
	
	public static void run() { 
		while(true) {
			try {
				Socket client = server.accept();
				DataInputStream in = new DataInputStream(client.getInputStream());
				String name = in.readUTF();
				
				addClient(client, name);
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
		
		log(name + " at [" + client.getInetAddress() + ":" + client.getLocalPort() + "] has connected to the server");
		
		
		ServerThread t = new ServerThread(name, client);
		t.start();
		t.queueMsg("n" + name);
		threads.add(t);
		clients.add(client);
		clientNames.add(name);
		
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
			
			System.out.println(clientNames.get(i) + " " + namesList);
			
			threads.get(i).queueMsg(namesList);
			
			try {
				new DataOutputStream(clients.get(i).getOutputStream()).writeUTF(namesList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("\n");
	}
	
}
