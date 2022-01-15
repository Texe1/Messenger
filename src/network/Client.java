package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import aes.Decryption;
import aes.Encryption;
import aes.KeySchedule;
import diffie_hellman.KeyExchange;
import gui.general.ClientFrame;
import main.Loggable;

public class Client extends Loggable {

	Socket client;
	DataOutputStream out;
	DataInputStream in;
	
	ClientFrame f;

	Thread receivingThread = new Thread() {
		@Override
		public void run() {
			while (!client.isClosed()) {
				waitForMessage();
			}
		}
	};

	Thread sendingAndProcessingThread = new Thread() {
		public void run() {
			while (!client.isClosed()) {
				if(contacts == null || contacts.length == 0) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(localSlowMode) {
					try {
						Thread.sleep(slowModePause);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				while (!outQueue.isEmpty()) {
					send(outQueue.remove(0));
					if(f != null) f.update(true);
				}
				
				while (!inQueue.isEmpty()) {
					processMsg(inQueue.remove(0));
					if(f != null) f.update(true);
				}
			}
		};
	};
	
	private boolean localSlowMode = false;
	private int slowModePause = 200;// the pause between sending the messages in local slowmode (in milliseconds)
	
	public void setFrame(ClientFrame f){
		this.f = f;
		f.update(this, true);
	}

	private String[] contacts;

	String host;
	String name;
	int serverPort;

	boolean receivedContacts = false;

	public boolean connected;

	private ArrayList<ArrayList<String>> chats = new ArrayList<>();

	public static final String[] encryptions = new String[] { "aes" };

	public CopyOnWriteArrayList<String> outQueue = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<String> inQueue = new CopyOnWriteArrayList<>();

	public static OutputStream logger;

	private boolean loop = true;
	private int sendAttempts = 0;

	public boolean isWaiting() {
		return loop;
	}
	
	public boolean receivedContacts() {
		if(receivedContacts) {
			receivedContacts = false;
			return true;
		}
		return false;
	}

	public void connect(String host, int port, String name) {
		this.host = host;
		this.serverPort = port;

		try {
			client = new Socket(host, port);
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
			out.writeUTF(name);

		} catch (UnknownHostException e) {
			System.err.println("Could not find Server at " + host + ":" + port);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		connected = true;

		if (!receivingThread.isAlive())
			receivingThread.start();
		if (!sendingAndProcessingThread.isAlive())
			sendingAndProcessingThread.start();
	}

	public void send(String s) {
		log(s);
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
				msg = "aes" + msg.substring(1);
			}
			if (msg.startsWith("aes")) {// specific request to encrypt with aes
				msg = msg.substring(3);

				
//				TODO fix Encryption
				// encrypting the main message
				String encrypted = Encryption.encrypt(msg, getChat(name)[2].toCharArray());
//				String key = KeySchedule.Key;

				// converting key to char[8]
//				char[] keyAschars = new char[8];

//				String[] splitKey = key.split("(?<=\\G.{16})");
//
//				for (int i = 0; i < 8; i++) {
//					keyAschars[i] = (char) Integer.parseInt(splitKey[i], 2);
//				}

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

	public void waitForMessage() {// waiting for Messages from Server or User and adding them to inQueue
		try {
			in = new DataInputStream(client.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String msg;
		try {
			msg = in.readUTF();
			inQueue.add(msg);
		} catch (IOException e) {
			if (loop)
				e.printStackTrace();
		}
	}

	private synchronized void processMsg(String s) {
		if (s.startsWith(">c")) {// receiving updated list of contacts

			if (s.length() > 3) {
				contacts = s.substring(3).split(" ");

				for (int i = 0; i < chats.size(); i++) {
					boolean isStillOnline = false;
					for (int j = 0; j < contacts.length; j++) {

						if (chats.get(i).get(i).equals(contacts[j])) {
							isStillOnline = true;
						}
					}
					if (!isStillOnline) {
						chats.remove(i--);
					}
				}
			} else {
				contacts = null;
				chats = new ArrayList<>();
				receivedContacts = true;
			}
			receivedContacts = true;
		} else if (s.startsWith("n")) {// receiving compatible name
			name = s.substring(1);
			log("name changed to: \"" + name + "\"");
		} else if (s.startsWith("me")) { // receiving encrypted message
			String origin = s.substring(2, s.indexOf('\\'));
			String encrMsg = s.substring(s.indexOf('\\') + 12);
			char[] key = s.substring(s.indexOf('\\') + 4, s.indexOf('\\') + 12).toCharArray();

//			String keyBinStr = "";
//
//			for (char c : keyAsChars) {
//				String t = Integer.toBinaryString((int) c);
//				while (t.length() < 16)
//					t = "0" + t;
//				keyBinStr += t;
//			}

			String msg = Decryption.decrypt(encrMsg, key);

			log("received message from '" + origin + ":\n" + msg + "\n_____");
			addToChat(origin, msg);

		} else if (s.startsWith("mp")) { // receiving plain text message
			if (s.contains("\\") && !s.substring(2).startsWith("\\")) {
				String name = s.substring(2, s.indexOf('\\'));
				String msg = s.substring(s.indexOf('\\') + 1);
				log("received message from '" + name + ":\n" + msg + "\n_____");
				addToChat(name, msg);
			} else {
				s = s.substring(2);
				if (s.startsWith("\\")) {
					s = s.substring(1);
				}
				log("Message from <Anonymus>:\n\t" + s);
			}
		} else if(s.charAt(0) == 'k') {// KeyExchange
			
			String name = s.substring(2, s.indexOf('\\'));
			
			BigInteger A = null;
			if(s.charAt(1) == '1') {// has to send back
				A = KeyExchange.randomPrime();
				BigInteger J = KeyExchange.step1(A);
			
				outQueue.add("k2" + J.toString(16));
			}else {// doesn't have to send back
				A = new BigInteger(getChat(name)[2], 16);
			}
			BigInteger I = new BigInteger(s.substring(s.indexOf('\\') + 1), 16);
			
			// generating key
			char[] key = KeySchedule.KeyGeneration(
					KeyExchange.step2(I, A)
				);
			
			ArrayList<String> chat = null;
			
			for (ArrayList<String> c : chats) {
				if(c.get(0).equals(name)) {
					chat = c;
					break;
				}
			}
			
			if(chat == null) {
				System.err.println("Fatal error during KeyExchange: Name not found");
			}
			
			chat.set(2, key.toString());
		}
	}

	public void disconnect() {
		log("disconnecting...");
		try {
			out.writeUTF("q");
			contacts = null;
			chats = new ArrayList<>();
			receivedContacts = true;
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

	
	public synchronized ArrayList<String> beginChat(String name, String encryption) {
		chats.add(new ArrayList<String>());
		ArrayList<String> chat = chats.get(chats.size() - 1);
		
		chat.add(name);
		chat.add(encryption);
		
		log("began chat with '" + name + "'on Encryption '" + encryption + "'");
		
		//key Exchange start
		char[] key = KeySchedule.KeyGeneration(BigInteger.ZERO);
		
		BigInteger A = KeyExchange.randomPrime();
		
		BigInteger I = KeyExchange.step1(A);
		chat.add(A.toString(16));
		
		
		outQueue.add("k1" + I.toString(16));
		
		return chats.get(chats.size() - 1);
	}

	public synchronized ArrayList<String> beginChat(String name) {
		return beginChat(name, "");
	}

	public void encryptChat(String name, String encryption) {
		for (ArrayList<String> chat : chats) {
			if (chat.get(0).equals(name)) {
				chat.set(1, encryption);
			}
		}
		
		// TODO: Key Exchange
	}

	public boolean sendThroughChat(String name, String msg) {
		ArrayList<String> chat = null;
		for (ArrayList<String> c : chats) {
			if (c.get(0).equals(name)) {
				chat = c;
			}
		}

		if (chat == null) {
			log("Could not find current chat with '" + name + "', beginning new chat");
			chat = beginChat(name, "aes");
		}

		String msgType = "me";

		if (chat.get(1).equals("")) {
			msgType = "mp";
		}

		chat.add("\\" + msg);

		outQueue.add(msgType + name + "\\" + chat.get(1) + msg);
		
		return true;
	}

	public void addToChat(String name, String msg) {
		ArrayList<String> chat = null;
		for (ArrayList<String> c : chats) {
			if (c.get(0).equals(name)) {
				chat = c;
			}
		}

		if (chat == null) {
			chat = beginChat(name, "aes");
		}

		chat.add(msg);
		
	}
	
	public String[] getChat(String name) {
		for (ArrayList<String> chat : chats) {
			if(chat.get(0).equals(name)) {
				
				String[] ret = new String[chat.size()];
				
				for (int i = 0; i < ret.length; i++) {
					ret[i] = chat.get(i);
				}
				
				return ret;
			}
		}
		
		return null;
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

	public String[] getContacts() {
		if (contacts == null)
			return new String[0];
		return contacts;
	}

}
