package xjasz.core.reflection.messages;


import xjasz.core.reflection.listeners.Listener;

public final class BackRequestMessage<T> extends Message {
    public final T object;

    public BackRequestMessage(T object) {
        this.object = object;
        setMessageDescription(object.toString());
    }

    @Override
    public void respond(Object object) {
        Listener.send(new BackResponseMessage<>(object));
    }
}