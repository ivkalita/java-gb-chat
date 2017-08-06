package com.kalita_ivan.chat.network.protocol;

import com.owlike.genson.Genson;
import rx.Observable;
import rx.subjects.PublishSubject;

public class JSONStreamParser {
    private static final int MAX_MESSAGE_SIZE = 1500;
    private byte[] buffer;
    private int current;
    private int closedBracesExpectedCount;
    public PublishSubject<Message> messageReceived;
    public PublishSubject<byte[]> messageRejected;
    private Genson genson;

    public JSONStreamParser(Observable<Byte> onByteReceived) {
        this.messageReceived = PublishSubject.create();
        this.messageRejected = PublishSubject.create();
        onByteReceived.subscribe(this::processByte);
        this.genson = new Genson();
        buffer = new byte[MAX_MESSAGE_SIZE];
        this.reset();
    }

    private void processByte(byte b) {
        if (current == MAX_MESSAGE_SIZE) {
            this.messageRejected.onNext(null);
            this.reset();
            return;
        }
        if (b == '{') {
            this.closedBracesExpectedCount += 1;
        } else if (b == '}') {
            this.closedBracesExpectedCount -= 1;
        }
        this.buffer[this.current++] = b;
        if (this.closedBracesExpectedCount > 0) {
            return;
        }
        if (this.closedBracesExpectedCount < 0 || b != '}') {
            this.messageRejected.onNext(this.buffer);
            this.reset();
            return;
        }

        try {
            this.deserialize();
        } catch (Throwable e) {
            this.messageRejected.onNext(this.buffer);
        } finally {
            this.reset();
        }
    }

    private void reset() {
        for (int i = 0; i < MAX_MESSAGE_SIZE; i++) {
            this.buffer[i] = 0;
        }
        this.current = 0;
        this.closedBracesExpectedCount = 0;
    }

    private void deserialize() {
        Message message = genson.deserialize(this.buffer, Message.class);
        this.messageReceived.onNext(message);
    }

    public String serialize(Message message) {
        return this.genson.serialize(message);
    }
}
