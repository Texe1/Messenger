package network.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import diffie_hellman.KeyExchange;
import gui.general.ClientFrame;

public class Client extends Socket {

	String name;

	ClientFrame f;
	
	DataInputStream in;
	DataOutputStream out;
	
	private ArrayList<Chat> chats = new ArrayList<>();

	private ArrayList<String> inQueue = new ArrayList<>();
	private ArrayList<String> outQueue = new ArrayList<>();
	
	public static final char MSG_NAME = 'n',
			MSG_MSG = 'm',
			MSG_KEY_INIT = 'k',
			MSG_KEY_RESPONSE = 'K',
			MSG_CONTACS = 'c';

	private Thread sendThread = new Thread("send&process") {
		public void run() {
			while(!isClosed() && isConnected()) {
				
				// processing messages
				int i = 0;
				while(i < inQueue.size()) {
					if(process(i)) {
						inQueue.remove(i);
					}else {
						i++;
					}
				}
				while(!outQueue.isEmpty()) {
					try {
						out.writeUTF(outQueue.remove(0));
					} catch (IOException e) {
						// TODO exception handling
						e.printStackTrace();
					}
				}
			}
		};
	};
	private Thread receiveThread = new Thread("receive") {
		public void run() {
			while (!isClosed() && isConnected()) {
				try {
					// receiving message
					String s = in.readUTF();
					if (s != null)
						inQueue.add(s);
				} catch (IOException e) {
					
				}
			}
		};
	};
	

	public Client(String host, int port, String name) throws UnknownHostException, IOException, InterruptedException {
		
		// connecting
		super(host, port);

		in = new DataInputStream(getInputStream());
		out = new DataOutputStream(getOutputStream());

		// requesting name (name might be changed due to duplicate)
		out.writeUTF(name);
		
		sendThread.start();
		receiveThread.start();
		
	}

	private void send(String s) {
		// sending the message like stored in outQueue
		if(this.isClosed() || !this.isConnected())
		try {
			out.writeUTF(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean process(int index) {
		String s = inQueue.get(index);
		
		char msgType = s.charAt(0);
		String msg = s.substring(1);
		
		switch (msgType) {
		
		// changing name
		case MSG_NAME ->{
			String name = s.substring(1);
			// TODO add forbidden chars, disconnect if name contains these
			this.name = name;
			return true;
		}
		
		// encrypted message
		case MSG_MSG ->{
			char encryptionType = msg.charAt(0);
			msg = msg.substring(index);
			String sender = msg.substring(0, '\\');
			// TODO decryption, adding to chat
			return true;
		}
		
		// key Exchange receiver
		case MSG_KEY_INIT -> {
			String name = msg.substring(0, msg.indexOf('\\'));
			Chat c = new Chat(name);
			
			BigInteger I = new BigInteger(msg.substring(msg.indexOf('\\') + 1), 16);
			
			System.out.println("Generated Prime : " + c.key());
			
			BigInteger J = KeyExchange.step1(new BigInteger(c.key(), 16));
			System.out.println("step 1: " + J.toString(16));
			System.out.println("received Prime : " + I.toString(16));
			c.unlock(I);
			chats.add(c);
			System.out.println("Key:" + c.key());
			
			String keyMsg = MSG_KEY_RESPONSE + name + '\\' + J.toString(16);
			outQueue.add(keyMsg);
			return true;
		}
		
		// key Exchange initiator
		case MSG_KEY_RESPONSE -> {
			String name = msg.substring(0, msg.indexOf('\\'));
			Chat c = null;
			for (Chat chat : chats) {
				if(chat.name().equals(name)) {
					c = chat;
					break;
				}
			}
			
			if(c == null) {
				throw new IllegalArgumentException("Did not find Chat with '" + name + "' to complete Key Exchange");
			}
			System.out.println("Generated Prime : " + c.key());
			
			BigInteger I = new BigInteger(msg.substring(msg.indexOf('\\')+1), 16);
			
			System.out.println("step 1: " + KeyExchange.step1(new BigInteger(c.key(), 16)));
			System.out.println("received Prime : " + I.toString(16));
			
			c.unlock(I);
			System.out.println("Key:" + c.key());
			return true;
		}
		
		// contacts
		case MSG_CONTACS -> {
			String[] names = msg.split("%");
			
			for (String n : names) {
				if(n.isBlank()) continue;
				boolean exists = false;
				for (Chat c : chats) {
					if(c.name().equals(n)) {
						exists = true;
						break;
					}
				}
				if(!exists) {
					Chat C = new Chat(n);
					chats.add(C);
					BigInteger I = KeyExchange.step1(new BigInteger(C.key(), 16));
					String keyMsg = MSG_KEY_INIT + n + '\\' + I.toString(16);
					outQueue.add(keyMsg);
				}
			}
			return true;
		}
		
		default -> throw new IllegalArgumentException("Unexpected message Type: " + msgType);
		}
	}
}
