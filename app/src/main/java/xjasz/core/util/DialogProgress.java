package xjasz.core.util;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import xjasz.core.Singleton;


public class DialogProgress {
    private static final String TAG = DialogProgress.class.getSimpleName();
    private static final int MESSAGE_QUEUE = 999;
    private static String dialogMessage = "";
    private static Handler handler;
    public static android.app.ProgressDialog progressDialog;

    public static void createProgressDialog(final String title, final String message, final String cancelMessage) {
        Singleton.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Progress dialog created");
                dialogMessage = message;
                progressDialog = android.app.ProgressDialog.show(Singleton.getMainActivity(), title,
                        dialogMessage, true);
                progressDialog.setCancelable(true);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        removeProgressDialog();
                        Singleton.toastMessage(cancelMessage);
                    }
                });
                handler = new DialogHandler();
            }
        });
    }

    public static boolean updateProgressDialog(final String message) {
        dialogMessage = message;
        if (progressDialog != null && handler != null) {
            if (!handler.hasMessages(MESSAGE_QUEUE)) {
                Message msg = Message.obtain(handler);
                msg.what = MESSAGE_QUEUE;
                msg.sendToTarget();
            }
            return true;
        } else {
            return false;
        }
    }

    private static class DialogHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (progressDialog != null) {
                progressDialog.setMessage(dialogMessage);
            } else
                Log.d(TAG, "Update dialog fail: " + dialogMessage);
        }
    }

    public static boolean doesProgressDialogExist(boolean remove) {
        return ((remove) ? removeProgressDialog() : progressDialog != null);
    }

    public static boolean removeProgressDialog() {
        if (progressDialog != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }
            });
            return true;
        }
        return false;
    }
}
