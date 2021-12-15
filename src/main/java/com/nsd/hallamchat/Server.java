package com.nsd.hallamchat;

import org.json.simple.JSONValue;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

//----------------------------------- Server Member Variables -------------------------------------------------//

    public static ArrayList<Channel> channels = new ArrayList<>();

//----------------------------------- Client Handler -------------------------------------------------//
    static class ClientHandler implements Runnable {

        // login name; null if not set
        private String login;

        private Socket client;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) throws IOException {
            client = socket;
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
        }

        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {

                    // number of response lines that the client should read
                    int linesToRead = 1;

//----------------------------------- Login -------------------------------------------------//

                    // logging request (+ login if possible) to server console
                    if (login != null)
                        System.out.println(login + ": " + inputLine);
                    else
                        System.out.println(inputLine);

                    // parse request, then try to deserialize JSON
                    Object json = JSONValue.parse(inputLine);
                    Request req;

                    // login request? Must not be logged in already
                    if (/*login == null &&*/
                            (req = LoginRequest.fromJSON(json)) != null) {
                        // set login name
                        login = ((LoginRequest) req).getName();

                        // send number of response lines to client
                        out.println(linesToRead); // only 1 line to read (success)

                        // response acknowledging the login request
                        out.println(new SuccessResponse());
                        continue;
                    }
//----------------------------------- Open -------------------------------------------------//

                    // open request. Must be logged in
                    if (/*login == null &&*/
                            (req = OpenRequest.fromJSON(json)) != null) {

                        String identity = ((OpenRequest) req).getIdentity();

                        // create a new channel with that name
                        Channel channel = new Channel(identity);
                        // add channel to the collection of channels
                        channels.add(channel);

                        // append new channel to file
                        PrintWriter printWriter = new PrintWriter(new FileWriter("channels.txt", true));
                        printWriter.print(channel);

                        // add client name to messagesRead hashmap --> currently they've read 0 messages
                        channel.addClient(identity);

                        // send number of response lines to client
                        out.println(linesToRead); // only 1 line to read (success)

                        // response acknowledging the post request
                        out.println(new SuccessResponse());
                        continue;

                    }
//----------------------------------- Publish -------------------------------------------------//

                    // Must be logged in and channel also has to be open
                    if (/*login == null &&*/
                            (req = PublishRequest.fromJSON(json)) != null &&
                            (channels.size() != 0)) {

                        // retrieve the channel that the client wants to publish to
                        // retrieve the message object
                        String channelName = ((PublishRequest) req).getIdentity();
                        Message message = ((PublishRequest) req).getMessage();


                        // only clients that are subscribed to a channel can publish messages
                        // Get the channel
                        Optional<Channel> channelMatch = channels.parallelStream()
                                .filter(ch -> ch.getName().equals(channelName))
                                .findAny();

                        // in case there is no match
                        if (channelMatch.isEmpty()) {
                            out.println(linesToRead);
                            out.println(new ErrorResponse("ERROR: CANNOT FIND CHANNEL"));
                            continue;
                        }

                        // check if client is a subscriber to the channel
                        boolean subbed = channelMatch.get().getSubscribers().contains(message.getAuthor());


                        if (!subbed) { // if the client is not subbed to the channel return an error response
                            out.println(linesToRead);
                            out.println(new ErrorResponse("ERROR: ONLY SUBSCRIBERS CAN PUBLISH MESSAGES"));
                            continue;
                        }

                        // add message to channel
                        synchronized (Channel.class) {

                            //add message to channel
                            channelMatch.get().addMessage(message);

                            // append message to file
                            // if the file does not exists, create a new one
                            // if the file already exists, open in append mode

                            PrintWriter out;
                            File f = new File(channelMatch.get().getName() + "_msgs.txt");
                            if (f.exists()) {
                                // open in append mode
                                out = new PrintWriter(new FileWriter(f, true));
                            }
                            else {
                                // open new file
                                out = new PrintWriter(new FileWriter(f));
                            }

                            out.println(message.getAuthor() + "," + message.getBody() + "," + message.getTimestamp());


                            // set the timestamp for that message
                            // The time stamp is simply the index of the message
                            for (Message m : channelMatch.get().getMessages()) {
                                m.setTimestamp(channelMatch.get().getMessages().indexOf(m) + 1);
                            }

                        }

                        // send number of response lines to client
                        out.println(linesToRead); // only 1 line to read (success)

                        // response acknowledging the post request
                        out.println(new SuccessResponse());
                        continue;
                    }

//----------------------------------- Get -------------------------------------------------//

                    // get request Must be logged in
                    if (/*login == null &&*/
                            (req = GetRequest.fromJSON(json)) != null) {

                        // Retrieve the channel identity that the client sent
                        String channelIdentity = ((GetRequest) req).getIdentity();

                        // Get the channel
                        Optional<Channel> channelMatch = channels.parallelStream()
                                .filter(ch -> ch.getName().equals(channelIdentity))
                                .findAny();

                        // in case there is no channel with that name
                        if (channelMatch.isEmpty()) {
                            out.println(linesToRead);
                            out.println(new ErrorResponse("ERROR: CANNOT FIND CHANNEL"));
                            continue;
                        }

                        // check if client is a subscriber to the channel
                        boolean subbed = channelMatch.get().getSubscribers().contains(login);

                        if (!subbed) { // if the client is not subbed to the channel return an error response
                            out.println(linesToRead);
                            out.println(new ErrorResponse("ERROR: ONLY SUBSCRIBERS CAN READ MESSAGES"));
                            continue;
                        }

                        boolean channelIsEmpty = false;
                        boolean allMsgsRead = false;

                        List<Message> msgs = null;
                        synchronized (Channel.class) {

                                int msgsRead = channelMatch.get().getRead(login); // how many messages of this channel have been read

                                if (channelMatch.get().getMessages().size() == 0){
                                    channelIsEmpty = true;
                                }

                                if (msgsRead == channelMatch.get().getMessages().size()) {
                                    allMsgsRead = true;
                                }
                                else { // there are unread messages

                                    // return a sublist of unread messages
                                msgs = channelMatch.get().getMessages().subList(msgsRead, channelMatch.get().getMessages().size());

                                channelMatch.get().setRead(login, channelMatch.get().getMessages().size());
                                }

                        }
                        if (channelIsEmpty || allMsgsRead) {
                            // no messages to read
                            // send over the total lines that need to be read to client
                            out.println(linesToRead);
                            out.println(new ErrorResponse("ERROR: NO MESSAGES TO READ"));
                            continue;
                        }
                        else {
                            // send over the total lines that need to be read to client
                            // add 1 more for the success response at the end
                            linesToRead++;
                            out.println(linesToRead);

                            out.println(new MessageListResponse(msgs));
                            out.println(new SuccessResponse());
                        }
                        continue;
                    }


//----------------------------------- Get Channels -------------------------------------------------//

                    if ((req = ChannelRequest.fromJSON(json)) != null) {

                        //  get the names of each channel
                        List<String> currentChannels = new ArrayList<>();
                        for (Channel ch : channels) {
                            currentChannels.add(ch.getName());
                        }

                        // send the list of channels
                        out.println(linesToRead + 1);

                        // send the list of channels
                        out.println(new ChannelResponse(currentChannels));
                        out.println(new SuccessResponse());
                        continue;
                    }


//----------------------------------- Subscribe -------------------------------------------------//

                    // subscribe request
                    if (/*login == null &&*/
                            (req = SubscribeRequest.fromJson(json)) != null) {
                        // subscribe to the channel if they're not already subscribed

                        String channel = ((SubscribeRequest) req).getChannel(); // the channel the client wants to subscribe to (to)
                        String identity = ((SubscribeRequest) req).getIdentity(); // the client's name (from)

                        // get the channel that the client wants to sub to
                        Optional<Channel> channelMatch = channels.parallelStream()
                                .filter(ch -> ch.getName().equals(channel))
                                .findAny();
//
                        // in case there is no match
                        if (channelMatch.isEmpty()) {
                            out.println(linesToRead);
                            out.println(new ErrorResponse("ERROR: CANNOT FIND CHANNEL"));
                            continue;
                        }
                        else {
                            // check they're already not subbed
                            if (!channelMatch.get().getSubscribers().contains(identity)) { // not subbed
                                // add name to sub list
                                channelMatch.get().addSubscriber(identity);
                                // add the channel name to the messagesRead hashmap
                                channelMatch.get().addClient(identity);
                            }
                            else { // already subbed
                                out.print(linesToRead);
                                out.print(new ErrorResponse("ERROR: ALREADY SUBBED TO THIS CHANNEL"));
                                continue;
                            }
                        }
                        // only a single line to read
                        out.println(linesToRead);
                        out.println(new SuccessResponse());
                        continue;
                    }

//----------------------------------- Unsubscribe -------------------------------------------------//

                    // Unsubscribe request
                    if (/*login == null &&*/
                            (req = UnsubscribeRequest.fromJson(json)) != null) {
                        // subscribe to the channel if they're not already subscribed

                        String channel = ((UnsubscribeRequest) req).getChannel();
                        String identity = ((UnsubscribeRequest) req).getIdentity();

                        // get the channel that the client wants to unsub from
                        Optional<Channel> channelMatch = channels.parallelStream()
                                .filter(ch -> ch.getName().equals(channel))
                                .findAny();

                        // in case there is no match
                        if (channelMatch.isEmpty()) {
                            out.println(linesToRead);
                            out.println(new ErrorResponse("ERROR: CANNOT FIND CHANNEL"));
                            continue;
                        }
                        else {
                            // check they're already not subbed
                            if (channelMatch.get().getSubscribers().contains(identity)) { // not subbed
                                // remove client name from sub list
                                channelMatch.get().getSubscribers().remove(identity);
                                // remove their name from the channel's messagesRead hashmap
                                channelMatch.get().removeClient(identity);
                            }
                            else { // already unsubscribed
                                out.print(linesToRead);
                                out.print(new ErrorResponse("ERROR: YOU'RE NOT SUBSCRIBED TO THIS CHANNEL"));
                                continue;
                            }
                        }

                        // only a single line to read
                        out.println(linesToRead);
                        out.println(new SuccessResponse());
                        continue;
                    }

//----------------------------------- Quit -------------------------------------------------//

                    // quit request? Must be logged in;
                    // send quit response because client needs to receive a response to avoid crashing
                    if (/*login == null &&*/QuitRequest.fromJSON(json) != null) {
                        out.println(linesToRead);
                        out.println(new QuitResponse());
                        in.close();
                        out.close();
                        continue;
                    }

//----------------------------------- Error -------------------------------------------------//

                    // error response acknowledging illegal request
                    // send over number of lines to client
                    out.println(linesToRead);
                    out.println(new ErrorResponse("ERROR: ILLEGAL REQUEST"));
                }
            } catch (IOException e) {
                System.out.println("Exception while connected");
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {

        // Open the messages file and load the channels
        try (BufferedReader reader = new BufferedReader(new FileReader("channels.txt")))
        {
            String line;
            while ((line = reader.readLine()) != null) {
                Channel channel = new Channel(line);
                channels.add(channel);
            }

            // load messages from file
            for (Channel channel : channels) {
                BufferedReader messageReader = new BufferedReader(new FileReader(channel.getName() + "_msgs.txt"));
                String messageLine;
                while ((messageLine = messageReader.readLine()) != null) {
                    // split the line by comma
                    String[] lines = messageLine.split(",");
                    channel.addMessage(new Message(lines[0], lines[1], Integer.parseInt(lines[2])));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        int portNumber = 1234;
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Exception listening for connection on port " +
                    portNumber);
            System.out.println(e.getMessage());
        }
    }
}
