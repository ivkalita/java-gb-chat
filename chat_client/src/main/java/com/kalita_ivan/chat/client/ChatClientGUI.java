package com.kalita_ivan.chat.client;

import io.reactivex.Flowable;
import com.kalita_ivan.chat.library.DefaultGUIWindow;
import com.kalita_ivan.chat.library.TextAreaTimeLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChatClientGUI extends DefaultGUIWindow {
    private JPanel upperPanel;
    private JTextField fieldIPAddr;
    private JTextField fieldPort;
    private JCheckBox chkAlwaysOnTop;
    private JTextField fieldLogin;
    private JPasswordField fieldPass;
    private JButton btnLogin;
    private JTextArea textAreaLog;
    private JList<String> userList;
    private JPanel bottomPanel;
    private JButton btnDisconnect;
    private JTextField fieldInput;
    private JButton btnSend;

    private TextAreaTimeLogger textAreaTimeLogger;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClientGUI::new);
    }

    private ChatClientGUI() {
        super();
        initLogger();
    }

    @Override
    protected Dimension getDefaultDimension() {
        return new Dimension(800, 800);
    }

    @Override
    protected String getDefaultTitle() {
        return "Chat client";
    }

    @Override
    protected void initGUI() {
        super.initGUI();
        upperPanel = new JPanel(new GridLayout(2, 3));
        fieldIPAddr = new JTextField("89.222.249.131");
        fieldPort = new JTextField("8189");
        chkAlwaysOnTop = new JCheckBox("Always on top");
        fieldLogin = new JTextField("login_1");
        fieldPass = new JPasswordField("pass_1");
        btnLogin = new JButton("Login");

        textAreaLog = new JTextArea();
        userList = new JList<>();

        bottomPanel = new JPanel(new BorderLayout());
        btnDisconnect = new JButton("Disconnect");
        fieldInput = new JTextField();
        btnSend = new JButton("Send");

        upperPanel.add(fieldIPAddr);
        upperPanel.add(fieldPort);
        upperPanel.add(chkAlwaysOnTop);
        upperPanel.add(fieldLogin);
        upperPanel.add(fieldPass);
        upperPanel.add(btnLogin);
        add(upperPanel, BorderLayout.NORTH);

        JScrollPane scrollLog = new JScrollPane(textAreaLog);
        textAreaLog.setEditable(false);
        add(scrollLog, BorderLayout.CENTER);

        JScrollPane scrollUserList = new JScrollPane(userList);
        scrollUserList.setPreferredSize(new Dimension(150, 0));
        add(scrollUserList, BorderLayout.EAST);

        bottomPanel.add(btnDisconnect, BorderLayout.WEST);
        bottomPanel.add(fieldInput, BorderLayout.CENTER);
        bottomPanel.add(btnSend, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnDisconnect.addActionListener(this::btnDisconnectActionListener);
        btnLogin.addActionListener(this::btnLoginActionListener);
        btnSend.addActionListener(this::btnSendActionListener);
//        Flowable.just("Hello world").subscribe(textAreaTimeLogger::onLog);
    }

    private void btnDisconnectActionListener(ActionEvent e) {
        this.textAreaTimeLogger.onLog("Disconnect button clicked");
    }

    private void btnLoginActionListener(ActionEvent e) {
        this.textAreaTimeLogger.onLog("Login button clicked");
    }

    private void btnSendActionListener(ActionEvent e) {
        this.textAreaTimeLogger.onLog("Send button clicked");
    }

    private void initLogger() {
        textAreaTimeLogger = new TextAreaTimeLogger(textAreaLog, "yyyy-MM-dd HH:mm:ss.SSS");
    }
}