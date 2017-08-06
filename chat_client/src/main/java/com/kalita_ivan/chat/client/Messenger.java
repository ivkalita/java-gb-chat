package com.kalita_ivan.chat.client;

import com.kalita_ivan.chat.network.MessageSocketThread;
import com.kalita_ivan.chat.network.protocol.Message;
import com.kalita_ivan.chat.network.protocol.TextMessage;
import rx.subjects.PublishSubject;

import java.io.IOException;
import java.net.Socket;

class Messenger {
    PublishSubject<Void> connected;
    PublishSubject<Void> disconnected;
    PublishSubject<Throwable> failed;
    PublishSubject<String> newMessage;

    private static int clientIdGenerator = 0;
    private MessageSocketThread socketThread;

    Messenger() {
        this.newMessage = PublishSubject.create();
        this.connected = PublishSubject.create();
        this.disconnected = PublishSubject.create();
        this.failed = PublishSubject.create();
    }

    void connect(String ip, String port, String login, String password) {
        if (this.socketThread != null) {
            return;
        }
        try {
            Socket socket = new Socket(ip, Integer.parseInt(port));
            this.socketThread = new MessageSocketThread("SocketThread", socket);
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
        switch(message.type) {
            case TEXT_MESSAGE:
                this.newMessage.onNext(String.class.cast(message.data.get("text")));
                break;
        }
    }

    void send(String text) {
        this.socketThread.send(new TextMessage(text));
    }
}
