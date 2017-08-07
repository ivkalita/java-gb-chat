package com.kalita_ivan.chat.server.core;

import com.kalita_ivan.chat.network.protocol.User;

import java.util.ArrayList;

public class MemoryAuthProvider implements AuthProvider {
    private ArrayList<User> users;

    MemoryAuthProvider() {
        this.users = new ArrayList<>();
        this.users.add(new User(1L, "qwerty", "qwerty", "Test User"));
        this.users.add(new User(2L, "qwerty1", "qwerty", "Xest User"));
    }

    public User authenticate(String login, String password) {
        for (User user: this.users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return user;
            }
        }

        return null;
    }
}
