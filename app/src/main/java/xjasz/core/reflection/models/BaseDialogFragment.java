package xjasz.core.reflection.models;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import xjasz.core.reflection.controllers.BaseController;
import xjasz.core.reflection.injectors.Injector;
import xjasz.core.reflection.listeners.Listener;


public abstract class BaseDialogFragment extends DialogFragment implements BaseController {
    private final String TAG = getClass().getSimpleName();
    private ViewGroup viewGroup;

    public abstract int getLayout();

    @Override
    public View getView() {
        return viewGroup;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
        viewGroup = (ViewGroup) inflater.inflate(getLayout(), container, false);
        Injector.inject(this);
        Listener.register(this);
        return viewGroup;
    }

    @Override
    public void hideSoftKeyboard() {
        Log.d(TAG, "hideSoftKeyboard");
        if (this != null && getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus().getWindowToken() != null) {
            ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            if (getView() != null) {
                getView().requestFocus();
            }
        }
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, String.valueOf(Thread.currentThread().getStackTrace()[2].getMethodName()));
        hideSoftKeyboard();
        Listener.unregister(this);
        super.onDestroyView();
    }
}
