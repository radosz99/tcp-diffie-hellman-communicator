package lab1.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lab1.cipher.Cipher;
import lab1.enums.Encryption;
import lab1.enums.Sender;
import lab1.server.Generator;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.ResourceBundle;

/**
 * This class provides basic control operations on the GUI client side.
 *
 * @author Radoslaw Lis
 */
public class ClientController implements Initializable {

    @FXML
    Button connectBtn = new Button();

    @FXML Button requestBtn = new Button();

    @FXML
    TextField valueP = new TextField();

    @FXML
    TextField valueG = new TextField();

    @FXML
    ComboBox<Encryption> encryptionComboBox = new ComboBox<>();
    @FXML Button sendEncryptionBtn = new Button();
    @FXML TextField valueA = new TextField();
    @FXML TextField valueBigA = new TextField();
    @FXML TextField valueB = new TextField();
    @FXML TextField valueSecret = new TextField();
    @FXML Label portNumber = new Label();
    @FXML Button drawBtn = new Button();
    @FXML Button sendA = new Button();
    @FXML Button calculateSecret = new Button();
    @FXML Button sendMessageBtn = new Button();
    @FXML TextField messageTextField = new TextField();
    @FXML TextArea messageHistory = new TextArea();
    @FXML Button clearHistoryBtn = new Button();

    private Generator generator = new Generator();
    private static String IP = "127.0.0.1";
    private static int portID = 6666;
    private Client client;
    private BigInteger p;
    private BigInteger g;
    private BigInteger a;
    private BigInteger B;
    private BigInteger A;

    /**
     * This method returns calculated client's secret.
     *
     * @return BigInteger variable which is secret
     */
    public BigInteger getSecret() {
        return secret;
    }

    private BigInteger secret;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private int port;
    private String messagesHistory = "";

    /**
     * This method returns current encryption type selected by the client.
     *
     * @return variable on enumerate Encryption
     */
    public Encryption getCurrentEncryptionType() {
        return currentEncryptionType;
    }

    private Encryption currentEncryptionType = Encryption.NONE;

    /**
     * This method is invoked on the beginning and it sets all properties to all of the GUI elements.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        valueP.setEditable(false);
        valueG.setEditable(false);
        valueA.setEditable(false);
        valueBigA.setEditable(false);
        valueB.setEditable(false);
        valueSecret.setEditable(false);
        sendA.setDisable(true);
        drawBtn.setDisable(true);
        requestBtn.setDisable(true);
        sendMessageBtn.setDisable(true);
        calculateSecret.setDisable(true);
        sendEncryptionBtn.setDisable(true);
        encryptionComboBox.getItems().addAll(Encryption.NONE, Encryption.CEASAR, Encryption.XOR);
        encryptionComboBox.setValue(Encryption.NONE);
        sendEncryptionBtn.setOnMouseClicked(event -> {
            currentEncryptionType = encryptionComboBox.getSelectionModel().getSelectedItem();
        });
    }

    /**
     * This methods draws little a and calculate bigA and also handle turning off appropriate buttons.
     */
    public void drawA(){
        a = generator.getA();
        valueA.setText(String.valueOf(a));
        calculateBigA();
        sendA.setDisable(false);
        drawBtn.setDisable(true);
        calculateSecret.setDisable(false);
    }

    /**
     * This methods shows message in messages history text area.
     * @param message String which will be shown
     * @param sender enumerate which hold sender of the message
     */
    public void showMessage(String message, Sender sender){
        messagesHistory += dtf.format(LocalDateTime.now()) + "\t" + sender.toString() + ": " + message + "\n";
        messageHistory.setText(messagesHistory);
    }

    /**
     * This method allows to clear all current messages in GUI.
     */
    public void clearHistory(){
        messagesHistory = "";
        messageHistory.setText(messagesHistory);
    }

    /**
     * This method provides sending calculated big A to the server to let him calculate the secret.
     */
    public void sendBigA(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a", A);
        client.onlySendMessage(jsonObject);
        sendA.setDisable(true);
    }

