package lab1.client;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;

/**
 * This class provides some methods which allows to wait for the big B from the server.
 *
 * @author Radoslaw Lis
 */
public class Listener implements Runnable {
    private BufferedReader in;
    ClientController clientController;
    private String key;

    /**
     * Constructor which allows to create Listener object.
     * @param in object of BufferedReader class
     * @param clientController GUI client controller
     * @param key key for which listener will wait
     */
    public Listener(String key, BufferedReader in, ClientController clientController) {
        this.in = in;
        this.clientController = clientController;
        this.key = key;
    }

    /**
     * This methods starts when object of the Listener class is created and thread is started. In loop it wait
     * for the message from the server with given key in the JSONObject. When it gets the message the loop is break.
     */
    @Override
    public void run() {
        while(true){
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(in.readLine());
                if(jsonObject.has(key)) {
                    clientController.handleGettingB(new BigInteger((String) jsonObject.get(key)));
                }
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
