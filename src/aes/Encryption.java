package aes;

public class Encryption {

	public static String encrypt(String s) {

		KeySchedule.keySchedule(KeySchedule.KeyGeneration());

		while (s.length() % 16 != 0) {
			s += " ";
		}

		char[] WorkChar = new char[16];
		
		char c;
		String OutputString = "";

		for (int i = 0; i < s.length(); i += 16) {
			WorkChar = s.substring(i, (i + 16)).toCharArray();

			for (int j = 0; j < 3; j++) {
				for (int j2 = 0; j2 < 3; j2++) {
					WorkChar[(j * 4) + j2] ^= KeySchedule.KeyWords[j][j2];
				}
			}

			for (int k = 1; k < 11; k++) {

				for (int j = 0; j < 16; j++) {

					// Substitution
					WorkChar[j]=KeySchedule.SubstitutionMap1[Integer.valueOf(WorkChar[j])];	
				}

				// Permutation
				c = WorkChar[1];
				WorkChar[1] = WorkChar[5];
				WorkChar[5] = WorkChar[9];
				WorkChar[9] = WorkChar[13];
				WorkChar[13] = c;

				c = WorkChar[2];
				WorkChar[2] = WorkChar[10];
				WorkChar[10] = c;
				c = WorkChar[6];
				WorkChar[6] = WorkChar[14];
				WorkChar[14] = c;

				c = WorkChar[15];
				WorkChar[15] = WorkChar[11];
				WorkChar[11] = WorkChar[7];
				WorkChar[7] = WorkChar[3];
				WorkChar[3] = c;
				
				//matrix
				for (int j = 0; j < 4; j++) {
					char[] e = new char[4];
					e = KeySchedule.mixColumns(WorkChar[j*4],WorkChar[(j*4)+1],WorkChar[(j*4)+2],WorkChar[(j*4)+3]);
					WorkChar[j*4]     = e[0];
					WorkChar[(j*4)+1] = e[1];
					WorkChar[(j*4)+2] = e[2];
					WorkChar[(j*4)+3] = e[3];
				}
				
				// Add RoundKey
				for (int j = 0; j < 3; j++) {
					for (int j2 = 0; j2 < 3; j2++) {
						WorkChar[(j * 4) + j2] ^= KeySchedule.KeyWords[(4 * k) + j][j2];
					}
				}

			}

			for (int j = 0; j < 16; j++) {
				OutputString += WorkChar[j];
			}

		}

//		System.out.println(OutputString);
//		System.out.println();
		return (OutputString);

	}
}