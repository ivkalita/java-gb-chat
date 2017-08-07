package com.kalita_ivan.chat.client;

import com.kalita_ivan.chat.network.MessageSocketThread;
import com.kalita_ivan.chat.network.protocol.messages.*;
import com.kalita_ivan.chat.network.protocol.models.User;
import rx.subjects.PublishSubject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

class Messenger {
    PublishSubject<Void> connected;
    PublishSubject<Void> disconnected;
    PublishSubject<Throwable> failed;
    PublishSubject<String> newMessage;
    PublishSubject<ArrayList<User>> userListUpdated;

    private MessageSocketThread socketThread;
    private String login;
    private String password;

    Messenger() {
        this.newMessage = PublishSubject.create();
        this.connected = PublishSubject.create();
        this.disconnected = PublishSubject.create();
        this.failed = PublishSubject.create();
        this.userListUpdated = PublishSubject.create();
    }

    void connect(String ip, String port, String login, String password) {
        this.login = login;
        this.password = password;
        if (this.socketThread != null) {
            return;
        }
        try {
            Socket socket = new Socket(ip, Integer.parseInt(port));
            this.socketThread = new MessageSocketThread("SocketThread", socket);
            this.socketThread.ready.subscribe(this::authenticate);
            this.socketThread.stopped.first().subscribe((none) -> {
                this.socketThread = null;
                this.disconnected.onNext(null);
            });
            this.socketThread.messageReceived.subscribe(this::onMessageReceived);
            this.socketThread.start();
            this.connected.onNext(null);
        } catch (IOException e) {
            this.failed.onNext(e);
        }
    }

    void disconnect() {
        if (this.socketThread == null) {
            return;
        }
        this.socketThread.close();
    }

    private void onMessageReceived(Message message) {
        if (message instanceof TextMessage) {
            this.onTextMessageReceived((TextMessage)message);
        } else if (message instanceof SystemMessage) {
            this.onSystemMessageReceived((SystemMessage)message);
        } else if (message instanceof UserListMessage) {
            this.onUserListMessageReceived((UserListMessage)message);
        }
    }

    private void onTextMessageReceived(TextMessage message) {
        this.newMessage.onNext(String.format("%s: %s", message.getSender().getName(), message.getText()));
    }

    private void onSystemMessageReceived(SystemMessage message) {
        this.newMessage.onNext(String.format("System: %s", message.getText()));
    }

    private void onUserListMessageReceived(UserListMessage message) {
        this.userListUpdated.onNext(message.getUsers());
    }

    void send(String text) {
        this.socketThread.send(new TextMessage(text, null));
    }

    private void authenticate(Socket socket) {
        this.socketThread.send(new AuthMessage(this.login, this.password));
    }
}
