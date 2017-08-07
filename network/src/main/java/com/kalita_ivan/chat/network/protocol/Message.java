package com.kalita_ivan.chat.network.protocol;

abstract public class Message {
    abstract public RawMessage wrap();

    public Message(RawMessage rawMessage) {
    }

    public Message() {
    }
}
