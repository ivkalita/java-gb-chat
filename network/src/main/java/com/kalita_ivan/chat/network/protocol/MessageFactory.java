package com.kalita_ivan.chat.network.protocol;

public class MessageFactory {
    Message createFromRaw(RawMessage rawMessage) {
        Message message = null;
        switch (rawMessage.type) {
            case TEXT_MESSAGE:
                message = new TextMessage(rawMessage);
                break;
            case AUTH:
                message = new AuthMessage(rawMessage);
                break;
            case SYSTEM:
                message = new SystemMessage(rawMessage);
                break;
            default:
                break;
        }

        return message;
    }
}
