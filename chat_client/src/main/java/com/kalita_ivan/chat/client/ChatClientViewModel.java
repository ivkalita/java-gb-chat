package com.kalita_ivan.chat.client;

import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

class ChatClientViewModel {
    static private final String DEFAULT_IP = "127.0.0.1";
    static private final String DEFAULT_PORT = "8000";
    static private final String DEFAULT_LOGIN = "login";
    static private final String DEFAULT_PASSWORD = "password";

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

    private boolean connected;
    private Messenger messenger;
    private DateFormat dateFormat;

    ChatClientViewModel(Messenger messenger) {
        this.dateFormat = new SimpleDateFormat("HH:mm:ss");
        this.messenger = messenger;
        this.connected = false;

        buttonLogin.subscribe(actionEvent -> this.messenger.connect(
            this.ip.getValue(),
            this.port.getValue(),
            this.login.getValue(),
            this.password.getValue()
        ));

        buttonDisconnect.subscribe(actionEvent -> this.messenger.disconnect());

        buttonSend.subscribe(actionEvent -> {
            if (!this.connected) {
                return;
            }
            String messageContent = this.message.getValue().trim();
            if (messageContent.length() == 0) {
                return;
            }

            this.messenger.send(messageContent);
            this.message.onNext("");
        });

        this.messenger.connected.subscribe((none) -> {
            this.connected = true;
            this.panelTopVisible.onNext(false);
            this.panelBottomVisible.onNext(true);
            log("ChatClient: connected.");
        });

        this.messenger.disconnected.subscribe((none) -> {
            this.connected = false;
            this.panelTopVisible.onNext(true);
            this.panelBottomVisible.onNext(false);
            log("ChatClient: disconnected.");
        });

        this.messenger.newMessage.subscribe(this::log);

        this.messenger.failed.subscribe((e) -> log(String.format("Messenger: exception: %s", e.getMessage())));
    }

    void init() {
        this.ip.onNext(DEFAULT_IP);
        this.port.onNext(DEFAULT_PORT);
        this.login.onNext(DEFAULT_LOGIN);
        this.password.onNext(DEFAULT_PASSWORD);
    }

    private void log(String message) {
        this.messages.onNext(String.format("%s: %s\n", dateFormat.format(new Date()), message));
    }
}
