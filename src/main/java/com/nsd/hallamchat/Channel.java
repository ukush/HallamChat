package com.nsd.hallamchat;

import java.util.ArrayList;
import java.util.HashMap;

public class Channel {
    private String name;

    // list of messages from clients
    private ArrayList<Message> messages = new ArrayList<>();

    // list of subscribed users (name only)
    private ArrayList<String> subscribers = new ArrayList<>();

    private HashMap<String, Integer> messagesRead = new HashMap<>();

    // constructor
    public Channel(String name) {

        this.name = name;
        subscribers.add(name); // add user to subscriber list
    }

    public synchronized void addMessage(Message message) {
        messages.add(message);
    }

    public synchronized void addSubscriber(String name) {
        subscribers.add(name);
    }

    public synchronized void addClient(String clientName) {
        messagesRead.put(clientName, 0);
    }

    public synchronized int getRead(String clientName) {
        return messagesRead.get(clientName); // returns how many messages have been read by that client
    }

    public synchronized void setRead(String clientName, int newRead) {
        messagesRead.replace(clientName, newRead);
    }

    public synchronized void removeClient(String clientName) {
        messagesRead.remove(clientName);
    }

    public synchronized String getName(){
        return name;
    }

    public ArrayList<String> getSubscribers() {
        return subscribers;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