    /**
     * This method provides functionality of showing big B on the GUI.
     * @param b BigInteger variable received from the server side where it was calculated
     */
    public void handleGettingB(BigInteger b){
        B = b;
        valueB.setText(String.valueOf(B));
    }

    /**
     * This method allows to change encryption type during having conversation with the server.
     */
    public void sendEncryptionType(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("encryption", encryptionComboBox.getSelectionModel().getSelectedItem());
        client.onlySendMessage(jsonObject);
    }

    /**
     * This method calculate secret based on a @a and @p values and start new thread which is responsible for
     * exchanging messages with the server.
     *
     * @throws IOException
     */
    public void calculateSecret() throws IOException {
        secret = B.modPow(a,p);
        valueSecret.setText(String.valueOf(secret));
        sendEncryptionBtn.setDisable(false);
        calculateSecret.setDisable(true);
        sendMessageBtn.setDisable(false);

        startConversation();
    }

    /**
     * This method creates object of Conversation class which inherits from Runnable class, so it can be easily
     * start as a new thread.
     * @throws IOException
     */
    public void startConversation() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
        Thread thread = new Thread(new Conversation(in, this));
        thread.start();
    }

    /**
     * This method is invoked when connection is started and it start the listener which waits for the message from
     * the server with big B value.
     * @throws IOException
     */
    public void startListeningForB() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
        Thread thread = new Thread(new Listener("b", in, this));
        thread.start();
    }

    /**
     * This method provides functionality of sending messages. It creates JSONObject object based on a encrypted with
     * given encryption type and Base64 of a String which is taken from the appropriate text field. Also it shows
     * message in messages history and clear message text field.
     */
    public void sendMessage(){
        JSONObject jsonObject = new JSONObject();
        String encryptedText = Cipher.encode(encryptionComboBox.getSelectionModel().getSelectedItem(), messageTextField.getText(), secret);
        jsonObject.put("msg", Base64.getEncoder().encodeToString(encryptedText.getBytes()));
        jsonObject.put("from", "someone");
        client.onlySendMessage(jsonObject);

        showMessage(messageTextField.getText(), Sender.ME);
        messageTextField.clear();
    }

    /**
     * This is method responsible for calculating big A and showing it on the GUI.
     */
    public void calculateBigA(){
        A = g.modPow(a,p);
        valueBigA.setText(String.valueOf(A));
    }

    /**
     * This method handle connection to the server. In another thread it creates new class Client object
     * and connect to the server and starts listening for the big B value from the server.
     */
    public void startConnection() {
        new Thread(() -> {
            try {
                client = new Client();
                client.startConnection(IP, portID);

                connectBtn.setDisable(true);
                startListeningForB();
                Platform.runLater(
                        () -> {
                            requestBtn.setDisable(false);
                            connectBtn.setText("Connected to the server");
                        }
                );
            } catch (IOException i) {
                Platform.runLater(
                        () -> {
                            badAlert("Server is not running!");
                        });
            }
        }).start();
    }

    /**
     * This method allows to send request for the @p and @g parameters to the server. It sends message and waits
     * for the response and take appropriate values from the given JSON. Also it sets those values on the GUI.
     *
     * @throws IOException
     */
    public void sendRequestForKeys() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("request", "keys");
        String response = client.sendMessage(jsonObject);
        try {
            JSONObject numbers = new JSONObject(response);
            this.p = new BigInteger((String) numbers.get("p"));
            this.g = new BigInteger((String) numbers.get("g"));
            this.port = numbers.getInt("client_port");
            Platform.runLater(
                    () -> {
                        portNumber.setText(String.valueOf(this.port));
                        requestBtn.setDisable(true);
                        drawBtn.setDisable(false);
                        valueP.setText(String.valueOf(p));
                        valueG.setText(String.valueOf(g));
                    }
            );
        } catch (JSONException i){
            Platform.runLater(
                    () -> {
                        badAlert("Somehow fail!");
                    });
        }
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

}
