package main.java.com.kalita_ivan.chat.library;

import javax.swing.*;
import java.awt.*;

public class DefaultGUIWindow extends JFrame {
    protected DefaultGUIWindow() {
        initGUI();
        initListeners();
        setVisible(true);
    }

    protected void initGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(getDefaultDimension()));
        setLocationRelativeTo(null);
        setTitle(getDefaultTitle());
    }

    protected Dimension getDefaultDimension() {
        return new Dimension(800, 400);
    }

    protected String getDefaultTitle() {
        return "Chat window";
    }

    protected void initListeners() {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultGUIExceptionHandler());
    }
}

