package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {

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
			System.err.println(
					"I/O Error in Server Thread for client " + clientName + ", INet=" + client.getInetAddress() + ":");
			e.printStackTrace();
			return;
		}

		while (true) {
			try {
				String s = in.readUTF();
				if (s.strip().equals("<c")) {// contact request
					Server.log("contacts requested by " + clientName + " at " + client.getInetAddress() + ":" + client.getLocalPort());
					String namesList = ">c";
					for (String name : Server.clientNames) {
						if (!name.equals(clientName))
							namesList += " " + name;
					}
					out.writeUTF(namesList);
				} else if (s.strip().equals("q")) {// quit message
					Server.clients.remove(client);
					Server.clientNames.remove(clientName);
					Server.log(clientName + " at [" + client.getInetAddress() + ":" + client.getLocalPort() + "] has disconnected.");
					client.close();//
					break;

				} else if (s.startsWith("m")) { // sending a message to other client
					String name = s.substring(2, s.indexOf('\\'));
					if (Server.clientNames.contains(name)) {// the requested receiver is connected to the server
						Socket receiver = Server.clients.get(Server.clientNames.indexOf(name));
						String send = s.replaceFirst(name, clientName);
						DataOutputStream rOut = new DataOutputStream(receiver.getOutputStream());
						rOut.writeUTF(send);
					} else {// the requested receiver is not connected to the server
						System.err.println("Server Error:\n\r\tCould not find client \"" + name + "\" for receiving message from "
								+ clientName + " at [" + client.getInetAddress() + ":" + client.getLocalPort() + "]");
					}
				} else {
					Server.log("<" + clientName + "> " + s + "| could not understand intentions");
				}
			} catch (IOException e) {
				Server.log("Reading from client " + clientName + " failed:");
				e.printStackTrace();
				break;
			}
		}

	}
}