package aes;

public class Decryption {

	public static String decrypt(String s, String key) {

		KeySchedule.keySchedule(key);

		char[] WorkChar = new char[16];
		String Text = "";
		String[] ZwischenText = new String[2];
		char c;
		String OutputString = "";
//		String[] EingabeText = new String[s.length()];
//		EingabeText = s.split(";");
//		for (int i = 0; i < EingabeText.length; i++) {
//			s+=(char)(int)(Integer.valueOf(EingabeText[i]));
//		}
		System.out.println("encrypted message:\n" + s);
		for (int i = 0; i < s.length(); i += 16) {
			WorkChar = s.substring(i, (i + 16)).toCharArray();

			for (int k = 10; k > 0; k--) {
				// Add RoundKey
				for (int j = 0; j < 3; j++) {
					for (int j2 = 0; j2 < 3; j2++) {
						WorkChar[(j * 4) + j2] ^= KeySchedule.KeyWords[(4 * k) + j][j2];
					}
				}
				// Reverse Permutation
				c = WorkChar[3];
				WorkChar[3] = WorkChar[7];
				WorkChar[7] = WorkChar[11];
				WorkChar[11] = WorkChar[15];
				WorkChar[15] = c;

				c = WorkChar[10];
				WorkChar[10] = WorkChar[2];
				WorkChar[2] = c;
				c = WorkChar[14];
				WorkChar[14] = WorkChar[6];
				WorkChar[6] = c;

				c = WorkChar[13];
				WorkChar[13] = WorkChar[9];
				WorkChar[9] = WorkChar[5];
				WorkChar[5] = WorkChar[1];
				WorkChar[1] = c;
				// Reverse Substitution

				for (int j = 0; j < 16; j++) {

					String BinaryBit = "" + Integer.toBinaryString(WorkChar[j]);
					while (BinaryBit.length() < 8) {
						BinaryBit = "0" + BinaryBit;
					}
					ZwischenText = BinaryBit.split("(?<=\\G.{4})");

					for (int g = 0; g < 2; g++) {
						Text = Integer
								.toBinaryString(KeySchedule.SubstitutionMap2[Integer.parseInt(ZwischenText[g], 2)]);
						while (Text.length() < 4) {
							Text = "0" + Text;
						}
						ZwischenText[g] = Text;
					}
					WorkChar[j] = (char) Integer.parseInt((ZwischenText[0] + ZwischenText[1]), 2);

				}

			}

			for (int j = 0; j < 3; j++) {
				for (int j2 = 0; j2 < 3; j2++) {
					WorkChar[(j * 4) + j2] ^= KeySchedule.KeyWords[j][j2];
				}
			}

			for (int j = 0; j < 16; j++) {
				OutputString += WorkChar[j];

			}

		}

		return (OutputString);
	}

}
