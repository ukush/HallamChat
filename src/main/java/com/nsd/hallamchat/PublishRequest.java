package com.nsd.hallamchat;

import org.json.simple.JSONObject;

public class PublishRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            PublishRequest.class.getSimpleName();

    private String identity;

    private Message message;


    // Constructor; throws NullPointerException if message is null.
    public PublishRequest(String identity, String from, String message) {
        // check for null
        if (message == null || identity == null)
            throw new NullPointerException();
        this.identity = identity;
        this.message = new Message(message, from, 0);
    }

    public String getIdentity() { return identity; }
    public Message getMessage() { return message; }


    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        // serialize the message
        JSONObject jMessage = (JSONObject) message.toJSON();

        // serialize the publishRequest
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("identity", identity);
        obj.put("message", jMessage);
        return obj;
    }

    // Tries to deserialize a PostRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static PublishRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize posted message
            String channel = (String)obj.get("identity");


            JSONObject jMessage = (JSONObject) obj.get("message");

            String from = (String)jMessage.get("author");
            String body = (String)jMessage.get("body");

            // construct the object to return (checking for nulls)
            return new PublishRequest(channel, from, body);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
