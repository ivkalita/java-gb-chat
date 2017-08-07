package com.kalita_ivan.chat.network.protocol.messages;

import com.kalita_ivan.chat.network.protocol.RawMessage;

abstract public class Message {
    abstract public RawMessage wrap();

    public Message(RawMessage rawMessage) {
    }

    public Message() {
    }
}
