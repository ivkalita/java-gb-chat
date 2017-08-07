package com.kalita_ivan.chat.server.core;

import java.net.Socket;

import com.kalita_ivan.chat.network.MessageSocketThread;
import com.kalita_ivan.chat.network.protocol.models.User;

class ChatSocketThread extends MessageSocketThread {
    private User user;

    ChatSocketThread(String name, Socket socket) {
        super(name, socket);
    }

    boolean isAuthenticated() {
        return this.user != null;
    }

    User getUser() {
        return this.user;
    }

    void authenticate(User user) {
        this.user = user;
    }
}
