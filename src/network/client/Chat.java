package network.client;

import java.math.BigInteger;
import java.util.ArrayList;

import aes.Decryption;
import aes.KeySchedule;
import diffie_hellman.KeyExchange;

public class Chat {
	private boolean locked = true;
	private String key;
	private String name;
	private Encryption encryption;

	// TODO set to private
	public ArrayList<String> messages = new ArrayList<>();

	public boolean locked() {
		return locked;
	}

	public String name() {
		return name;
	}

	public String key() {
		return key;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Encryption encryption() {
		return encryption;
	}

	public void setEncryption(Encryption encryption) {
		this.encryption = encryption;
	}

	public String[] messages() {
		return messages.toArray(new String[0]);
	}

	public boolean addMessage(String s) {
		if(locked) return false;
		if(encryption == Encryption.AES) {
			String decrypted = Decryption.decrypt(s, key.toCharArray());
			messages.add(decrypted);
			return true;
		}
		
		messages.add(s);
		return true;
	}
	
	public boolean addRawMsg(String s) {
		if(locked) return false;
		
		messages.add(s);
		return true;
	}
	
	public String encrypt(String s) {
		addRawMsg(s);
		return aes.Encryption.encrypt(s, key.toCharArray());
	}

	public Chat(String name) {
		this.name = name;
		this.encryption = Encryption.AES;
		this.key = KeyExchange.randomPrime().toString(16);
	}

	public void unlock(BigInteger B) {
		if (!locked)
			return;
		BigInteger A = new BigInteger(key, 16);

		key = new String(KeySchedule.KeyGeneration(KeyExchange.step2(B, A)));
		locked = false;
	}

	public static enum Encryption {
		NONE("", 0), AES("aes", 1);

		public final String s;
		public final int i;

		private Encryption(String s, int i) {
			this.s = s;
			this.i = i;
		}
	}

}
