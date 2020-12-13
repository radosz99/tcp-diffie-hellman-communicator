package lab1.server;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * This class allows to calculate parameters needed for the Diffie-Hellman protocol.
 *
 * @author Radoslaw Lis
 */
public class Generator {
    public BigInteger getP() {
        return p;
    }

    public BigInteger getG() {
        return g;
    }

    private BigInteger p;
    private BigInteger g;
    private BigInteger b;
    private BigInteger a;
    private static SecureRandom random = new SecureRandom();

    public BigInteger getB() {
        return b;
    }

    public BigInteger getA() {
        return a;
    }

    /**
     * In this constructor all parameters are created or calculated.
     */
    public Generator() {
        p = BigInteger.probablePrime(1024, random);
        g = new BigInteger(256, random);
        b = new BigInteger(512, random);
        a = new BigInteger(512, random);
    }

    /**
     * This method allows to generate random string with random length.
     * @return created random String
     */
    public static String generateRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = random.nextInt(100) + 1;
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
