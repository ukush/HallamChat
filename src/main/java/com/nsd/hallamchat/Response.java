package com.nsd.hallamchat;

public abstract class Response {
    // Serializes this object into a JSONObject
    public abstract Object toJSON();

    // Serializes this object and returns the JSON as a string
    public String toJSONString() { return toJSON().toString(); }

    // Prints this object in JSON representation
    public String toString() { return toJSONString(); }
}
