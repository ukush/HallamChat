package com.nsd.hallamchat;

import org.json.simple.JSONObject;

public class OpenRequest extends Request{

    // use the class name as the tag
    private static final String _class =
            OpenRequest.class.getSimpleName();

    private String identity;

    // constructor
    public OpenRequest(String identity) {

        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    // serialize to JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("identity", identity);
        return obj;
    }

    // deserialize
    public static OpenRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize
            String identity = (String)obj.get("identity");
            // construct the object to return
            return new OpenRequest(identity);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
