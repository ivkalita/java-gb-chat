package com.kalita_ivan.chat.network.protocol;

public class SystemMessage extends Message {
    private String text;

    public SystemMessage(String text) {
        super();
        this.text = text;
    }

    public SystemMessage(RawMessage rawMessage) {
        this(rawMessage.getData("text"));
    }

    public String getText() {
        return this.text;
    }

    @Override
    public RawMessage wrap() {
        RawMessage wrapped = new RawMessage();
        wrapped.type = RawMessage.Type.SYSTEM;
        wrapped.putData("text", this.text);
        return wrapped;
    }
}
