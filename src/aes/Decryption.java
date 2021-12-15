package aes;


public class Decryption {

	public static String decrypt(String s, String key) {

		KeySchedule.keySchedule(key);

		char[] WorkChar = new char[16];
		char c;
		String OutputString = "";
//		String[] EingabeText = new String[s.length()];
//		EingabeText = s.split(";");
//		for (int i = 0; i < EingabeText.length; i++) {
//			s+=(char)(int)(Integer.valueOf(EingabeText[i]));
//		}
//		System.out.println("encrypted message:\n" + s);
		for (int i = 0; i < s.length(); i += 16) {
			WorkChar = s.substring(i, (i + 16)).toCharArray();

			for (int k = 10; k > 0; k--) {
				// Add RoundKey
				for (int j = 0; j < 3; j++) {
					for (int j2 = 0; j2 < 3; j2++) {
						WorkChar[(j * 4) + j2] ^= KeySchedule.KeyWords[(4 * k) + j][j2];
					}
				}
				
				for (int j = 0; j < 4; j++) {
					char[] e = new char[4];
					e = KeySchedule.inverseMixColumns(WorkChar[j*4],WorkChar[(j*4)+1],WorkChar[(j*4)+2],WorkChar[(j*4)+3]);
					WorkChar[j*4]     = e[0];
					WorkChar[(j*4)+1] = e[1];
					WorkChar[(j*4)+2] = e[2];
					WorkChar[(j*4)+3] = e[3];
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
					int z = 0;
					while (WorkChar[j] != KeySchedule.SubstitutionMap[z]) {
						z++;
					}
					WorkChar[j] = (char)z;		
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
