package xjasz.core.reflection.messages;

public final class FragmentChangeMessage<BaseFragment> extends Message {
    public final BaseFragment object;
    public final int container;
    public final int inAnimation;
    public final int outAnimation;

    public FragmentChangeMessage(BaseFragment object, int container, int inAnimation, int outAnimation) {
        this.object = object;
        this.container = container;
        this.inAnimation = inAnimation;
        this.outAnimation = outAnimation;
        setMessageDescription(object.toString());
    }
}