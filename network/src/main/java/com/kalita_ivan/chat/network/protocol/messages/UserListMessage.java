package com.kalita_ivan.chat.network.protocol.messages;

import java.util.ArrayList;

import com.kalita_ivan.chat.network.protocol.RawMessage;
import com.kalita_ivan.chat.network.protocol.models.User;

public class UserListMessage extends Message {
    private ArrayList<User> users;

    public UserListMessage(ArrayList<User> users) {
        this.users = users;
    }

    public UserListMessage(RawMessage rawMessage) {
        this(User.fromArrayList(rawMessage.data.get("users")));
    }

    @Override
    public RawMessage wrap() {
        RawMessage wrapped = new RawMessage();
        wrapped.type = RawMessage.Type.USER_LIST;
        wrapped.putData("users", this.users);
        return wrapped;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
