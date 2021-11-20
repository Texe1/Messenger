package network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	public static void send(String s, String host, int port) {
		try {
			Socket client = new Socket(host, port);
			System.out.println("1");
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			System.out.println("2");
			out.writeUTF(s);
			System.out.println("3");
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
