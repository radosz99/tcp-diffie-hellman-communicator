package cipher.test_cases;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import static org.junit.Assert.assertEquals;



public class TestBigInteger {
    @Test
    public void testProcess(){
        SecureRandom secureRandom = new SecureRandom();
        BigInteger a = new BigInteger(512, secureRandom);
        BigInteger b = new BigInteger(512, secureRandom);

        BigInteger p = BigInteger.probablePrime(1024, secureRandom);
        BigInteger g = new BigInteger(256, secureRandom);

        BigInteger A = g.modPow(a, p);
        BigInteger B = g.modPow(b,p);

        BigInteger clientK = B.modPow(a,p);
        BigInteger serverK = A.modPow(b,p);

        System.err.println(clientK);


        BigInteger k = clientK.and(BigInteger.valueOf(0xFF));
        System.err.println(k);
        assertEquals(clientK, serverK);
    }
}
