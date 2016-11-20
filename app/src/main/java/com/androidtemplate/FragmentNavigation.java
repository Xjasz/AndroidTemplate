package com.androidtemplate;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidcore.template.R;

import xjasz.core.Singleton;
import xjasz.core.reflection.injectors.Inject;
import xjasz.core.reflection.listeners.Listen;
import xjasz.core.reflection.listeners.Listener;
import xjasz.core.reflection.messages.BackRequestMessage;
import xjasz.core.reflection.messages.BackResponseMessage;
import xjasz.core.reflection.messages.FragmentChangeMessage;
import xjasz.core.reflection.models.BaseFragment;

public class FragmentNavigation extends BaseFragment {
    private final String TAG = getClass().getSimpleName();

    @Inject(id = R.id.ivRate)
    private ImageView ivRate;

    @Inject(id = R.id.ivContactUs)
    private ImageView ivContactUs;

    @Inject(id = R.id.ivLogout)
    private ImageView ivLogout;

    @Inject(id = R.id.ivDonate)
    private ImageView ivDonate;

    @Inject(id = R.id.ivInfo)
    private ImageView ivInfo;

    @Inject(id = R.id.ivShare)
    private ImageView ivShare;

    @Inject(id = R.id.ivHome)
    private ImageView ivHome;

    @Inject(id = R.id.rlShare)
    private void onSharePressed() {
        Log.d(TAG, "onSharePressed");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String shareText = "https://play.google.com/store/apps/details?id=com.delvedapps.craigslistchecker";
        shareIntent.putExtra("sms_body", shareText);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Craigslist Checker");
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share with"));
    }

    @Inject(id = R.id.viewroot)
    private void onViewRootPressed() {
        Log.d(TAG, "onViewRootPressed");
    }

    @Inject(id = R.id.rlLogout)
    private void onLogoutPressed() {
        Log.d(TAG, "onLogoutPressed");
        Listener.send(new BackResponseMessage<>(0));
    }

    @Inject(id = R.id.rlDonate)
    private void onDonatePressed() {
        Log.d(TAG, "onDonatePressed");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HD7DZQMXGT7DN"));
        startActivity(intent);
    }

    @Inject(id = R.id.rlInfo)
    private void onInfoPressed() {
        Log.d(TAG, "onInfoPressed");
        Listener.send(new FragmentChangeMessage(new FragmentDummy(),
                R.id.container_Menufragment,
                R.anim.fade_in,
                R.anim.slideout_to_left));
        Listener.send(new FragmentChangeMessage(new FragmentDummy(),
                R.id.container_Subfragment,
                R.anim.fade_in,
                R.anim.fade_out));
        Listener.send(new FragmentChangeMessage(new FragmentDummy(),
                R.id.container_Mainfragment,
                R.anim.fade_in,
                R.anim.fade_out));
    }

    @Inject(id = R.id.rlHome)
    private void onHomePressed() {
        Log.d(TAG, "onHomePressed");
        Listener.send(new FragmentChangeMessage(new FragmentDummy(),
                R.id.container_Menufragment,
                R.anim.fade_in,
                R.anim.slideout_to_left));
        Listener.send(new FragmentChangeMessage(new FragmentHome(),
                R.id.container_Mainfragment,
                R.anim.fade_in,
                R.anim.fade_out));
        Listener.send(new FragmentChangeMessage(new FragmentDummy(),
                R.id.container_Subfragment,
                R.anim.fade_in,
                R.anim.fade_out));
    }

    @Inject(id = R.id.rlRate)
    private void onRatePressed() {
        Log.d(TAG, "onRatePressed");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.delvedapps.craigslistchecker"));
        startActivity(intent);
    }


    @Inject(id = R.id.rlContactUs)
    private void onContactUsPressed() {
        Log.d(TAG, "onContactUsPressed");
        String SUBJECT = "CraigslistChecker - Support Message";
        String[] SUPPORTEMAILS = {"support@delvedapps.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, SUPPORTEMAILS);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\n\n\n\n\n\n\n\n" + Singleton.DEVICE_ID + "\n"
                + Singleton.APP_VERSION_CODE);
        emailIntent.setType("plain/text");
        startActivity(emailIntent);

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_navigationmenu;
    }

    @Listen(consumed = true)
    public void onMessageReceived(BackRequestMessage<Integer> request) {
        switch (request.object) {
            case 0:
                Listener.send(new FragmentChangeMessage(new FragmentDummy(),
                        R.id.container_Menufragment,
                        R.anim.fade_in,
                        R.anim.slideout_to_left));
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewGroup = super.onCreateView(inflater, container, savedInstanceState);
        initializeFragment();
        return viewGroup;
    }

    @Override
    public void onDestroyView() {
        this.hideSoftKeyboard();
        super.onDestroyView();
    }

    private void initializeFragment() {
        ivRate.setColorFilter(Color.WHITE);
        ivContactUs.setColorFilter(Color.WHITE);
        ivLogout.setColorFilter(Color.WHITE);
        ivDonate.setColorFilter(Color.WHITE);
        ivInfo.setColorFilter(Color.WHITE);
        ivShare.setColorFilter(Color.WHITE);
        ivHome.setColorFilter(Color.WHITE);
    }

}