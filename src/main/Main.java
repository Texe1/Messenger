package main;

import java.util.Scanner;

import aes.Decryption;
import aes.Encryption;
import aes.KeySchedule;
import network.Client;
import network.Server;

public class Main {

	public static void main(String[] args) {
		System.out.println("s => Server\nc => Client");

		Scanner sc = new Scanner(System.in);
		String in = sc.nextLine();
		if (in.equals("s")) {
			Server.start(1337);
			Server.laufen();
		} else if (in.equals("c")) {
			String s = "Hallo, Welt";

			// encryption
			String encrypted = Encryption.encrypt(s);
			System.out.println(encrypted);

			// binaryString to char[] conversion
			char[] keyAschars = new char[8];

			String[] splitKey = KeySchedule.Key.split("(?<=\\G.{16})");

			for (int i = 0; i < 8; i++) {
				keyAschars[i] = (char) Integer.parseInt(splitKey[i], 2);
				for (int j = 0; j < 16; j++) {
					System.out.print(((keyAschars[i] & (1 << (15 - j))) != 0) ? "1" : "0");
				}
			}
			
			System.out.println(Decryption.decrypt(encrypted, KeySchedule.Key));
			
			String send = "";
			
			for(char c : keyAschars) {
				send += "" + c;
			}
			
			System.out.println(send.length());
			
			send += encrypted;
			
			System.out.println(send.length());
			
			
			System.out.println();

			System.out.println();
			for(char c : send.toCharArray()) {
				System.out.println((int)c);
			}
			System.out.println();
			System.out.println();
			
			Client.send(send, "localhost", 1337);
		}else {
			System.out.println("that was not one of the options!");
		}
	}

}
