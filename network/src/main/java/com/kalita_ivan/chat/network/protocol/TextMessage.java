package com.kalita_ivan.chat.network.protocol;

public class TextMessage extends Message {
    public TextMessage(String text) {
        super();
        this.type = Type.TEXT_MESSAGE;
        this.data.put("text", text);
    }
}
