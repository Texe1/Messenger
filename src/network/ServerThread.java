package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

public class ServerThread extends Thread{
	
	String clientName;
	Socket client;
	
	public ServerThread(String clientName, Socket client) {
		this.clientName = clientName;
		this.client = client;
	}
	
	public void run() {
		DataInputStream in;
		DataOutputStream out;
		
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
		} catch (IOException e) {
			System.err.println("I/O Error in Server Thread for client " + clientName + ", INet=" + client.getInetAddress() + ":");
			e.printStackTrace();
			return;
		}
		
		while (true) {
			try {
				String s = in.readUTF();
				if(s.strip().equals("<c")) {// contact request
					System.out.println("contacts requested by " + clientName + " at " + client.getInetAddress() + ":" + client.getLocalPort());
					Set<String> keySet = Server.clients.keySet();
					String namesList = ">c";
					for (String k : keySet) {
						namesList += " " + k;
					}
					out.writeUTF(namesList);
					System.out.println(namesList);
				}else if(s.strip().equals("q")) {//quit message
					Server.clients.remove(clientName);
					client.close();
					break;
				}else if(s.startsWith("m")){	// sending a message to other client
					String name = s.substring(2).split(" ")[0];
					if(Server.clients.containsKey(name)) {
						Socket receiver = Server.clients.get(name);
						DataOutputStream rOut = new DataOutputStream(receiver.getOutputStream());
						rOut.writeUTF(s);
					}else {
						System.err.println("Server Error:\n\r\tCould not find client for receiving message from " + clientName + " at " + client.getInetAddress() + ":" + client.getLocalPort());
					}
				}else{
					System.out.println("<" + clientName + "> " + s + "| could not understand intentions");
				}
			} catch (IOException e) {
				System.out.println("Reading from client " + clientName + " failed:");
				e.printStackTrace();
				break;
			}
		}
		
	}
}