package com.nsd.hallamchat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChannelResponse extends Response {

    // class name to be used as tag in JSON representation
    private static final String _class =
            ChannelResponse.class.getSimpleName();

    private List<String> channels;

    // Constructor; throws NullPointerException if messages contains nulls.
    public ChannelResponse(List<String> channels) {
        // check for nulls
        if (channels == null || channels.contains(null))
            throw new NullPointerException();
        this.channels = channels;
    }

    public List<String> getChannels() { return channels; }

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        // serialize channels into a JSONArray
        JSONArray arr = new JSONArray();
        arr.addAll(channels);
        // serialize this as a JSONObject
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("channels", arr);
        return obj;
    }

    // Tries to deserialize a Channel list response instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static ChannelResponse fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize messages from JSONArray
            JSONArray arr = (JSONArray)obj.get("channels");
            List<String> channels = new ArrayList<>();

            for (Object ch_str : arr)
                channels.add((String)ch_str);
            // construct the object to return (checking for nulls)
            return new ChannelResponse(channels);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
