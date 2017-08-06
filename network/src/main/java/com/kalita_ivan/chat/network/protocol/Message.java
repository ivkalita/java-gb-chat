package com.kalita_ivan.chat.network.protocol;

import java.util.HashMap;

public class Message {
    public enum Type {
        TEXT_MESSAGE(1);

        private final int id;

        Type(int id) {
            this.id = id;
        }

        public int getValue() {
            return this.id;
        }
    }

    public Type type;
    public HashMap<String, Object> data;

    public Message() {
        this.data = new HashMap<>();
    }
}
