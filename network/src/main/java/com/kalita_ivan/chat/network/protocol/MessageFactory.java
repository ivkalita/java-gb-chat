package com.kalita_ivan.chat.network.protocol;

import com.kalita_ivan.chat.network.protocol.messages.*;

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
            case USER_LIST:
                message = new UserListMessage(rawMessage);
                break;
            default:
                break;
        }

        return message;
    }
}
