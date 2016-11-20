package xjasz.core.reflection.messages;

public final class ResponseMessage<T> extends Message {
    public final T object;

    public ResponseMessage(T object) {
        this.object = object;
        setMessageDescription(object.toString());
    }
}