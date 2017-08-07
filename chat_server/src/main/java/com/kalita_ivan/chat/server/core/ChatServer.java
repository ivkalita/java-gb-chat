package com.kalita_ivan.chat.server.core;

import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.kalita_ivan.chat.network.MessageSocketThread;
import com.kalita_ivan.chat.network.ServerSocketThread;
import com.kalita_ivan.chat.network.protocol.*;


public class ChatServer {

    private ChatServerListener listener;
    private ServerSocketThread thread;
    private ArrayList<ChatSocketThread> clients;
    private Boolean clientsBlocked = false;
    private AuthProvider authProvider;

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
        this.clients = new ArrayList<>();
        this.authProvider = new MemoryAuthProvider();
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
        ChatSocketThread thread = new ChatSocketThread(threadName, socket);
        thread.started.subscribe((none) -> this.onChatSocketThreadStarted(thread));
        thread.stopped.subscribe((none) -> this.onChatSocketThreadStopped(thread));
        thread.ready.subscribe((none) -> this.onChatSocketThreadReady(thread));
        thread.failed.subscribe((throwable) -> this.onChatSocketThreadFailed(thread, throwable));
        thread.messageReceived.subscribe((message) -> this.onChatSocketThreadMessageReceived(thread, message));
        thread.messageRejected.subscribe((rejected) -> this.onChatSocketThreadMessageRejected(thread, rejected));
        this.clients.add(thread);
        thread.start();
    }

    private void onChatSocketThreadStarted(ChatSocketThread thread) {
        log(String.format("ChatSocketThread (%s) started.", thread.getName()));
    }

    private void onChatSocketThreadStopped(ChatSocketThread thread) {
        if (!this.clientsBlocked) {
            this.clients.remove(thread);
            if (thread.isAuthenticated()) {
                this.broadcast(new SystemMessage(String.format("User %s left the chat.", thread.getUser().getName())));
            }
        }
        log(String.format("ChatSocketThread (%s) stopped.", thread.getName()));
    }

    private void onChatSocketThreadReady(ChatSocketThread thread) {
        log(String.format("ChatSocketThread (%s) ready.", thread.getName()));
    }

    private void onChatSocketThreadFailed(ChatSocketThread thread, Throwable error) {
        log(String.format("ChatSocketThread (%s) exception: %s.", thread.getName(), error.getMessage()));
    }

    private void onChatSocketThreadMessageReceived(ChatSocketThread thread, Message message) {
        if (message instanceof TextMessage) {
            this.onChatSocketThreadTextMessageReceived(thread, (TextMessage)message);
        } else if (message instanceof AuthMessage) {
            this.onChatSocketThreadAuthMessageReceived(thread, (AuthMessage)message);
        }
    }

    private void onChatSocketThreadTextMessageReceived(ChatSocketThread thread, TextMessage message) {
        log(String.format(
            "ChatSocketThread (%s) sent message: %s.",
            thread.getName(), message.getText()
        ));
        if (!thread.isAuthenticated()) {
            log(String.format("ChatSocketThread (%s) is not authenticated.", thread.getName()));
            thread.send(new SystemMessage("You are not authenticated."));
            return;
        }
        message.setSender(thread.getUser());
        this.broadcast(message);
    }

    private void onChatSocketThreadAuthMessageReceived(ChatSocketThread thread, AuthMessage message) {
        log(String.format(
            "ChatSocketThread (%s) auth message: (%s, %s).",
            thread.getName(), message.getLogin(), message.getPassword()
        ));
        User user = this.authProvider.authenticate(message.getLogin(), message.getPassword());
        if (user == null) {
            log(String.format("ChatSocketThread (%s) auth failed.", thread.getName()));
            thread.send(new SystemMessage("Invalid credentials"));
            return;
        }
        thread.authenticate(user);
        log(String.format("ChatSocketThread (%s) authenticated as %s", thread.getName(), user.getName()));
        this.broadcast(new SystemMessage(String.format("%s joined the chat.", user.getName())));
    }

    private void onChatSocketThreadMessageRejected(ChatSocketThread thread, byte[] rejected) {
        try {
            log(String.format(
                "ChatSocketThread (%s) message rejected: %s.",
                thread.getName(), new String(rejected, "UTF-8")
            ));
        } catch (UnsupportedEncodingException e) {
            log(String.format(
                "ChatSocketThread (%s) unable to format message – unsupported encoding.",
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
        for (ChatSocketThread client: this.clients) {
            if (!client.isAuthenticated()) {
                continue;
            }
            client.send(message);
        }
    }
}
