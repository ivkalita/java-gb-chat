package com.kalita_ivan.chat.client;

import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class ChatClientViewModel {
    BehaviorSubject<String> ip = BehaviorSubject.create();
    BehaviorSubject<String> port = BehaviorSubject.create();
    BehaviorSubject<String> login = BehaviorSubject.create();
    BehaviorSubject<String> password = BehaviorSubject.create();
    BehaviorSubject<String> message = BehaviorSubject.create();
    BehaviorSubject<Boolean> alwaysOnTop = BehaviorSubject.create(false);
    BehaviorSubject<Boolean> panelTopVisible = BehaviorSubject.create(true);
    BehaviorSubject<Boolean> panelBottomVisible = BehaviorSubject.create(false);
    BehaviorSubject<ActionEvent> buttonLogin = BehaviorSubject.create();
    BehaviorSubject<ActionEvent> buttonDisconnect = BehaviorSubject.create();
    BehaviorSubject<ActionEvent> buttonSend = BehaviorSubject.create();
    PublishSubject<String> messages = PublishSubject.create();
    BehaviorSubject<Boolean> connected = BehaviorSubject.create(false);

    private Messenger messenger;
    private DateFormat dateFormat;

    ChatClientViewModel(Messenger messenger) {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        this.messenger = messenger;

        this.messenger
            .messageDelivered
            .subscribe(message -> log(String.format("Message \"%s\" delivered.", message.getContent())));

        buttonLogin.subscribe(actionEvent -> {
            connected.onNext(true);
            panelTopVisible.onNext(false);
            panelBottomVisible.onNext(true);
            log("User logged in.");
        });

        buttonDisconnect.subscribe(actionEvent -> {
            connected.onNext(false);
            panelTopVisible.onNext(true);
            panelBottomVisible.onNext(false);
            log("User logged out.");
        });

        buttonSend.subscribe(actionEvent -> {
            if (!connected.getValue()) {
                return;
            }
            String messageContent = this.message.getValue().trim();
            if (messageContent.length() == 0) {
                return;
            }

            Message message = messenger.send(this.message.getValue());
            this.message.onNext("");
            log(String.format("Message \"%s\" sent.", message.getContent()));
        });
    }

    private void log(String message) {
        messages.onNext(String.format("%s: %s\n", dateFormat.format(new Date()), message));
    }
}
