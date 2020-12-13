package lab1.client;

import lab1.enums.Encryption;
import java.math.BigInteger;

/**
 * This class facilitates storing and displaying clients information on the GUI. Full of getters, setters and
 * constructors.
 *
 * @author Radoslaw Lis
 */
public class SimpleClient {
    int clientID;
    int portID;
    Encryption encryptionType;
    BigInteger secret;

    public BigInteger getSecret() {
        return secret;
    }

    public void setSecret(BigInteger secret) {
        this.secret = secret;
    }

    public int getClientID() {
        return clientID;
    }

    public Encryption getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(Encryption encryptionType) {
        this.encryptionType = encryptionType;
    }

    public int getPortID() {
        return portID;
    }

    public SimpleClient(int clientID, int portID, Encryption encryptionType, BigInteger secret) {
        this.clientID = clientID;
        this.portID = portID;
        this.encryptionType = encryptionType;
        this.secret = secret;
    }

    public SimpleClient(int clientID, int portID, BigInteger secret) {
        this.clientID = clientID;
        this.portID = portID;
        this.secret = secret;
        this.encryptionType = Encryption.NONE;
    }

    public SimpleClient(int clientID, int portID) {
        this.clientID = clientID;
        this.portID = portID;
        this.secret = BigInteger.valueOf(-1);
        this.encryptionType = Encryption.NONE;
    }
}
