package com.kalita_ivan.chat.client;

import java.util.Date;

class Message {
    private String content;
    private Date createdAt;
    private Date sentAt;
    private Date acceptedAt;
    private int serverId;
    private int clientId;

    Message(String content, Date createdAt, Date sentAt, Date acceptedAt, int serverId, int clientId) {
        this.content = content;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.acceptedAt = acceptedAt;
        this.serverId = serverId;
        this.clientId = clientId;
    }

    String getContent() {
        return content;
    }
}
