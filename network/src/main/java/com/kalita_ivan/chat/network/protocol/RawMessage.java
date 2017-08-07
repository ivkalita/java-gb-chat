package com.kalita_ivan.chat.network.protocol;

import java.util.HashMap;

public class RawMessage {
    public enum Type {
        TEXT_MESSAGE, AUTH, SYSTEM, USER_LIST
    }

    public Type type;
    public HashMap<String, Object> data;

    public RawMessage() {
        this.data = new HashMap<>();
    }

    public String getData(String key) {
        return String.class.cast(this.data.get(key));
    }

    public void putData(String key, Object value) {
        this.data.put(key, value);
    }
}
