package main.java.com.kalita_ivan.chat.library;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextAreaTimeLogger {
    private JTextArea textArea;
    private DateFormat dateFormat;

    public TextAreaTimeLogger(JTextArea textArea, String pattern) {
        this.textArea = textArea;
        this.dateFormat = new SimpleDateFormat(pattern);
    }

    public void onLog(String msg) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(dateFormat.format(new Date()) + ": " + msg + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }
}
