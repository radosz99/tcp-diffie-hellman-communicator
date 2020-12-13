package cipher.test_cases;

import lab1.cipher.Cipher;
import lab1.enums.Encryption;
import lab1.server.Generator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestCeasar {
    private static int iterations = 100;
    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[iterations][0];
    }

    @Test
    public void testEncoding(){
        String message = Generator.generateRandomString();
        Random random = new Random();
        BigInteger secret = new BigInteger(1000, random);
        String encodedMessage = Cipher.encode(Encryption.CEASAR, message, secret);
        String decodedMessage = Cipher.decode(Encryption.CEASAR, encodedMessage, secret);
        assertEquals(message, decodedMessage);
    }
}
