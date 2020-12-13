package lab1.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Base64;
import lab1.cipher.Cipher;
import lab1.client.SimpleClient;
import lab1.enums.Encryption;
import org.json.JSONObject;

class Session implements Runnable {
    private PrintWriter out;
    private BufferedReader in;
    private Socket clientSocket;
    private BigInteger b;
    private BigInteger B;
    private BigInteger secret;
    private BigInteger A;
    private BigInteger p;
    private BigInteger g;
    private Generator generator;
    ServerController serverController;

    /**
     * This constructor initialize @out and @in parameters.
     *
     * @param clientSocket socket of the client who participates in the session
     * @param serverController GUI controller
     * @throws IOException
     */
    Session(Socket clientSocket, ServerController serverController) throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        generator = new Generator();
        this.p = generator.getP();
        this.g = generator.getG();

        this.clientSocket = clientSocket;
        this.serverController = serverController;
    }

    /**
     * This is main method of the session. Server tries to send B to the client and then - in loop - it waits for
     * messages from the client and invoke @messageHandler method which deal with message.
     */
    @Override
    public void run() {
        try {
            sendBToTheClient();
            while(true) {
                JSONObject jsonObject = new JSONObject(in.readLine());
                messageHandler(jsonObject);
            }
            } catch (IOException i) {
                System.err.println(i.getMessage());
        }
    }

    /**
     * This is private method which takes JSONObject as a parameter and - depends of the keys - it invoke right
     * method.
     * @param jsonObject JSON from the client
     */
    private void messageHandler(JSONObject jsonObject) {
        if (jsonObject.has("request") && jsonObject.get("request").equals("keys")) {
            handleKeysRequest();
        } else if (jsonObject.has("a")) {
            handleReceivingA((String) jsonObject.get("a"));
        } else if (jsonObject.has("encryption")) {
            serverController.changeClientEncryptionType(clientSocket.getPort(), Encryption.valueOf((String) jsonObject.get("encryption")));
        } else if (jsonObject.has("msg")) {
            handleReceivingMessages((String) jsonObject.get("msg"));
        } else {
            out.println("Undefined request");
        }
    }

    /**
     * This method sends value of the B parameter to the client.
     */
    private void sendBToTheClient(){
        b = generator.getB();
        JSONObject jsonB = new JSONObject();
        B = g.modPow(b,p);
        jsonB.put("b", B);
        out.println(jsonB);
    }

    /**
     * This method sends value of @p and @g parameters to the client in JSONObject type.
     */
    private void handleKeysRequest(){
        JSONObject numbers = new JSONObject();
        numbers.put("p", p);
        numbers.put("g", g);
        numbers.put("client_port", clientSocket.getPort());
        out.println(numbers);
    }

    /**
     * This method is invoked when client send big A. Secret is calculated and it is updated
     * in GUI.
     * @param strA value of big A from the JSON from the client
     */
    private void handleReceivingA(String strA){
        A = new BigInteger(strA);
        secret = A.modPow(b, p);
        serverController.changeClientSecret(clientSocket.getPort(), secret);
    }

    /**
     * This method is responsible for updating server GUI when server receives new message from the client.
     * @param message String from the client
     */
    private void handleReceivingMessages(String message){
        int portId = clientSocket.getPort();
        for(SimpleClient simpleClient : serverController.getClientsList()){
            if(simpleClient.getPortID() == portId){
                String decodedText = new String(Base64.getDecoder().decode(message));
                decodedText = Cipher.decode(simpleClient.getEncryptionType(), decodedText, secret);
                serverController.showMessage(decodedText, clientSocket.getPort());
            }
        }
    }
}

