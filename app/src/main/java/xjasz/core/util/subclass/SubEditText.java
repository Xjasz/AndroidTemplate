package xjasz.core.util.subclass;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class SubEditText extends EditText {
	IOnBackButtonListener _listener;

	public interface IOnBackButtonListener {
		boolean OnEditTextBackButton();
	}

	public SubEditText(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public void setOnBackButtonListener(IOnBackButtonListener l) {
		_listener = l;
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
			if (_listener != null && _listener.OnEditTextBackButton())
				return true;
		}
		return super.onKeyPreIme(keyCode, event);
	}
}