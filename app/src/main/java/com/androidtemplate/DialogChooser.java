package com.androidtemplate;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidcore.template.R;

import xjasz.core.Singleton;

public class DialogChooser extends DialogFragment implements OnClickListener {
    private static final String TAG = DialogChooser.class.getSimpleName();
    public static final int REQUEST_CODE_PREPARE_ROSTER = 1001;
    public static final int RESULT_CODE_OK = 1002;
    public static final int RESULT_CODE_CANCEL = 1003;
    public static final int RESULT_CODE_START_SERVICE = 1004;

    private int arg;

    TextView tvTitle;
    TextView tvInfo;
    View rlBody;
    View rlBody2;

    TextView tvCancel;
    TextView tvContinue;
    View rlContinue;

    ProgressBar spinner;
    TextView tvLoader;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(Singleton.getMainActivity(), R.style.MyCustomTheme);
        View view = LayoutInflater.from(Singleton.getAppContext()).inflate(R.layout.dialog_chooser, null, false);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        arg = getArguments().getInt("arg");

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvInfo = (TextView) view.findViewById(R.id.tvInfo);
        rlBody = view.findViewById(R.id.rlBody);
        rlBody2 = view.findViewById(R.id.rlBody2);

        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        tvContinue = (TextView) view.findViewById(R.id.tvContinue);
        rlContinue = view.findViewById(R.id.rlContinue);

        spinner = (ProgressBar) view.findViewById(R.id.spinner);
        tvLoader = (TextView) view.findViewById(R.id.tvLoader);

        if (arg == REQUEST_CODE_PREPARE_ROSTER) {
            tvTitle.setText("Preparing Rosters");
        }

        tvCancel.setOnClickListener(this);
        rlContinue.setOnClickListener(this);

        Singleton.startBlinkAnimation(tvContinue);
        return dialog;
    }


    private void generateRosters() {
        tvTitle.setText("Generating Rosters");
        tvCancel.setOnClickListener(null);
        rlContinue.setOnClickListener(null);

        AlphaAnimation animIn = new AlphaAnimation(0, 1);
        animIn.setInterpolator(new LinearInterpolator());
        animIn.setDuration(300);
        rlBody2.setAnimation(animIn);

        AlphaAnimation animOut = new AlphaAnimation(1, 0);
        animOut.setInterpolator(new LinearInterpolator());
        animOut.setDuration(300);
        rlBody.setAnimation(animOut);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlBody.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                rlBody2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                returnData(arg, RESULT_CODE_OK);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rlBody.startAnimation(animOut);
        rlBody2.startAnimation(animIn);

    }


    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.80f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                Log.d(TAG, "Cancel clicked");
                returnData(arg, RESULT_CODE_CANCEL);
                dismiss();
                break;
            case R.id.rlContinue:
                Log.d(TAG, "Continue clicked");
                generateRosters();
                break;
        }
    }

    private void returnData(int arg, int result) {
        if (arg == REQUEST_CODE_PREPARE_ROSTER) {
            getTargetFragment().onActivityResult(arg,
                    result, null);
        }
    }
}
