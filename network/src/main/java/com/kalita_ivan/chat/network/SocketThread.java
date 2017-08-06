package com.kalita_ivan.chat.network;

import rx.subjects.PublishSubject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketThread extends Thread {
    public PublishSubject<Void> started;
    public PublishSubject<Void> stopped;
    public PublishSubject<Socket> ready;
    public PublishSubject<Throwable> failed;
    public PublishSubject<Byte> byteReceived;

    private Socket socket;
    private DataOutputStream out;

    public SocketThread(String name, Socket socket) {
        super(name);
        this.socket = socket;
        this.started = PublishSubject.create();
        this.stopped = PublishSubject.create();
        this.ready = PublishSubject.create();
        this.failed = PublishSubject.create();
        this.byteReceived = PublishSubject.create();
    }

    @Override
    public void run() {
        super.run();
        this.started.onNext(null);

        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.ready.onNext(socket);
            byte b;
            while ((b = in.readByte()) != -1 && !isInterrupted()) {
                this.byteReceived.onNext(b);
            }
        } catch (IOException e) {
            this.failed.onNext(e);
        } finally {
            this.close();
        }
    }

    public synchronized void send(byte[] bytes) {
        try {
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            this.failed.onNext(e);
            this.close();
        }
    }

    public synchronized void close() {
        interrupt();
        tryCloseSocket();
        this.stopped.onNext(null);
    }

    private void tryCloseSocket() {
        try {
            this.socket.close();
        } catch (IOException e) {
            this.failed.onNext(e);
        }
    }
}
