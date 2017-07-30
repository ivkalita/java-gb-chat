package com.kalita_ivan.chat.client;

import rx.Observable;
import rx.observables.SwingObservable;
import rx.schedulers.SwingScheduler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static com.kalita_ivan.chat.library.mvvm.View2ViewModelBindings.bindSwingView;
import static com.kalita_ivan.chat.library.mvvm.ViewModel2ViewBindings.bindViewModelBoolean;
import static com.kalita_ivan.chat.library.mvvm.ViewModel2ViewBindings.bindViewModelString;

class ChatClientView extends JFrame  {
    private ChatClientViewModel viewModel;

    static private final String DEFAULT_IP = "127.0.0.1";
    static private final String DEFAULT_PORT = "8000";
    static private final String DEFAULT_LOGIN = "login";
    static private final String DEFAULT_PASSWORD = "password";

    private JPanel panelTop;
    private JPanel panelBottom;
    private JTextField fieldIP;
    private JTextField fieldPort;
    private JTextField fieldLogin;
    private JTextField fieldPassword;
    private JTextField fieldMessage;
    private JCheckBox checkAlwaysOnTop;
    private JButton buttonLogin;
    private JButton buttonDisconnect;
    private JButton buttonSend;
    private JTextArea textAreaMessages;
    private JList<String> listUsers;

    ChatClientView() {
        this.buildView();
    }

    private void buildView() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(800, 800));
        setLocationRelativeTo(null);

        panelTop = new JPanel(new GridLayout(2, 3));
        fieldIP = new JTextField(DEFAULT_IP);
        fieldPort = new JTextField(DEFAULT_PORT);
        checkAlwaysOnTop = new JCheckBox("Always on top");
        fieldLogin = new JTextField(DEFAULT_LOGIN);
        fieldPassword = new JPasswordField(DEFAULT_PASSWORD);
        buttonLogin = new JButton("Login");

        textAreaMessages = new JTextArea();
        listUsers = new JList<>();

        panelBottom = new JPanel(new BorderLayout());
        buttonDisconnect = new JButton("Disconnect");
        fieldMessage = new JTextField();
        buttonSend = new JButton("Send");

        panelTop.add(fieldIP);
        panelTop.add(fieldPort);
        panelTop.add(checkAlwaysOnTop);
        panelTop.add(fieldLogin);
        panelTop.add(fieldPassword);
        panelTop.add(buttonLogin);
        add(panelTop, BorderLayout.NORTH);

        JScrollPane scrollLog = new JScrollPane(textAreaMessages);
        textAreaMessages.setEditable(false);
        add(scrollLog, BorderLayout.CENTER);

        JScrollPane scrollUserList = new JScrollPane(listUsers);
        scrollUserList.setPreferredSize(new Dimension(150, 0));
        add(scrollUserList, BorderLayout.EAST);

        panelBottom.add(buttonDisconnect, BorderLayout.WEST);
        panelBottom.add(fieldMessage, BorderLayout.CENTER);
        panelBottom.add(buttonSend, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);
    }

    void bind(ChatClientViewModel viewModel) {
        this.viewModel = viewModel;
        bindSwingView(fieldIP).toViewModel(viewModel.ip);
        bindSwingView(fieldPort).toViewModel(viewModel.port);
        bindSwingView(fieldLogin).toViewModel(viewModel.login);
        bindSwingView(fieldPassword).toViewModel(viewModel.password);
        bindSwingView(fieldMessage).toViewModel(viewModel.message);
        bindSwingView(checkAlwaysOnTop).toViewModel(viewModel.alwaysOnTop);
        bindSwingView(buttonLogin).toViewModel(viewModel.buttonLogin);
        bindSwingView(buttonDisconnect).toViewModel(viewModel.buttonDisconnect);
        bindSwingView(buttonSend).toViewModel(viewModel.buttonSend);

        SwingObservable.fromKeyEvents(fieldMessage)
            .observeOn(SwingScheduler.getInstance())
            .filter(e -> e.getKeyCode() == KeyEvent.VK_ENTER)
            .subscribe(e -> viewModel.buttonSend.onNext(
                new ActionEvent(buttonSend, ActionEvent.ACTION_FIRST, null))
            );
        bindViewModelString(viewModel.message).toSwingViewText(fieldMessage);
        bindViewModelBoolean(viewModel.panelTopVisible).toSwingViewVisiblePropertyOf(panelTop);
        bindViewModelBoolean(viewModel.panelBottomVisible).toSwingViewVisiblePropertyOf(panelBottom);
        bindViewModelBoolean(viewModel.alwaysOnTop).toSwingViewAlwaysOnTopPropertyOf(this);
        bindViewModelString(viewModel.messages).toSwingViewAccumulatedText(textAreaMessages);
    }
}
