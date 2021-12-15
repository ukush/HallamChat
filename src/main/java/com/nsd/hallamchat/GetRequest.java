package com.nsd.hallamchat;

import org.json.simple.JSONObject;

public class GetRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            GetRequest.class.getSimpleName();

    private String identity;
    private int after;

    // Constructor; no arguments as there are no instance fields
    public GetRequest(String identity) {
        this.identity = identity;
        this.after = 0;
    }


    // accessor methods


    public String getIdentity() {
        return identity;
    }

    public int getAfter() {
        return after;
    }

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("identity", identity);
        obj.put("after", after);
        return obj;
    }

    // Tries to deserialize a ReadRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static GetRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;

            // deserialize the identity
            String identity = (String)obj.get("identity");
            //int after = (int)obj.get("after");

            // construct the new object to return
            return new GetRequest(identity);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
