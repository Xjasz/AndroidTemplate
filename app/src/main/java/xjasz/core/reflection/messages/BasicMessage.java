package xjasz.core.reflection.messages;

public final class BasicMessage<T> extends Message {
    public final T object;

    public BasicMessage(T object) {
        this.object = object;
        setMessageDescription(object.toString());
    }
}