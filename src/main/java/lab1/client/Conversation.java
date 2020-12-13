package lab1.client;

import lab1.cipher.Cipher;
import lab1.enums.Sender;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Base64;

/**
 * This class provides some methods which allows to exchanging messages with the server.
 *
 * @author Radoslaw Lis
 */
public class Conversation implements Runnable {
    private BufferedReader in;
    private ClientController clientController;

    /**
     * Constructor which allows to create Conversation object.
     * @param in object of BufferedReader class
     * @param clientController GUI client controller
     */
    public Conversation(BufferedReader in, ClientController clientController) {
        this.in = in;
        this.clientController = clientController;
    }

    /**
     * This methods starts when object of the Conversation class is created and thread is started. In infinite loop
     * it wait for a message from the server, decoded it and shows it in GUI.
     */
    @Override
    public void run() {
        while(true){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(in.readLine());

            } catch (IOException e) {
                e.printStackTrace();
            }
            String decodedText = new String(Base64.getDecoder().decode((String) jsonObject.get("msg")));
            decodedText = Cipher.decode(clientController.getCurrentEncryptionType(), decodedText, clientController.getSecret());
            clientController.showMessage(decodedText, Sender.SERVER);
        }
    }
}
