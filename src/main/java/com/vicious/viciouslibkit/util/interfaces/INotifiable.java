package com.vicious.viciouslibkit.util.interfaces;

public interface INotifiable<T> {
    void notify(INotifier<T> sender, T status);
}
