package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import aes.Encryption;
import aes.KeySchedule;

public class Client {

	Socket client;
	DataOutputStream out;
	DataInputStream in;

	String[] contacts;

	String host;
	String name;
	int serverPort;
	
//	public CopyOnWriteArrayList<String> msgQueue = new CopyOnWriteArrayList<>();

	private boolean loop = true;
	private int sendAttempts = 0;

	public boolean isWaiting() {return loop;}
	
	public void registerToServer(String host, int port, String name) {
		this.host = host;
		this.serverPort = port;
		
		try {
			client = new Socket(host, port);
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
			out.writeUTF(name);
			out.writeUTF("<c");

		} catch (UnknownHostException e) {
			System.err.println("Could not find Server at " + host + ":" + port);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String s) {
		if (s.equals("q")) {
			deRegister();
		}else if (s.startsWith("me")) {// wants to encrypt message
			
			String msg = s.substring(2);
			String name;

			if (msg.startsWith("\"")) {// name in ""
				if (!msg.substring(1).contains("\"")) {
					System.err.println("Message syntax error:\n\tCould not detect end of receiver name");
					return;
				}
				name = msg.substring(1).split("\"")[0];
				msg = msg.substring(name.length() + 2);// "name" gets stripped away from message
			} else if (msg.contains("\\") && !msg.startsWith("\\")) { // name ends with \
				name = msg.split("\\")[0];
				msg = msg.substring(name.length() + 1);// name\ gets stripped away from message
			} else {
				System.err.println("Message syntax error:\n\tCould not detect receiver name");
				return;
			}

			if (msg.startsWith(" ")) {// aes as standard encryption: message is altered to specific aes encryption request
				msg = "aes" + msg.substring(1);
			}
			if (msg.startsWith("aes")) {// specific request to encrypt with aes
				msg = msg.substring(3);

				
				// encrypting the main message
				String encrypted = Encryption.encrypt(msg);
				String key = KeySchedule.Key;

				// converting key to char[8]
				char[] keyAschars = new char[8];

				String[] splitKey = key.split("(?<=\\G.{16})");

				for (int i = 0; i < 8; i++) {
					keyAschars[i] = (char) Integer.parseInt(splitKey[i], 2);
				}
				
				// preparing message head
				String send = "me" + name + "\\aes";
				
				// adding key
				for (char c : keyAschars) {
					send += "" + c;
				}
				
				// adding encrypted message
				send += encrypted;
				
				
				if(out == null){// OutputStream is not yet defined
					try {
						out = new DataOutputStream(client.getOutputStream());
					} catch (IOException e) {
						System.err.println("I/O error: could not get DataOutputStream:");
						e.printStackTrace();
						if(sendAttempts++ > 10) {
							System.err.println("problem constantly recurring...\nshutdown initiated...");
							deRegister();
							return;
						}
						System.err.println("rebooting client...");
						reboot();
						System.err.println("resending message...");
						send(s);
						return;
					}
				}
				
				sendAttempts = 0;
				
				try {// sending message to Server
					out.writeUTF(send);
				} catch (IOException e) {
					System.err.println("I/O Error while sending message:");
					e.printStackTrace();
					return;
				}
			}
		}else {

			try {
				out.writeUTF(s);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public void waitForMessage() {// waiting for Messages from Server or User
		try {
			in = new DataInputStream(client.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		while (loop) {
			System.out.println("test");
			
			try {
				String s = in.readUTF();
				if (s.startsWith(">c")) {
					if (s.length() > 3) {
						s = s.substring(3);
						contacts = s.split(" ");
						System.out.println(s.replace(' ', '\n').replace(name, "").replace("\n\n", "\n"));
					}
				} else if (s.startsWith("n")) {
					name = s.substring(1);
					System.out.println("name changed to: \"" + name + "\"");
				}
			} catch (IOException e) {
				if (loop)e.printStackTrace();
			}
			
		}
	}

	public void deRegister() {
		System.out.println("disconnecting...");
		try {
			out.writeUTF("q");
			loop = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reboot() {
		deRegister();
		try {
			Thread.sleep(1000);// waiting for server to complete deRegistration
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		registerToServer(host, serverPort, name);
	}

	public static class ClientThread extends Thread{
		
		private Client client;
		
		public ClientThread(Client client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			while(client.loop)client.waitForMessage();
		}
	}

}

