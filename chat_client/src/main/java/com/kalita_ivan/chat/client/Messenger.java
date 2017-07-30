package com.kalita_ivan.chat.client;

import rx.subjects.PublishSubject;

import javax.swing.*;
import java.util.Date;

class Messenger {
    PublishSubject<Message> messageDelivered = PublishSubject.create();

    private static int clientIdGenerator = 0;

    Message send(String content) {
        Message message = new Message(
            content,
            new Date(),
            new Date(),
            null,
            -1,
            Messenger.clientIdGenerator++
        );

        // Just a mock for asynchronous message delivery, actually,
        // messageDelivered won't be called inside Messenger::send.
        SwingUtilities.invokeLater(() -> messageDelivered.onNext(message));

        return message;
    }
}
