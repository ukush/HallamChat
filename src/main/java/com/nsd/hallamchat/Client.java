package com.nsd.hallamchat;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Client extends Application
implements Initializable {


    public static String username;
    public static Socket client;
    public static BufferedReader in;
    public static PrintWriter out;
    public static Scanner sc;
    //-------------- Login Page ----------------------//

    @FXML
    TextField name;
    @FXML
    Label wrongLogin;
    @FXML
    TextField hostname;
    @FXML
    TextField port;
    @FXML
    Label wrongServer;
    @FXML
    Label serverDetails;
    @FXML
    Button loginButton;


    //-------------- Chat Page ----------------------//

    @FXML
    Button disconnect;
    @FXML
    ImageView uploadPic;
    @FXML
    Label welcome;
    @FXML
    TextField enterMessage;
    @FXML
    Button sendMsg, refresh, viewMedia, sendFile;
    @FXML
    Button attachFile;
    @FXML
    ChoiceBox<String> choiceBox;
    @FXML
    Button showChannels, sub, unsub;
    @FXML
    VBox messageBox;
    @FXML
    ListView<String> fileStage;
    @FXML
    ImageView profilePic, viewImage;




    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hallamchat/LoginPage.fxml")));
        primaryStage.setTitle("HallamChat");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) throws IOException {
        launch(args);
        //String hostName = "localhost";
        //int portNumber = 12345;
//
        //try (
        //        Socket socket = new Socket(hostName, portNumber);
        //        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        //        BufferedReader in = new BufferedReader(
        //                new InputStreamReader(socket.getInputStream()));
        //        BufferedReader stdIn = new BufferedReader(
        //                new InputStreamReader(System.in))
        //) {
        //    String userInput;
        //    while ((userInput = stdIn.readLine()) != null) {
        //        // Parse user and build request
        //        Request req;
//
//
        //        Scanner sc = new Scanner(userInput);
        //        try {
        //            switch (sc.next()) {
        //                case "login": {
        //                    username = sc.skip(" ").nextLine(); // this sets the username which can be used for all other requests
        //                    req = new LoginRequest(username);
        //                    break;
        //                }
        //                case "open":{
        //                    req = new OpenRequest(username);
        //                    break;
        //                }
        //                case "publish": {
        //                    String channel = sc.skip(" ").next();
        //                    String message = sc.skip(" ").nextLine();
        //                    req = new PublishRequest(channel, username, message);
        //                    break;
        //                }
        //                case "get":
        //                    req = new GetRequest(sc.skip(" ").nextLine());
        //                    break;
        //                case "sub":
        //                    req = new SubscribeRequest(username, sc.skip(" ").nextLine());
        //                    break;
        //                case "unsub":
        //                    req = new UnsubscribeRequest(username, sc.skip(" ").nextLine());
        //                    break;
        //                case "quit":
        //                    req = new QuitRequest();
        //                    break;
        //                default:
        //                    System.out.println("ILLEGAL COMMAND");
        //                    continue;
        //            }
        //        } catch (NoSuchElementException e) {
        //            System.out.println("ILLEGAL COMMAND");
        //            continue;
        //        }
//
        //        // Send request to server
        //        out.println(req);
//
        //        // first line from the server is always the number of lines to read next
        //        int responseLines = Integer.parseInt(in.readLine());
//
        //        // Read server response; terminate if null (i.e. server quit)
        //        String serverResponse;
//
        //        for (int i = 0; i < responseLines; i++) {
//
        //            if ((serverResponse = in.readLine()) == null)
        //                break;
//
        //            // Parse JSON response, then try to deserialize JSON
        //            Object json = JSONValue.parse(serverResponse);
        //            Response resp;
//
        //            // Try to deserialize a success response
        //            if (SuccessResponse.fromJSON(json) != null)
        //                continue;
//
        //            // Try to deserialize a list of messages
        //            if ((resp = MessageListResponse.fromJSON(json)) != null) {
        //                for (Message m: ((MessageListResponse)resp).getMessages())
        //                    System.out.println(m);
        //                continue;
        //            }
//
        //            // Try to deserialize an error response
        //            if ((resp = ErrorResponse.fromJSON(json)) != null) {
        //                System.out.println(((ErrorResponse)resp).getError());
        //                continue;
        //            }
//
        //            // Try to deserialize a Quit response
        //            if ((resp = QuitResponse.fromJSON(json)) != null) {
        //                System.out.println((("Session ended")));
        //                // close the socket
        //                socket.close();
        //                continue;
        //            }
//
        //            // Not any known response
        //            System.out.println("PANIC: " + serverResponse +
        //                    " parsed as " + json);
        //            break;
        //        }
        //    }
        //} catch (UnknownHostException e) {
        //    System.err.println("Don't know about host " + hostName);
        //    System.exit(1);
        //} catch (IOException e) {
        //    System.err.println("Couldn't get I/O for the connection to " +
        //            hostName);
        //    System.exit(1);
        //}
    }

    public void startClient(String hostname, String portnumber) throws IOException {

        int port = Integer.parseInt(portnumber);

        // create socket, reader and writer
        try {
            client = new Socket(hostname, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    //------------------ Connect Scene Methods -------------------------------//


    public void connect(ActionEvent actionEvent) throws IOException {

        /**
         * Usernames can only be between 4-10 characters
         * Cannot have duplicate usernames
         * Database is checked for duplicate usernames
         *
         * Once usernames have been verified, check hostname and port number are also valid
         * If all are valid, change to the next scene
         */


        hostname.setText("localhost");
        port.setText("1234");


        if (isUnameValid(name.getText())) {

            // set username
            username = name.getText();

            if (hostname.getText().isEmpty() || port.getText().isEmpty()) {
                wrongServer.setText("Missing server details");
            }
//
            else if (!hostname.getText().equals("localhost") && !hostname.getText().isEmpty()) {
                wrongServer.setText("Cannot find host");
            }
            else if (!port.getText().equals("1234") && !port.getText().isEmpty()) {
                wrongServer.setText("Incorrect Port Number");
            }
//
            else {
                // both local host and port number must be valid
                // Make connection to server
                startClient(hostname.getText(), port.getText());
                // send a login request
                sendLoginRequest();
                // send an open request
                sendOpenRequest();
                // change scene
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hallamchat/Chat.fxml")));
                Stage window = (Stage) loginButton.getScene().getWindow();
                window.setScene(new Scene(root, 600, 400));
            }
        }
        else {
            // invalid username
            wrongLogin.setText("That username has been taken");
        }
    }

    public boolean isUnameValid(String username) {
        return true;
    }

    public void sendLoginRequest() throws IOException {
        Request req = new LoginRequest(username);
        out.println(req);
        getResponse();
    }

    //------------------ Chat Scene Methods -------------------------------//

    public void sendOpenRequest() throws IOException {
        Request req = new OpenRequest(username);
        out.println(req);
        getResponse();
    }

    public void showOpenChannels(ActionEvent actionEvent) throws IOException {
        // get the open channels from the server
        // add these channels to the choiceBox
        sendChannelRequest();
    }

    public void sendPublishRequest(ActionEvent actionEvent) throws IOException {
        // get channel value from currentChannel selected
        String channel = choiceBox.getValue();
        String message = enterMessage.getText();
        // clear the message field
        enterMessage.clear();

        Request req = new PublishRequest(channel, username, message);
        out.println(req);
        getResponse();

        sendGetRequest();
    }

    public void sendSubscribeRequest(ActionEvent actionEvent) throws IOException {
        String subChannel = choiceBox.getValue();
        String currentChannel =  username;
        Request req = new SubscribeRequest(currentChannel, subChannel);
        out.println(req);
        getResponse();
    }

    public void sendUnsubscribeRequest(ActionEvent actionEvent) throws IOException {
        String unsubChannel = choiceBox.getValue();
        String currentChannel =  username;
        Request req = new UnsubscribeRequest(currentChannel, unsubChannel);
        out.println(req);
        getResponse();
    }

    public void sendQuitRequest(ActionEvent actionEvent) throws IOException {
        Request req = new QuitRequest();
        out.println(req);
        getResponse();

    }

    public void sendGetRequest() throws IOException {
        // get channel from current/selected channel
        String channel = choiceBox.getValue();
        Request req = new GetRequest(channel);
        out.println(req);
        getResponse();
    }

    public void sendChannelRequest() throws IOException {
        Request req = new ChannelRequest(username);
        out.println(req);
        getResponse();
    }

    public void getResponse() throws IOException {

        // first line from the server is always the number of lines to read next
        int responseLines = Integer.parseInt(in.readLine());
        // Read server response; terminate if null (i.e. server quit)
        String serverResponse;

        for (int i = 0; i < responseLines; i++) {
            if ((serverResponse = in.readLine()) == null)
                break;

            // Parse JSON response, then try to deserialize JSON
            Object json = JSONValue.parse(serverResponse);
            Response resp;


            // Try to deserialize a success response
            if (SuccessResponse.fromJSON(json) != null){
                continue;
            }


            // Try to deserialize a list of messages
            if ((resp = MessageListResponse.fromJSON(json)) != null) {
                // instead of displaying messages to console, add them to the message area
                for (Message m: ((MessageListResponse)resp).getMessages())
                    // add message body to the vbox
                    messageBox.getChildren().add(new Text(m.getAuthor() + ": " + m.getBody()));
                continue;
            }

            // deserialize a list of open channels
            if ((resp = ChannelResponse.fromJSON(json)) != null) {
                //StringBuilder line = new StringBuilder();
                for (String s : ((ChannelResponse)resp).getChannels()) {
                    if (!choiceBox.getItems().contains(s))
                        choiceBox.getItems().add(s);
                }
                choiceBox.setValue(username);
                continue;
            }


            // Try to deserialize an error response
            if ((resp = ErrorResponse.fromJSON(json)) != null) {
                // display error response as a pop up
                System.out.println(((ErrorResponse)resp).getError());
                continue;
            }


            // Try to deserialize a Quit response
            if ((resp = QuitResponse.fromJSON(json)) != null) {
                System.out.println((("Session ended")));
                // close the socket
                //socket.close();
                continue;
            }
            // Not any known response
            System.out.println("PANIC: " + serverResponse +
                    " parsed as " + json);
            break;
        }
    }

    public void selectFile(ActionEvent actionEvent){
        // opens a pop up that allows the user to select a file from their machine
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            // add to listView
            fileStage.getItems().add(selectedFile.getName());
        }
    }

    public void uploadPP(MouseEvent actionEvent) {
        // opens a pop up that allows the user to select a file from their machine
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            // change profile pic
            profilePic.setPreserveRatio(true);
            profilePic.setImage(image);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
