package com.kalita_ivan.chat.network;

import rx.subjects.PublishSubject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {

    public PublishSubject<Void> started;
    public PublishSubject<Void> stopped;
    public PublishSubject<ServerSocket> ready;
    public PublishSubject<ServerSocket> acceptTimeout;
    public PublishSubject<Socket> accepted;
    public PublishSubject<Throwable> failed;

    private int port;
    private int timeout;

    public ServerSocketThread(String name, int port, int timeout) {
        super(name);
        this.port = port;
        this.timeout = timeout;
        this.started = PublishSubject.create();
        this.stopped = PublishSubject.create();
        this.ready = PublishSubject.create();
        this.acceptTimeout = PublishSubject.create();
        this.accepted = PublishSubject.create();
        this.failed = PublishSubject.create();
    }

    @Override
    public void run() {
        this.started.onNext(null);

        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            serverSocket.setSoTimeout(this.timeout);
            this.ready.onNext(serverSocket);
            this.loop(serverSocket);
        } catch (IOException e) {
            this.failed.onNext(e);
        } finally {
            this.stopped.onNext(null);
        }
    }

    private void loop(ServerSocket serverSocket) throws IOException {
        while (!isInterrupted()) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (SocketTimeoutException e) {
                this.acceptTimeout.onNext(serverSocket);
                continue;
            }
            this.accepted.onNext(socket);
        }
    }
}
