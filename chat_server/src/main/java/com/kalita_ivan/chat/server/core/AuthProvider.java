package com.kalita_ivan.chat.server.core;

import com.kalita_ivan.chat.network.protocol.models.User;

public interface AuthProvider {
    User authenticate(String login, String password);
}
