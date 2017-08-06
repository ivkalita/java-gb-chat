package com.kalita_ivan.chat.server.core;

import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import com.kalita_ivan.chat.network.MessageSocketThread;
import com.kalita_ivan.chat.network.ServerSocketThread;
import com.kalita_ivan.chat.network.protocol.Message;


public class ChatServer {

    private ChatServerListener listener;
    private ServerSocketThread thread;
    private ArrayList<MessageSocketThread> clients;
    private Boolean clientsBlocked = false;

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
        this.clients = new ArrayList<>();
    }

    public void startListening(int port) {
        log("ChatServer: started.");
        if (this.thread == null) {
            this.initServerSocketThread(port);
            this.thread.start();
        }
    }

    private void initServerSocketThread(int port) {
        this.thread = new ServerSocketThread("Server socket thread", port, 2000);
        this.thread.started.subscribe(this::onServerSocketThreadStarted);
        this.thread.ready.subscribe(this::onServerSocketThreadReady);
        this.thread.stopped.subscribe(this::onServerSocketThreadStopped);
        this.thread.accepted.subscribe(this::onServerSocketThreadAccepted);
    }

    private void onServerSocketThreadStarted(Void v) {
        log("ServerSocketThread: started.");
    }

    private void onServerSocketThreadReady(ServerSocket socket) {
        log("ServerSocketThread: ready.");
    }

    private void onServerSocketThreadStopped(Void v) {
        log("ServerSocketThread: stopped.");
    }

    private void onServerSocketThreadAccepted(Socket socket) {
        String threadName = String.format("%s:%d", socket.getInetAddress(), socket.getPort());
        log(String.format("ServerSocketThread: accepted %s", threadName));
        MessageSocketThread thread = new MessageSocketThread(threadName, socket);
        thread.started.subscribe((none) -> this.onMessageSocketThreadStarted(thread));
        thread.stopped.subscribe((none) -> this.onMessageSocketThreadStopped(thread));
        thread.ready.subscribe((none) -> this.onMessageSocketThreadReady(thread));
        thread.failed.subscribe((throwable) -> this.onMessageSocketThreadFailed(thread, throwable));
        thread.messageReceived.subscribe((message) -> this.onMessageSocketThreadMessageReceived(thread, message));
        thread.messageRejected.subscribe((rejected) -> this.onMessageSocketThreadMessageRejected(thread, rejected));
        this.clients.add(thread);
        thread.start();
    }

    private void onMessageSocketThreadStarted(MessageSocketThread thread) {
        log(String.format("MessageSocketThread (%s): started.", thread.getName()));
    }

    private void onMessageSocketThreadStopped(MessageSocketThread thread) {
        if (!this.clientsBlocked) {
            this.clients.remove(thread);
        }
        log(String.format("MessageSocketThread (%s): stopped.", thread.getName()));
    }

    private void onMessageSocketThreadReady(MessageSocketThread thread) {
        log(String.format("MessageSocketThread (%s): ready.", thread.getName()));
    }

    private void onMessageSocketThreadFailed(MessageSocketThread thread, Throwable error) {
        log(String.format("MessageSocketThread (%s): exception: %s.", thread.getName(), error.getMessage()));
    }

    private void onMessageSocketThreadMessageReceived(MessageSocketThread thread, Message message) {
        switch (message.type) {
            case TEXT_MESSAGE:
                log(String.format(
                    "MessageSocketThread (%s): sent message: %s.",
                    thread.getName(), message.data.get("text")
                ));
                this.broadcast(message);
                break;
            default:
        }
    }

    private void onMessageSocketThreadMessageRejected(MessageSocketThread thread, byte[] rejected) {
        try {
            log(String.format(
                "MessageSocketThread (%s): message rejected: %s.",
                thread.getName(), new String(rejected, "UTF-8")
            ));
        } catch (UnsupportedEncodingException e) {
            log(String.format(
                "MessageSocketThread (%s): unable to format message – unsupported encoding.",
                thread.getName()
            ));
        }
    }

    public void dropAllClients() {
        this.clientsBlocked = true;
        for(MessageSocketThread client: this.clients) {
            client.close();
        }
        this.clients.clear();
        this.clientsBlocked = false;
        log("ChatServer: all clients dropped.");
    }

    public void stopListening() {
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
        this.dropAllClients();
        log("ChatServer: stopped.");
    }

    private void log(String msg) {
        listener.onLog(msg);
    }

    private void broadcast(Message message) {
        for (MessageSocketThread client: this.clients) {
            client.send(message);
        }
    }
}
