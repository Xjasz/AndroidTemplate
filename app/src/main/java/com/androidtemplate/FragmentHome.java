package com.androidtemplate;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcore.template.R;

import xjasz.core.Singleton;
import xjasz.core.reflection.injectors.Inject;
import xjasz.core.reflection.listeners.Listen;
import xjasz.core.reflection.listeners.Listener;
import xjasz.core.reflection.messages.BackRequestMessage;
import xjasz.core.reflection.messages.BasicMessage;
import xjasz.core.reflection.messages.FragmentChangeMessage;
import xjasz.core.reflection.models.BaseFragment;
import xjasz.core.webservice.WebService;

public class FragmentHome extends BaseFragment {
    private static final String TAG = FragmentHome.class.getSimpleName();
    private static String CurrentPrefName = "MAIN";

    private DialogChooser dialogChooser;

    @Inject(id = R.id.llSearch)
    private View llSearch;

    @Inject(id = R.id.etSearch)
    private EditText etSearch;

    @Inject(id = R.id.tvStart)
    private TextView tvStart;


    @Inject(id = R.id.tvVersion)
    private TextView tvVersion;

    @Inject(id = R.id.tvStart)
    private void onStartPressed() {
        this.hideSoftKeyboard();
        final String searchString = etSearch.getText().toString().trim().replace(" ", "+");
        if (tvLocationRight.getText().toString().contentEquals("Select Location")) {
            Toast.makeText(Singleton.getAppContext(), "Select Location.", Toast.LENGTH_SHORT).show();
        } else if (tvCategoryRight.getText().toString().contentEquals("Select Category")) {
            Toast.makeText(Singleton.getAppContext(), "Select Category.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Singleton.getAppContext(), "Start", Toast.LENGTH_SHORT).show();
        }
    }

    @Inject(id = R.id.tvCategoryRight)
    private TextView tvCategoryRight;

    @Inject(id = R.id.tvLocationRight)
    private TextView tvLocationRight;

    @Inject(id = R.id.tvHomeTitle)
    private TextView tvHomeTitle;

    @Inject(id = R.id.ivCategory)
    private ImageView ivCategory;

    @Inject(id = R.id.ivLocation)
    private ImageView ivLocation;

    @Inject(id = R.id.ivSearchSettings)
    private ImageView ivSearchSettings;

    @Inject(id = R.id.ivAutomationSettings)
    private ImageView ivAutomationSettings;

    @Inject(id = R.id.ivPost)
    private ImageView ivPost;

    @Inject(id = R.id.rlCategory)
    private View rlCategory;

    @Inject(id = R.id.rlCategory)
    private void onCategoryPressed() {
        this.hideSoftKeyboard();
        Listener.send(new FragmentChangeMessage(new FragmentDummy(),
                R.id.container_Subfragment,
                R.anim.slidein_frombottom,
                R.anim.slideout_to_bottom));
    }

    @Inject(id = R.id.rlLocation)
    private View rlLocation;

    @Inject(id = R.id.rlLocation)
    private void onLocationPressed() {
        this.hideSoftKeyboard();

        Log.d(TAG, "onLocationPressed");
        dialogChooser = new DialogChooser();
        Bundle b = new Bundle();
        b.putInt("arg", DialogChooser.REQUEST_CODE_PREPARE_ROSTER);
        dialogChooser.setTargetFragment(this, DialogChooser.REQUEST_CODE_PREPARE_ROSTER);
        dialogChooser.setArguments(b);
        dialogChooser.show(getFragmentManager(), getClass().getSimpleName());

    }


    @Inject(id = R.id.rlSearchSettings)
    private View rlSearchSettings;

    @Inject(id = R.id.rlSearchSettings)
    private void onSearchSettingsPressed() {
        WebService webService = new WebService();
        webService.webEvent(new int[]{WebService.GET, WebService.GET_APP_DATA}, null);
        this.hideSoftKeyboard();
    }


    @Inject(id = R.id.rlAutomationSettings)
    private View rlAutomationSettings;

    @Inject(id = R.id.rlAutomationSettings)
    private void onAutomationSettingsPressed() {
        this.hideSoftKeyboard();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://accounts.craigslist.org/"));
        startActivity(intent);
    }

    @Inject(id = R.id.rlPost)
    private View rlPost;

    @Inject(id = R.id.rlPost)
    private void onPostPressed() {
        this.hideSoftKeyboard();
    }


    @Listen(consumed = true)
    public void onMessageReceived(BackRequestMessage<Integer> request) {
        Log.d(TAG, "got back press");
        switch (request.object) {
            case 0:
                request.respond(0);
                break;
        }
    }

    @Listen()
    public void onSimpleMessageReceived(BasicMessage<Integer> request) {
        Log.d(TAG, "got Simple Message");
        switch (request.object) {
            case 112:
                Singleton.toastMessage(TAG + ": onSimpleMessageReceived " + String.valueOf(request.object));
                break;
        }
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewGroup = super.onCreateView(inflater, container, savedInstanceState);
        initializeFragment();
        tvVersion.setText("Version: " + Singleton.APP_VERSION_NAME);
        return viewGroup;
    }

    @Override
    public void onDestroyView() {
        this.hideSoftKeyboard();
        super.onDestroyView();
    }

    private void initializeFragment() {
        ivCategory.setColorFilter(Color.parseColor("#783381"));
        ivLocation.setColorFilter(Color.parseColor("#783381"));
        ivSearchSettings.setColorFilter(Color.parseColor("#783381"));
        ivAutomationSettings.setColorFilter(Color.parseColor("#783381"));
        ivPost.setColorFilter(Color.parseColor("#783381"));

        Typeface myTypeface = Typeface.createFromAsset(Singleton.getAppContext().getAssets(),
                "fonts/deadheadrough.ttf");
        tvHomeTitle.setTypeface(myTypeface);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d(TAG, "requestCode = " + String.valueOf(requestCode));
        Log.d(TAG, "resultCode = " + String.valueOf(resultCode));
        if (requestCode == DialogChooser.REQUEST_CODE_PREPARE_ROSTER && resultCode == DialogChooser.RESULT_CODE_OK) {
            Singleton.toastMessage(TAG + " onActivityResult resultCode = " + String.valueOf(resultCode));
            if (dialogChooser != null) {
                dialogChooser.dismiss();
            }
        }
    }
}
