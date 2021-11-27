package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import aes.Decryption;
import aes.Encryption;
import aes.KeySchedule;

public class Client {

	Socket client;
	DataOutputStream out;
	DataInputStream in;
	
	ClientThread ct = new ClientThread(this);

	String[] contacts;

	String host;
	String name;
	int serverPort;
	
	public boolean connected;

	private ArrayList<ArrayList<String>> chats = new ArrayList<>();

	public static final String[] encryptions = new String[] { "aes" };

//	public CopyOnWriteArrayList<String> msgQueue = new CopyOnWriteArrayList<>();

	private boolean loop = true;
	private int sendAttempts = 0;

	public boolean isWaiting() {
		return loop;
	}

	public void connect(String host, int port, String name) {
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
		
		ct.start();
		
		connected = true;
	}

	public void send(String s) {
		if (s.equals("q")) {
			disconnect();
		} else if (s.startsWith("me")) {// wants to encrypt message

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
				name = msg.substring(0, msg.indexOf('\\'));
				msg = msg.substring(name.length() + 1);// name\ gets stripped away from message
			} else {
				System.err.println("Message syntax error:\n\tCould not detect receiver name");
				return;
			}

			if (msg.startsWith(" ")) {// aes as standard encryption: message is altered to specific aes encryption
										// request
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

				if (out == null) {// OutputStream is not yet defined
					try {
						out = new DataOutputStream(client.getOutputStream());
					} catch (IOException e) {
						System.err.println("I/O error: could not get DataOutputStream:");
						e.printStackTrace();
						if (sendAttempts++ > 10) {
							System.err.println("problem constantly recurring...\nshutdown initiated...");
							disconnect();
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
		} else {

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

			try {
				String s = in.readUTF();
				if (s.startsWith(">c")) {
					if (s.length() > 3) {
						s = s.substring(3);
						contacts = s.split(" ");
						System.out.println("received updated contacts list:\n\t"
								+ s.replace(" ", "\n\t").replace(name, "").replace("\n\n", "\n"));
					}
				} else if (s.startsWith("n")) {
					name = s.substring(1);
					System.out.println("name changed to: \"" + name + "\"");
				} else if (s.startsWith("me")) {
					String origin = s.substring(2, s.indexOf('\\'));
					String encrMsg = s.substring(s.indexOf('\\') + 12);
					char[] keyAsChars = s.substring(s.indexOf('\\') + 4, s.indexOf('\\') + 12).toCharArray();

					String keyBinStr = "";

					for (char c : keyAsChars) {
						String t = Integer.toBinaryString((int) c);
						while (t.length() < 16)
							t = "0" + t;
						keyBinStr += t;
					}

					String msg = Decryption.decrypt(encrMsg, keyBinStr);

					System.out.println("Message by '" + origin + "': \n" + msg);
					
					addToChat(origin, msg);

				} else if (s.startsWith("mp")) {
					if (s.contains("\\") && !s.substring(2).startsWith("\\")) {
						String name = s.substring(2, s.indexOf('\\'));
						String msg = s.substring(s.indexOf('\\') + 1);
						System.out.println("message from <" + name + ">\n\t" + msg);
						addToChat(name, msg);
					} else {
						s = s.substring(2);
						if (s.startsWith("\\")) {
							s = s.substring(1);
						}
						System.out.println("Message from <Anonymus>:\n\t" + s);
					}
				}
			} catch (IOException e) {
				if (loop)
					e.printStackTrace();
			}

		}
	}

	public void disconnect() {
		System.out.println("disconnecting...");
		try {
			out.writeUTF("q");
			loop = false;
			connected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reboot() {
		disconnect();
		try {
			Thread.sleep(1000);// waiting for server to complete deRegistration
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		connect(host, serverPort, name);
	}

	public void beginChat(String name, String encryption) {
		chats.add(new ArrayList<String>());
		chats.get(chats.size() - 1).add(name);
		chats.get(chats.size() - 1).add(encryption);
	}

	public void beginChat(String name) {
		chats.add(new ArrayList<String>());
		chats.get(chats.size() - 1).add(name);
		chats.get(chats.size() - 1).add("");
	}

	public void encryptChat(String name, String encryption) {
		for (ArrayList<String> chat : chats) {
			if (chat.get(0).equals(name)) {
				chat.set(1, encryption);
			}
		}
	}

	public boolean sendThroughChat(String name, String msg) {
		ArrayList<String> chat = null;
		for (ArrayList<String> c : chats) {
			if (c.get(0).equals(name)) {
				chat = c;
			}
		}

		if (chat == null) {
			System.err.println("Error: could not find current chat with '" + name + "'");
			return false;
		}

		String s = "mp";

		if (chat.get(1).equals("")) {
			s = "me";
		}

		s += name + "\\" + chat.get(1) + msg;

		chat.add("\\" + msg);
		
		send(s);

		return true;
	}

	public void addToChat(String name, String msg) {
		ArrayList<String> chat = null;
		for (ArrayList<String> c : chats) {
			if (c.get(0).equals(name)) {
				chat = c;
			}
		}
		
		if(chat == null) {
			beginChat(name);
			chat = chats.get(chats.size()-1);
		}
		
		chat.add(name + "\\" + msg);
	}
	
	public String[][] getChats() {
		String[][] ret = new String[chats.size()][];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = new String[chats.get(i).size()];
			for (int j = 0; j < ret[i].length; j++) {
				ret[i][j] = chats.get(i).get(j);
			}
		}

		return ret;
	}

	public static class ClientThread extends Thread {

		private Client client;

		public ClientThread(Client client) {
			this.client = client;
		}

		@Override
		public void run() {
			while (client.loop)
				client.waitForMessage();
		}
	}

}
