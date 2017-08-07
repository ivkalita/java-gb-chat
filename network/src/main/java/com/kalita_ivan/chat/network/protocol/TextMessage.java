package com.kalita_ivan.chat.network.protocol;

public class TextMessage extends Message {
    private String text;
    private User sender;

    public TextMessage(String text, User sender) {
        super();
        this.text = text;
        this.sender = sender;
    }

    public TextMessage(RawMessage rawMessage) {
        this(rawMessage.getData("text"), User.fromHashMap(rawMessage.data.get("sender")));
    }

    public String getText() {
        return this.text;
    }

    public User getSender() {
        return this.sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Override
    public RawMessage wrap() {
        RawMessage wrapped = new RawMessage();
        wrapped.type = RawMessage.Type.TEXT_MESSAGE;
        wrapped.putData("text", this.getText());
        wrapped.putData("sender", this.getSender());
        return wrapped;
    }
}
