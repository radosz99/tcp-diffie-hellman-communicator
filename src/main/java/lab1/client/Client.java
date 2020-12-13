package lab1.client;

import java.io.*;
import java.net.*;
import org.json.JSONObject;

/**
 * This class provides basic operations on the client side such as sending message to the server.
 *
 * @author Radoslaw Lis
 */
public class Client {
    /**
     * This method returns client socket.
     *
     * @return client socket holding the connection
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * This method allows to connect client to the server.
     *
     * @param ip name of the host
     * @param port number of the port on which server works
     * @throws IOException
     */
    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    /**
     * This method allows sending message and waiting for the response to the server using PrintWriter class.
     *
     * @param jsonObject object which will be send to the server
     * @return
     * @throws IOException
     */
    public String sendMessage(JSONObject jsonObject) throws IOException {
        out.println(jsonObject);
        String resp = in.readLine();
        return resp;
    }

    /**
     * This method allows sending message and waiting for the response to the server using PrintWriter class.
     *
     * @param jsonObject object which will be send to the server
     * @return
     * @throws IOException
     */
    public void onlySendMessage(JSONObject jsonObject){
        out.println(jsonObject);
    }
}
