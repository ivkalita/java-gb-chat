package com.kalita_ivan.chat.server.core;

public class ChatServer {

    private ChatServerListener listener;

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
    }

    public void startListening(int port) {
        log("Server started");
    }

    public void dropAllClients() {
        log("Drop all clients");
    }

    public void stopListening() {
        log("Server stopped");
    }

    private void log(String msg) {
        listener.onLog(msg);
    }
}
