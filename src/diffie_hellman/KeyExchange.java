package diffie_hellman;
import java.util.Random;
import java.math.BigInteger;

public class KeyExchange{
	public static final BigInteger p = new BigInteger("11152369815908100909805641654202996053525795381774543504241525589886039826540697020559270571527270062975460857198594611453639399492282383678143319271877444716155749412084295178840151479280439847349167907204793647481959343186979602676272171780062741930175180635934007024611500699081960132743057502249149320162743612203429339757070065211643432608583020938344285669486707709774229157423194313376646071613379793198420181492695660100979301942738593440407281300031197890831922646664845531230276625278459058340702082240812853541075023564730420021426695658275038039112927230722068962624306752752768850013535802962607985574422160342786020971430814219547353076386254368962085576729990143468422443849637825638154998847943104678374912385758399024485454884784607673488031420854657941179722608063770422762056103131457579987594798661644433712917666050109724187365980037014821480745032487812579995310684959648534889755770519839");
	public static final int g = 17;
	
//	private static  BigInteger I = BigInteger.ZERO;
	public static BigInteger randomPrime () {
		BigInteger I = null;
		boolean isPrime = false;
        while (!isPrime) {
            Random r = new Random();
            String s = "";
            for (int i = 0; i < 50; i++) {
                s += Integer.toBinaryString(r.nextInt());
            }
            I = new BigInteger(s, 2);
            isPrime = I.isProbablePrime(Integer.MAX_VALUE);
        }
        return I;
	}
	
    public static BigInteger step1 (BigInteger I) {
		BigInteger J = new BigInteger("" + g);
		BigInteger A = J.modPow(I, p);
		return A;
	}
	
	public static BigInteger step2 (BigInteger B, BigInteger I) {
		BigInteger A = B.modPow(I, p);
		return A;
		
	}
}