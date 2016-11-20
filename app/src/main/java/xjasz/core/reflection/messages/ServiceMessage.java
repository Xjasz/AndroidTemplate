package xjasz.core.reflection.messages;

public final class ServiceMessage<T> extends Message {
    public final T object;

    public ServiceMessage(T object) {
        this.object = object;
        setMessageDescription(object.toString());
    }
}