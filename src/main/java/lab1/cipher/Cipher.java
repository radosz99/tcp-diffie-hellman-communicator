package lab1.cipher;

import lab1.enums.Encryption;
import java.math.BigInteger;

/**
 *
 * This class provides basic encryption and decryption methods.
 *
 * @author Radoslaw Lis
 */
public class Cipher {
    // Size of used alphabet in Ceasar cipher
    static int alphabetSize = 26;

    /**
     * This is the method for encoding/decoding using XOR.
     * <p>
     * From the @secret the youngest byte is taken and char by char the output message is created.
     *
     * @param secret variable on the basis of which XOR will be done
     * @param message String which will be encoded/decoded
     * @return
     */
    public static String XOR(BigInteger secret, String message){
        String decryptedMessage = "";
        byte lowByte = secret.and(BigInteger.valueOf(0xFF)).byteValue();
        for(int i = 0; i < message.length(); i++){
            decryptedMessage += Character.toString((char)(message.charAt(i) ^ lowByte));
        }
        return decryptedMessage;
    }

    /**
     * This is the method for decoding using Ceasar cipher.
     * <p>
     * From the @secret the youngest byte is taken and method @makeCeasar with the given offset (which is based on
     * the youngest byte) and message is invoked.
     *
     * @param secret variable on the basis of which decoding using Caesar cipher will be done
     * @param message String which will be decoded
     * @return decoded message in String type
     */
    public static String decodeCaesar(BigInteger secret, String message){
        byte lowByte = secret.and(BigInteger.valueOf(0xFF)).byteValue();
        int offset = alphabetSize - (lowByte % alphabetSize);
        return makeCaesar(offset, message);

    }

    /**
     * This is the method for selecting an encoding method based on object of Encryption enumerate.
     *
     * @param encryption encryption type from the client
     * @param message String which will be encoded
     * @param secret variable on the basis of which encoding will be done
     * @return encoded message in String type
     */
    public static String encode(Encryption encryption, String message, BigInteger secret){
        String encryptedText = null;
        switch(encryption){
            case NONE:
                encryptedText = message;
                break;
            case XOR:
                encryptedText = Cipher.XOR(secret, message);
                break;
            case CEASAR:
                encryptedText = Cipher.encodeCaesar(secret, message);
                break;
        }
        return encryptedText;
    }

    /**
     * This is the method for selecting an decoding method based on object of Encryption enumerate.
     *
     * @param encryption encryption type from the client
     * @param message String which will be decoded
     * @param secret variable on the basis of which decoding will be done
     * @return decoded message in String type
     */
    public static String decode(Encryption encryption, String message, BigInteger secret) {
        String decodedText = null;
        switch (encryption) {
            case NONE:
                decodedText = message;
                break;
            case XOR:
                decodedText = Cipher.XOR(secret, message);
                break;
            case CEASAR:
                decodedText = Cipher.decodeCaesar(secret, message);
                break;
        }
        return decodedText;
    }

    /**
     * This is the method for encoding using Ceasar cipher.
     * <p>
     * From the @secret the youngest byte is taken and method @makeCeasar with the given offset (which is based on
     * the youngest byte) and message is invoked.
     *
     * @param secret variable on the basis of which encoding using Caesar cipher will be done
     * @param message String which will be encoded
     * @return encoded message in String type
     */
    public static String encodeCaesar(BigInteger secret, String message){
        byte lowByte = secret.and(BigInteger.valueOf(0xFF)).byteValue();
        int offset = lowByte % alphabetSize;
        return makeCaesar(offset, message);
    }

    /**
     * This is the method for changing input message using Ceasar cipher with given offset.
     * @param offset integer variable by which to shift
     * @param message String variable which will be changed
     * @return output String
     */
    private static String makeCaesar(int offset, String message){
        String result = "";
        for (char character : message.toCharArray()) {
            if (character != ' ') {
                int originalAlphabetPosition = character - 'a';
                int newAlphabetPosition = (originalAlphabetPosition + offset) % alphabetSize;
                char newCharacter = (char) ('a' + newAlphabetPosition);
                result += Character.toString(newCharacter);
            } else {
                result += Character.toString(character);
            }
        }
        return result;
    }
}
