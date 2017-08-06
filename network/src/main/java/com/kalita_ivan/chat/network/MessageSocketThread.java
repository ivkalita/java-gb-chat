package com.kalita_ivan.chat.network;

import rx.subjects.PublishSubject;

import java.io.UnsupportedEncodingException;
import java.net.Socket;

import com.kalita_ivan.chat.network.protocol.JSONStreamParser;
import com.kalita_ivan.chat.network.protocol.Message;


public class MessageSocketThread extends SocketThread {

    private JSONStreamParser streamParser;
    public PublishSubject<Message> messageReceived;
    public PublishSubject<byte[]> messageRejected;

    public MessageSocketThread(String name, Socket socket) {
        super(name, socket);
        this.messageReceived = PublishSubject.create();
        this.messageRejected = PublishSubject.create();
        this.streamParser = new JSONStreamParser(this.byteReceived);
        this.streamParser.messageReceived.subscribe(this.messageReceived::onNext);
        this.streamParser.messageRejected.subscribe(this.messageRejected::onNext);
    }

    public void send(Message message) {
        String serialized = this.streamParser.serialize(message);
        try {
            this.send(serialized.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            this.failed.onNext(e);
        }
    }
}
