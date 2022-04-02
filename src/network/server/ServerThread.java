package network.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import network.client.Client;

public class ServerThread extends Thread {

	String clientName;
	Socket client;
	
	boolean connected = true;
	
	public CopyOnWriteArrayList<String> outQueue = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<String>  inQueue = new CopyOnWriteArrayList<>();
	
	Thread sendingThread = new Thread("out" + clientName) {
		@Override
		public void run() {

			DataOutputStream out;
			
			try {
				out = new DataOutputStream(client.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			while (!client.isClosed()) {
				while (!outQueue.isEmpty()) {
					try {
						out.writeUTF(outQueue.get(0));
						System.out.println(outQueue.get(0));
						outQueue.remove(0);
					} catch (IOException e) {
						System.err.println(
								"I/O Error in Server Thread for client " + clientName + ", INet=" + client.getInetAddress() + ":" + client.getLocalPort());
						e.printStackTrace();
					}
					
				}
			}
		}
	};
	
	Thread receivingThread = new Thread("in" + clientName) {
		@Override
		public void run() {
			
			DataInputStream in;
			
			try {
				in = new DataInputStream(client.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			String msg;
			
			while (!client.isClosed()){
				try {
					msg = in.readUTF();
					inQueue.add(msg);
				} catch (IOException e) {
					System.err.println(
							"I/O Error in Server Thread for client " + clientName + ", INet=" + client.getInetAddress() + ":" + client.getLocalPort());
					e.printStackTrace();
				}
			}
		}
	};

	public ServerThread(String clientName, Socket client) {
		this.clientName = clientName;
		this.client = client;
	}
	
	public synchronized String getClientName() {
		return clientName;
	}
	
	public synchronized void queueMsg(String s) {
		outQueue.add(s);
	}

	public void run() {

		sendingThread.start();
		receivingThread.start();

		while (client.isConnected()) {
			while (!inQueue.isEmpty()) {
				processMsg(inQueue.get(0));
				inQueue.remove(0);
			}
		}
	}

	private synchronized void processMsg(String s) {
		if (s.strip().equals("q")) {// quit message
			Server.clients.remove(client);
			Server.clientNames.remove(clientName);
			Server.threads.remove(this);
			Server.log(clientName + " at [" + client.getInetAddress() + ":" + client.getPort() + "] has disconnected.");
//			Server.updateContacts();
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (s.startsWith("m")) { // sending a message to other client
			String name = s.substring(2, s.indexOf('\\'));
			
			if (Server.clientNames.contains(name)) {// the requested receiver is connected to the server
				int clientID = Server.clientNames.indexOf(name);
				String send = s.replaceFirst(name, clientName);
				Server.threads.get(clientID).queueMsg(send);
				
			} else {// the requested receiver is not connected to the server
				System.err.println("Server Error:\n\r\tCould not find client \"" + name + "\" for receiving message from "
						+ clientName + " at [" + client.getInetAddress() + ":" + client.getLocalPort() + "]");
			}
			
		} else if(s.startsWith("" + Client.MSG_KEY_INIT) || s.startsWith("" + Client.MSG_KEY_RESPONSE)) {
			Server.log(s);
			String name = s.substring(1, s.indexOf('\\'));
		
			if (Server.clientNames.contains(name)) {// the requested receiver is connected to the server
				int clientID = Server.clientNames.indexOf(name);
				String send = s.substring(0, 1) + this.clientName + s.substring(s.indexOf('\\'));
				Server.threads.get(clientID).queueMsg(send);
				
			} else {// the requested receiver is not connected to the server
				System.err.println("Server Error:\n\r\tCould not find client '" + name + "' for receiving key from "
						+ clientName + " at [" + client.getInetAddress() + ":" + client.getLocalPort() + "]");
			}
			
		} else {
			Server.log("<" + clientName + "> " + s + " | could not understand intentions");
		}
	}

}