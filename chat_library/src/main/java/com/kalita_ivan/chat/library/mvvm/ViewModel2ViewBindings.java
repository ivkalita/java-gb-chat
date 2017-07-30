package com.kalita_ivan.chat.library.mvvm;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.SwingScheduler;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class ViewModel2ViewBindings {
    public static BooleanBindOfAble bindViewModelBoolean(Observable<Boolean> source) {
        return new BooleanBindOfAble(source);
    }

    public static StringBindOfAble bindViewModelString(Observable<String> source) {
        return new StringBindOfAble(source);
    }

    public static <T> BindOfAble<T> bindViewModel(Observable<T> source) {
        return new BindOfAble<>(source);
    }

    public static class BooleanBindOfAble {

        private final Observable<Boolean> source;

        private BooleanBindOfAble(final Observable<Boolean> source) {
            this.source = source;
        }

        public void toSwingViewAlwaysOnTopPropertyOf(Window target) {
            source.observeOn(SwingScheduler.getInstance())
                .subscribe(value -> {
                    if (target.isAlwaysOnTop() == value) {
                        return;
                    }
                    target.setAlwaysOnTop(value);
                });
        }

        public void toSwingViewVisiblePropertyOf(JComponent target) {
            source.observeOn(SwingScheduler.getInstance())
                .subscribe(value -> {
                    if (target.isVisible() == value) {
                        return;
                    }
                    target.setVisible(value);
                });
        }
    }

    public static class StringBindOfAble {

        private final Observable<String> source;

        private StringBindOfAble(final Observable<String> source) {
            this.source = source;
        }

        public void toSwingViewText(JTextComponent target) {
            source.observeOn(SwingScheduler.getInstance())
                .subscribe(text -> {
                    if (text.equals(target.getText())) {
                        return;
                    }
                    target.setText(text);
                });
        }

        public void toSwingViewAccumulatedText(JTextComponent target) {
            source.observeOn(SwingScheduler.getInstance())
                .subscribe(text -> target.setText(target.getText() + text));
        }
    }

    public static class BindOfAble<T> {

        private final Observable<T> source;

        private BindOfAble(final Observable<T> source) {
            this.source = source;
        }

        public void toAction(Action1<T> action) {
            source.observeOn(SwingScheduler.getInstance())
                .subscribe(action);
        }
    }
}
