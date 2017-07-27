package main.java.com.kalita_ivan.chat.server.gui;

import main.java.com.kalita_ivan.chat.library.DefaultGUIWindow;
import main.java.com.kalita_ivan.chat.library.TextAreaTimeLogger;
import main.java.com.kalita_ivan.chat.server.core.ChatServer;
import main.java.com.kalita_ivan.chat.server.core.ChatServerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChatServerGUI extends DefaultGUIWindow implements ChatServerListener {

    private static final String START_LISTENING = "Start listening";
    private static final String DROP_ALL_CLIENTS = "Drop all clients";
    private static final String STOP_LISTENING = "Stop listening";

    private ChatServer chatServer;
    private JButton btnStartListening;
    private JButton btnStopListening;
    private JButton btnDropAllClients;
    private JTextArea textAreaLog;
    private TextAreaTimeLogger textAreaTimeLogger;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatServerGUI::new);
    }

    private ChatServerGUI() {
        super();
        initChatServer();
        initLogger();
    }

    protected String getDefaultTitle() {
        return "Chat server";
    }

    protected void initGUI() {
        super.initGUI();
        btnStartListening = new JButton(START_LISTENING);
        btnStopListening = new JButton(STOP_LISTENING);
        btnDropAllClients = new JButton(DROP_ALL_CLIENTS);
        textAreaLog = new JTextArea();

        JPanel upperPanel = new JPanel(new GridLayout(1, 3));
        upperPanel.add(btnStartListening);
        upperPanel.add(btnStopListening);
        upperPanel.add(btnDropAllClients);
        add(upperPanel, BorderLayout.NORTH);

        JScrollPane scrollLog = new JScrollPane(textAreaLog);
        textAreaLog.setEditable(false);
        add(scrollLog, BorderLayout.CENTER);
    }

    protected void initListeners() {
        super.initListeners();
        btnStartListening.addActionListener(this::onStartListeningClick);
        btnDropAllClients.addActionListener(this::onDropAllClientsClick);
        btnStopListening.addActionListener(this::onStopListeningClick);
    }

    private void initChatServer() {
        chatServer = new ChatServer(this);
    }

    private void initLogger() {
        textAreaTimeLogger = new TextAreaTimeLogger(textAreaLog, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    private void onStartListeningClick(ActionEvent e) {
        chatServer.startListening(8189);
    }

    private void onStopListeningClick(ActionEvent e) {
        chatServer.stopListening();
    }

    private void onDropAllClientsClick(ActionEvent e) {
        chatServer.dropAllClients();
    }

    @Override
    public void onLog(String msg) {
        textAreaTimeLogger.onLog(msg);
    }
}
