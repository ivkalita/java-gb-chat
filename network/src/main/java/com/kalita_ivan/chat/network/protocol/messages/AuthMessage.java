package com.kalita_ivan.chat.network.protocol.messages;


import com.kalita_ivan.chat.network.protocol.RawMessage;

public class AuthMessage extends Message {
    private String login;
    private String password;

    public AuthMessage(String login, String password) {
        super();
        this.login = login;
        this.password = password;
    }

    public AuthMessage(RawMessage rawMessage) {
        this(rawMessage.getData("login"), rawMessage.getData("password"));
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public RawMessage wrap() {
        RawMessage wrapped = new RawMessage();
        wrapped.type = RawMessage.Type.AUTH;
        wrapped.putData("login", this.getLogin());
        wrapped.putData("password", this.getPassword());
        return wrapped;
    }
}
