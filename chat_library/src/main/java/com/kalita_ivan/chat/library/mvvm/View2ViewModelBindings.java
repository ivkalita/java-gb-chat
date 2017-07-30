package com.kalita_ivan.chat.library.mvvm;

import rx.Observable;
import rx.Observer;
import rx.functions.Func0;
import rx.observables.SwingObservable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

public class View2ViewModelBindings {
    public static BindOfAble<ActionEvent> bindSwingView(AbstractButton source) {
        return new BindOfAble<>(SwingObservable.fromButtonAction(source));
    }

    public static BindOfAble<Boolean> bindSwingView(JToggleButton source) {
        return new BindOfAble<>(SwingObservable.fromButtonAction(source).map(actionEvent -> source.isSelected()));
    }

    public static BindOfAble<String> bindSwingView(JTextComponent source) {
        return new BindOfAble<>(SwingObservable.fromDocumentEvents(source.getDocument())
            .map(documentEvent1 -> source.getText()));
    }

    public static BindOfAble<Integer> bindSwingView(JTextComponent source, AbstractButton sourceEventTrigger) {
        return new BindOfAble<>(SwingObservable.fromButtonAction(sourceEventTrigger)
            .map(actionEvent -> Integer.parseInt(source.getText())));
    }

    public static <T> BindOfAble<T> bindSwingView(AbstractButton sourceEventTrigger, Func0<T> function) {
        return new BindOfAble<>(
            SwingObservable.fromButtonAction(sourceEventTrigger)
                .map(buttonClick -> function.call()));
    }

    public static class BindOfAble<T> {

        private final Observable<T> source;

        private BindOfAble(final Observable<T> source) {
            this.source = source;
        }

        public void toViewModel(Observer<T> target) {
            source.subscribe(target);
        }
    }
}
