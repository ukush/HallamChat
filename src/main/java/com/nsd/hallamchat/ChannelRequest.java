package com.nsd.hallamchat;

import org.json.simple.JSONObject;


public class ChannelRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            ChannelRequest.class.getSimpleName();

    private String identity;

    // Constructor; no arguments as there are no instance fields
    public ChannelRequest(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }


    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("identity", identity);
        return obj;
    }

    // Tries to deserialize a channel response instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static ChannelRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;

            // deserialize the identity
            String identity = (String)obj.get("identity");

            // construct the new object to return
            return new ChannelRequest(identity);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
