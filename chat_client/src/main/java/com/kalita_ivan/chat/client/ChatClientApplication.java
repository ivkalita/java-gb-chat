package com.kalita_ivan.chat.client;

import com.kalita_ivan.chat.library.DefaultGUIExceptionHandler;

import javax.swing.*;

public class ChatClientApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Thread.setDefaultUncaughtExceptionHandler(new DefaultGUIExceptionHandler());
            Messenger messenger = new Messenger();
            ChatClientViewModel chatClientViewModel = new ChatClientViewModel(messenger);
            ChatClientView chatClientView = new ChatClientView();
            chatClientView.bind(chatClientViewModel);
            chatClientView.setVisible(true);
        });
    }
}
