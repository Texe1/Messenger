package aes;

import java.util.Random;



public class KeySchedule {

	public static String Key;
	public static int[] SubstitutionMap1 = new int[] { 9, 4, 14, 0, 11, 6, 2, 1, 13, 7, 3, 15, 5, 10, 8, 12 };
	public static int[] SubstitutionMap2 = new int[] { 3, 7, 6, 10, 1, 12, 5, 9, 14, 0, 13, 4, 15, 8, 2, 11 };
	public static char[] cSubstitutionMap1 = new char[] { 9, 4, 14, 0, 11, 6, 2, 1, 13, 7, 3, 15, 5, 10, 8, 12 };
	public static char[] cSubstitutionMap2 = new char[] { 3, 7, 6, 10, 1, 12, 5, 9, 14, 0, 13, 4, 15, 8, 2, 11 };
	public static char[][] KeyWords = new char[44][4];

	public static String KeyGeneration() {
		Random r = new Random();
		char[] key = new char[8];
		Key = "";

		// TODO char[] key (unimplemented)
		for (int i = 0; i < 8; i++) {
			key[i] = (char) r.nextInt(65536);
//			System.out.println(Integer.toBinaryString((int) key[i]));
		}

		for (int i = 0; i < 128; i++) {
			Key += ((int) (Math.random() + 0.5)); // Key-Generation
		}

		return Key;
	}

	public static void keySchedule(String key) {

		String KeyText = "";
		String[] split = new String[(key.length() / 8) + 1];
		for (int i = 0; i < (key.length() / 8); i++) {
			split = key.split("(?<=\\G.{8})");
		}

		int Z = 0;
		for (int i = 0; i < split.length; i++) {
			Z = Integer.parseInt(split[i], 2);
			KeyText += ((char) Z);
		}
//		System.out.println("128bit-Key:");
//		System.out.println(key);
//		 System.out.println(KeyText);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				KeyWords[i][j] = KeyText.charAt((i * 4) + j);
			}
		}
		char[] RoundConstant = new char[] {'\u0001','\u0002','\u0004','\u0008','\u0010','\u0020','\u0040','\u0080','\u001B','\u0036'};
		String Text = "";
		String[] ZwischenText = new String[2];
		char c;
		for (int i = 4; i < 44; i++) { // Key stretch

			for (int j = 0; j < 4; j++) {
				KeyWords[i][j] = KeyWords[i - 1][j];
			}

			if (i % 4 == 0) {
				c = KeyWords[i][0];
				KeyWords[i][0] = KeyWords[i][1];
				KeyWords[i][1] = KeyWords[i][2];
				KeyWords[i][2] = KeyWords[i][3];
				KeyWords[i][3] = c;

				for (int j = 0; j < 4; j++) {
					String BinaryBit = "" + Integer.toBinaryString(KeyWords[i][j]);
					while (BinaryBit.length() < 8) {
						BinaryBit = "0" + BinaryBit;
					}
					ZwischenText = BinaryBit.split("(?<=\\G.{4})");

					for (int k = 0; k < 2; k++) {
						Text = Integer.toBinaryString(SubstitutionMap1[Integer.parseInt(ZwischenText[k], 2)]);
						while (Text.length() < 4) {
							Text = "0" + Text;
						}
						ZwischenText[k] = Text;
					}
					KeyWords[i][j] = (char) Integer.parseInt((ZwischenText[0] + ZwischenText[1]), 2);

					KeyWords[i][j] ^= KeyWords[i - 4][j];
				}
				KeyWords[i][0] ^= RoundConstant[(i/4)-1];
			} else {

				for (int j = 0; j < 4; j++) {
					KeyWords[i][j] ^= KeyWords[i - 4][j];
				}

			}

		}
	}
	public static char multiplication(char a, char b) {	
		char c = '\u0000';			
		//Multiplication		
		for (int i = 0; i < 8; i++) {
			if((a & (1 << i)) != 0) {
				c ^= (b<<i);
			}
		}
		char p =  '\u011B';
		//bit shift modulo 
		for (int i = 14; i > 7; i--) {
			if ((c & (1 << i)) != 0) {
				c ^= (p << (i-8));
			}				
		}
		return c;
	}
	
	public static char[] mixColumns(char a,char b,char c,char d) {	
		char[] e = new char[4];
		e[0] ^=(KeySchedule.multiplication(a,'\u0002'))^(KeySchedule.multiplication(b,'\u0003'))^(c)^(d);
		e[1] ^=(a)^(KeySchedule.multiplication(b,'\u0002'))^(KeySchedule.multiplication(c,'\u0003'))^(d);
		e[2] ^=(a)^(b)^(KeySchedule.multiplication(c,'\u0002'))^(KeySchedule.multiplication(d,'\u0003'));
		e[3] ^=(KeySchedule.multiplication(a,'\u0003'))^(b)^(c)^(KeySchedule.multiplication(d,'\u0002'));		

		return e;
	}
	
	
	
	public static char[] inverseMixColumns(char a,char b,char c,char d) {
		char[] e = new char[4];
		e[0] ^=(KeySchedule.multiplication(a,'\u000e'))^(KeySchedule.multiplication(b,'\u000b'))^(KeySchedule.multiplication(c,(char)13))^(KeySchedule.multiplication(d,'\u0009'));
		e[1] ^=(KeySchedule.multiplication(a,'\u0009'))^(KeySchedule.multiplication(b,'\u000e'))^(KeySchedule.multiplication(c,'\u000b'))^(KeySchedule.multiplication(d,(char)13));
		e[2] ^=(KeySchedule.multiplication(a,(char)13))^(KeySchedule.multiplication(b,'\u0009'))^(KeySchedule.multiplication(c,'\u000e'))^(KeySchedule.multiplication(d,'\u000b'));
		e[3] ^=(KeySchedule.multiplication(a,'\u000b'))^(KeySchedule.multiplication(b,(char)13))^(KeySchedule.multiplication(c,'\u0009'))^(KeySchedule.multiplication(d,'\u000e'));		

		return e;
	}

}
