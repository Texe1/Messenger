package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	Socket client;
	DataOutputStream out;
	DataInputStream in;
	
	String[] contacts;
	
	String name;

	public void registerToServer(String host, int port, String name) {
		try {
			client = new Socket(host, port);
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
			out.writeUTF(name);
			out.writeUTF("<c");
			
		} catch (UnknownHostException e) {
			System.err.println("Could not access host at " + host + ":" + port);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String s) {
		DataOutputStream out;
		try {
			out = new DataOutputStream(client.getOutputStream());
			out.writeUTF(s);
//			deRegister();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void waitForMessage() {
		DataInputStream in;
		try {
			in = new DataInputStream(client.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		while (true) {
			try {
				String s = in.readUTF();
				System.out.println(s);
				if(s.startsWith(">c")) {
					s = s.substring(3);
					contacts = s.split(" ");
					System.out.println(s.replace(' ', '\n').replace(name, "").replace("\n\n", "\n"));
				}else if(s.startsWith("n")) {
					name = s.substring(1);
					System.out.println("name changed to: \"" + name + "\"");
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public void deRegister() {
		try {
			out.writeUTF("q");
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
