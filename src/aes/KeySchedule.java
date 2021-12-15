package aes;

import java.util.Random;



public class KeySchedule {

	public static String Key;
	public static char[] SubstitutionMap1 = new char[] {'\u0063','\u007c','\u0077','\u007b','\u00f2','\u006b','\u006f','\u00c5','\u0030','\u0001','\u0067','\u002b','\u00fe','\u00d7','\u00ab','\u0076',
			'\u00ca','\u0082','\u00c9','\u007d','\u00fa','\u0059','\u0047','\u00f0','\u00ad','\u00d4','\u00a2','\u00af','\u009c','\u00a4','\u0072','\u00c0',
			'\u00b7','\u00fd','\u0093','\u0026','\u0036','\u003f','\u00f7','\u00cc','\u0034','\u00a5','\u00e5','\u00f1','\u0071','\u00d8','\u0031','\u0015',
			'\u0004','\u00c7','\u0023','\u00c3','\u0018','\u0096','\u0005','\u009a','\u0007','\u0012','\u0080','\u00e2','\u00eb',(char)39,'\u00b2','\u0075',
			'\u0009','\u0083','\u002c','\u001a','\u001b','\u006e','\u005a','\u00a0','\u0052','\u003b','\u00d6','\u00b3','\u0029','\u00e3','\u002f','\u0084',
			'\u0053','\u00d1','\u0000','\u00ed','\u0020','\u00fc','\u00b1','\u005b','\u006a','\u00cb','\u00be','\u0039','\u004a','\u004c','\u0058','\u00cf',
			'\u00d0','\u00ef','\u00aa','\u00fb','\u0043','\u004d','\u0033','\u0085','\u0045','\u00f9','\u0002','\u007f','\u0050','\u003c','\u009f','\u00a8',
			'\u0051','\u00a3','\u0040','\u008f','\u0092','\u009d','\u0038','\u00f5','\u00bc','\u00b6','\u00da','\u0021','\u0010','\u00ff','\u00f3','\u00d2',
			'\u00cd','\u000c','\u0013','\u00ec','\u005f','\u0097','\u0044','\u0017','\u00c4','\u00a7','\u007e','\u003d','\u0064','\u005d','\u0019','\u0073',
			'\u0060','\u0081','\u004f','\u00dc','\u0022','\u002a','\u0090','\u0088','\u0046','\u00ee','\u00b8','\u0014','\u00de','\u005e','\u000b','\u00db',
			'\u00e0','\u0032','\u003a',(char)10,'\u0049','\u0006','\u0024',(char)92,'\u00c2','\u00d3','\u00ac','\u0062','\u0091','\u0095','\u00e4','\u0079',
			'\u00e7','\u00c8','\u0037','\u006d','\u008d','\u00d5','\u004e','\u00a9','\u006c','\u0056','\u00f4','\u00ea','\u0065','\u007a','\u00ae','\u0008',
			'\u00ba','\u0078','\u0025','\u002e','\u001c','\u00a6','\u00b4','\u00c6','\u00e8','\u00dd','\u0074','\u001f','\u004b','\u00bd','\u008b','\u008a',
			'\u0070','\u003e','\u00b5','\u0066','\u0048','\u0003','\u00f6','\u000e','\u0061','\u0035','\u0057','\u00b9','\u0086','\u00c1','\u001d','\u009e',
			'\u00e1','\u00f8','\u0098','\u0011','\u0069','\u00d9','\u008e','\u0094','\u009b','\u001e','\u0087','\u00e9','\u00ce','\u0055','\u0028','\u00df',
			'\u008c','\u00a1','\u0089',(char)13,'\u00bf','\u00e6','\u0042','\u0068','\u0041','\u0099','\u002d','\u000f','\u00b0','\u0054','\u00bb','\u0016',};
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
		for (int i = 0; i < Key.length()/8; i++) {
			int Z =0;
			Z = Integer.parseInt(Key.substring(i*8,(i*8+8)),2);
			KeyText +=((char)Z);
		}		

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				KeyWords[i][j] = KeyText.charAt((i * 4) + j);
			}
		}
		char[] RoundConstant = new char[] {'\u0001','\u0002','\u0004','\u0008','\u0010','\u0020','\u0040','\u0080','\u001B','\u0036'};
		
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
					KeyWords[i][j]=SubstitutionMap1[Integer.valueOf(KeyWords[i][j])];	
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
