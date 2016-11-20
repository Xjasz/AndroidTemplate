package com.androidtemplate;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcore.template.R;

import xjasz.core.Singleton;
import xjasz.core.reflection.injectors.Inject;
import xjasz.core.reflection.listeners.Listen;
import xjasz.core.reflection.listeners.Listener;
import xjasz.core.reflection.messages.BackRequestMessage;
import xjasz.core.reflection.messages.BackResponseMessage;
import xjasz.core.reflection.messages.FragmentChangeMessage;
import xjasz.core.reflection.messages.NetworkMessage;
import xjasz.core.reflection.models.BaseActivity;
import xjasz.core.reflection.models.BaseFragment;
import xjasz.core.util.contstant.Constants;
import xjasz.core.webservice.WebService;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private ViewGroup actionBar;
    private TextView tvTitle;
    private ImageView ivMenu;
    private ImageView ivFavorites;
    private ImageView ivUp;
    private ImageView ivDown;

    @Inject(id = R.id.statusBarBackground)
    private View statusBarBackground;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Inject(id = R.id.vfader)
    private View vfader;

    @Inject(id = R.id.vfader)
    private void onFaderPressed() {
        Listener.send(new FragmentChangeMessage( new FragmentDummy(),
                R.id.container_Menufragment,
                R.anim.anim_in,
                R.anim.slideout_to_left));
    }

    @Listen()
    public void onNetworkMessageReceived(NetworkMessage<Integer> request) {
        if (!request.response.contentEquals(Constants.VALID_WEB_EVENT)) {
            Singleton.toastMessage(request.response);
        } else {
            switch (request.object) {
                case WebService.GET_APP_DATA:
                    Singleton.toastMessage(TAG + " GET WebService.APP_DATA = VALID");
                    break;
                case WebService.GET_APP_COMMANDS:
                    Singleton.toastMessage("Location Data Updated");
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Singleton.registerMainActivity(this);
        initializeActionBar();
        initializeActivity();
    }


    @Listen
    public void onFragmentChangeMessageReceived(FragmentChangeMessage fcm) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(fcm.inAnimation, fcm.outAnimation);
        transaction.replace(fcm.container, (BaseFragment) fcm.object).commit();
        if (fcm.container == R.id.container_Menufragment) {
            if (fcm.object instanceof FragmentDummy) {
                Log.d(TAG, "gone");
                vfader.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "visible");
                vfader.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getStatusBarColor(View view) {
        int color = Color.BLACK;
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (view.getBackground() instanceof ColorDrawable) {
                    color = ((ColorDrawable) view.getBackground()).getColor();
                    Log.d(TAG, "color :" + String.valueOf(color));
                    float[] hsv = new float[3];
                    Color.colorToHSV(color, hsv);
                    hsv[2] *= 0.8f;
                    color = Color.HSVToColor(hsv);
                    return color;
                }
            }
        }
        Log.d(TAG, "invalid color");
        return color;
    }

    @Override
    public void onBackPressed() {
        if (vfader.getVisibility() == View.VISIBLE)
            vfader.setVisibility(View.GONE);
        Listener.send(new BackRequestMessage<Integer>(0));
    }

    @Listen()
    public void onBackPressResponseMessage(BackResponseMessage<Integer> response) {
        Log.d(TAG, "onBackPressResponseMessage = " + String.valueOf(response.object));
        switch (response.object) {
            case 0:
                finish();
                break;
        }
    }

    private void initializeActionBar() {
        actionBar = (ViewGroup) findViewById(R.id.container_Actionbar);
        getLayoutInflater().inflate(R.layout.actionbar_activity, actionBar);
        tvTitle = (TextView) actionBar.findViewById(R.id.tvTitle);
        ivFavorites.setColorFilter(Color.parseColor("#FFF6FA02"));
        ivMenu.setColorFilter(Color.WHITE);
        ivUp.setColorFilter(Color.WHITE);
        ivDown.setColorFilter(Color.WHITE);
        ivMenu.setOnClickListener(this);
        ivFavorites.setOnClickListener(this);
        ivUp.setOnClickListener(this);
        ivDown.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializeActivity() {
        getSupportFragmentManager().beginTransaction().add(R.id.container_Mainfragment, new FragmentHome()).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container_Subfragment, new FragmentDummy()).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container_Menufragment, new FragmentDummy()).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMenu:
                if (vfader.getVisibility() == View.VISIBLE) {
                    Listener.send(new FragmentChangeMessage(new FragmentDummy(),
                            R.id.container_Menufragment,
                            R.anim.anim_in,
                            R.anim.slideout_to_left));
                } else {
                    Listener.send(new FragmentChangeMessage(new FragmentNavigation(),
                            R.id.container_Menufragment,
                            R.anim.slidein_fromleft,
                            R.anim.anim_out));
                }
                break;
        }
    }

}
