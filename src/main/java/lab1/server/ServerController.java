package lab1.server;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lab1.cipher.Cipher;
import lab1.enums.Encryption;
import lab1.client.SimpleClient;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class provides basic control operations on the GUI server side.
 *
 * @author Radoslaw Lis
 */
public class ServerController implements Initializable {
    @FXML private Button runBtn = new Button();
    @FXML private TableView<SimpleClient> clients = new TableView();
    @FXML private TableColumn<SimpleClient, Integer> column1;
    @FXML private TableColumn<SimpleClient, Integer> column2;
    @FXML private TableColumn<SimpleClient, String> column3;
    @FXML private TableColumn<SimpleClient, Encryption> column4;
    @FXML private TextField messageTextField = new TextField();
    @FXML private Button messageBtn = new Button();
    @FXML private TextArea messagesTextArea = new TextArea();

    private String messagesHistory = "";
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private ServerSocket serverSocket;

    public List<SimpleClient> getClientsList() {
        return clientsList;
    }

    private List<SimpleClient> clientsList = new ArrayList<>();
    private static int portID = 6666;
    List<Socket> socketList = new ArrayList<>();

    /**
     * This method is invoked on the beginning and it sets all properties to all of the GUI elements.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        column1 = new TableColumn<>("Client ID");
        column1.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        column2 = new TableColumn<>("Port number");
        column2.setCellValueFactory(new PropertyValueFactory<>("portID"));
        column3 = new TableColumn<>("Secret");
        column3.setCellValueFactory(new PropertyValueFactory<>("secret"));
        column4 = new TableColumn<>("Encryption");
        column4.setCellValueFactory(new PropertyValueFactory<>("encryptionType"));
        column1.setSortable(false);
        column2.setSortable(false);
        column3.setSortable(false);
        column4.setSortable(false);
        clients.getColumns().addAll(column1, column2, column3, column4);
        clients.setPlaceholder(new Label("No clients"));
    }

    /**
     * This method shows message in messages history text area.
     * @param message String which will be shown
     * @param port integer which hold number of the client port
     */
    public void showMessage(String message, int port){
        messagesHistory += dtf.format(LocalDateTime.now()) + "\t" + port + ": " + message + "\n";
        messagesTextArea.setText(messagesHistory);
    }

    /**
     * This method update GUI when client is connecting to the server
     *
     * @param clientId client id
     * @param port port on which client is connected
     */
    public void addClient(int clientId, int port){
        clientsList.add(new SimpleClient(clientId, port));
        clients.setItems(FXCollections.observableList(clientsList));
    }

    /**
     * This method shows new window on the GUI with error log.
     * @param message error log
     */
    private void badAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    /**
     * This method sends message from server to client. Port is taken from row from table. Message is taken from
     * message text field and encoded in right encryption and then send to the client.
     *
     * @throws IOException
     */
    public void sendMessage() throws IOException {
        if(messageTextField.getText().length()==0){
            badAlert("Type a message!");
            return;
        }
        try {
            int port = clients.getSelectionModel().getSelectedItem().getPortID();

        for(Socket socket : socketList){
            if(socket.getPort() == port){
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                JSONObject jsonObject = new JSONObject();
                String encryptedText = Cipher.encode(clients.getSelectionModel().getSelectedItem().getEncryptionType(), messageTextField.getText(), clients.getSelectionModel().getSelectedItem().getSecret());
                jsonObject.put("msg", Base64.getEncoder().encodeToString(encryptedText.getBytes()));
                jsonObject.put("from", "server");
                out.println(jsonObject);
                messageTextField.clear();
            }
        }
        } catch (NullPointerException n){
            badAlert("Choose client for the message!");
        }
    }

    /**
     * This is method which change value of secret in GUI.
     *
     * @param port port of the client whose secret need to be changed
     * @param secret current value of the secret
     */
    public void changeClientSecret(int port, BigInteger secret){
        for(SimpleClient client : clientsList){
            if(client.getPortID() == port){
                client.setSecret(secret);
                clientsList.remove(client);
                clientsList.add(new SimpleClient(client.getClientID(), client.getPortID(), client.getSecret()));
                clients.setItems(FXCollections.observableList(clientsList));
                break;
            }
        }
    }

    /**
     * This is method which change client encryption type in GUI.
     *
     * @param port port of the client whose encryption type need to be changed
     * @param encryption current encryption type
     */
    public void changeClientEncryptionType(int port, Encryption encryption){
        for(SimpleClient client : clientsList){
            if(client.getPortID() == port){
                client.setEncryptionType(encryption);
                clientsList.remove(client);
                clientsList.add(new SimpleClient(client.getClientID(), client.getPortID(),  client.getEncryptionType(), client.getSecret()));
                clients.setItems(FXCollections.observableList(clientsList));
                break;
            }
        }
    }

    /**
     * This method starts thread which is responsible for accepting connections from clients and starting
     * new threads (sessions).
     */
    public void runServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(portID);
                runBtn.setDisable(true);
                Platform.runLater(
                            () -> {
                            runBtn.setText("Server is running...");
                        }
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    socketList.add(clientSocket);
                    addClient(clientsList.size() + 1, clientSocket.getPort());
                    Thread thread = new Thread(new Session(clientSocket, this));
                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
