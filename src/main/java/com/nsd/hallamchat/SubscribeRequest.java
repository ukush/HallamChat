package com.nsd.hallamchat;

import org.json.simple.JSONObject;

public class SubscribeRequest extends Request{
    // use class name as JSON tag
    private static final String _class
            = SubscribeRequest.class.getSimpleName();

    // identity name (from)
    private String identity;

    // channel name to subscribe to
    private String channel;

    // constructor
    public SubscribeRequest(String identity, String channel) {
        // check for null
        if (channel == null)
            throw new NullPointerException();
        this.identity = identity;
        this.channel = channel;
    }


    public String getIdentity() {
        return identity;
    }

    public String getChannel() {
        return channel;
    }

    // serialise into JSON object
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("identity", identity);
        obj.put("channel", channel);
        return obj;
    }



    // deserialize
    public static SubscribeRequest fromJson(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            if (!_class.equals(obj.get("_class")))
                return null;

            // deserialize the identity
            String identity = (String)obj.get("identity");

            // deserialize the channel
            String channel = (String)obj.get("channel");

            // construct object
            return new SubscribeRequest(identity, channel);
        }catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }


}
