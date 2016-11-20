package xjasz.core;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import xjasz.core.storage.database.SqlDatabase;
import xjasz.core.util.abstraction.AbstractAnimation;
import xjasz.core.util.contstant.Constants;

@SuppressWarnings("unused")
public final class Singleton extends Application {
    private static final String TAG = Singleton.class.getSimpleName();
    private static Context CONTEXT;
    private static Activity ACTIVITY;
    public static String APP_VERSION_CODE;
    public static String APP_VERSION_NAME;
    public static String DEVICE_ID;
    public static String LOCAL_FILE;
    public static String FOLDER_ROOT;
    public static String PACKAGE_NAME;
    public static SqlDatabase SQL_DB;
    public static int DEVICE_HEIGHT;
    public static int DEVICE_WIDTH;
    public static float DEVICE_DPI;
    public static Date DATE;

    public static boolean AFTER_JELLYBEAN;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application Singleton Created");
        Singleton.CONTEXT = getApplicationContext();
        CONTEXT = getApplicationContext();
        SQL_DB = new SqlDatabase(CONTEXT);
        DEVICE_ID = Settings.Secure.getString(CONTEXT.getContentResolver(), Settings.Secure.ANDROID_ID);
        DEVICE_HEIGHT = CONTEXT.getResources().getDisplayMetrics().heightPixels;
        DEVICE_WIDTH = CONTEXT.getResources().getDisplayMetrics().widthPixels;
        DEVICE_DPI = CONTEXT.getResources().getDisplayMetrics().density;
        PACKAGE_NAME = CONTEXT.getPackageName();
        AFTER_JELLYBEAN = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2);
        FOLDER_ROOT = Environment.getExternalStorageDirectory().toString() + Constants.FRONT_SLASH + CONTEXT.getApplicationInfo().labelRes;
        new File(FOLDER_ROOT).mkdirs();
        try {
            APP_VERSION_CODE = String.valueOf(CONTEXT.getPackageManager().getPackageInfo(CONTEXT.getPackageName(), 0).versionCode);
            APP_VERSION_NAME = CONTEXT.getPackageManager().getPackageInfo(CONTEXT.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            APP_VERSION_CODE = Constants.APP_VERSION_UNKNOWN;
            APP_VERSION_NAME = Constants.APP_VERSION_UNKNOWN;
        }
    }

    public static Context getAppContext() {
        return CONTEXT;
    }

    public static Activity getMainActivity() {
        return ACTIVITY;
    }

    public static void registerMainActivity(Activity activity) {
        ACTIVITY = activity;
    }

    public static void unRegisterMainActivity() {
        ACTIVITY = null;
    }

    public static void toastMessage(final String message) {
        if (ACTIVITY != null && CONTEXT != null)
            ACTIVITY.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CONTEXT, message, Toast.LENGTH_SHORT).show();
                }
            });
    }

    public static void pause(int pauseTime) {
        try {
            Thread.sleep(pauseTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void storeFileLocation(String file) {
        SimpleDateFormat s = new SimpleDateFormat("MMddyy-hhmmss", Locale.getDefault());
        if (file == null) {
            LOCAL_FILE = FOLDER_ROOT + File.separator + s.format(new Date()) + Constants.FILETYPE_JPEG;
        } else {
            String ext = getShortFileExtension(file);
            if (ext != null)
                LOCAL_FILE = FOLDER_ROOT + File.separator + s.format(new Date()) + Constants.PERIOD + ext;
            else
                toastMessage(Constants.INVALID_FILE_TYPE);
        }
    }

    public static String getPath(Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = CONTEXT.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
            assert cursor != null;
            if (cursor.moveToFirst())
                return cursor.getString(cursor.getColumnIndexOrThrow("_data"));
            cursor.close();
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String priceWithDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(price);
    }

    private static String priceWithoutDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return formatter.format(price);
    }

    public static String priceToString(Double price) {
        String toShow = priceWithoutDecimal(price);
        if (toShow.indexOf(Constants.PERIOD) > 0) {
            return priceWithDecimal(price);
        } else {
            return priceWithoutDecimal(price);
        }
    }

    public static void startBlinkAnimation(View view) {
        AlphaAnimation blinkAnimation = new AlphaAnimation(1, .5f);
        blinkAnimation.setDuration(300);
        blinkAnimation.setInterpolator(new LinearInterpolator());
        blinkAnimation.setRepeatCount(Animation.INFINITE);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(blinkAnimation);
    }

    public static void startFadeAnimation(final View view, final boolean in, int duration) {
        view.setVisibility(View.VISIBLE);
        Animation animation;
        if (in) {
            animation = new AlphaAnimation(0, 1);
        } else {
            animation = new AlphaAnimation(1, 0);
        }
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(duration);
        animation.setAnimationListener(new AbstractAnimation() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (!in)
                    view.setVisibility(View.GONE);
            }
        });
        view.startAnimation(animation);
    }

    public static void translateAnimation(View view, float xFrom, float xTo, float yFrom, float yTo, int duration, boolean fillAfter) {
        Animation animation = new TranslateAnimation(xFrom, xTo, yFrom, yTo);
        animation.setDuration(duration);
        animation.setFillAfter(fillAfter);
        view.startAnimation(animation);
    }

    public static void shareIntent(String message) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(Constants.MIME_TYPE_PLAIN_TEXT);
        i.putExtra(Intent.EXTRA_TEXT, message);
        getMainActivity().startActivity(Intent.createChooser(i, Constants.SHARE_TITLE));
    }

    public static void emailIntent(String email, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(Constants.MAIL_TO, email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        getMainActivity().startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public static void setSpannableTextView(TextView textview, String searchString, String text) {
        if (searchString.length() > 0 && text.toLowerCase(Locale.getDefault()).contains(searchString.toLowerCase(Locale.getDefault()))) {
            Spannable spanText = Spannable.Factory.getInstance().newSpannable(text);
            int start = 0;
            while (true) {
                if (text.toLowerCase(Locale.getDefault()).indexOf(searchString.toLowerCase(Locale.getDefault()), start) != -1) {
                    start = text.toLowerCase(Locale.getDefault()).indexOf(searchString.toLowerCase(Locale.getDefault()), start);
                    spanText.setSpan(new BackgroundColorSpan(Color.parseColor("#27AAE1")), start, start + searchString.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanText.setSpan(new ForegroundColorSpan(Color.WHITE), start, start + searchString.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = start + searchString.length();
                } else {
                    textview.setText(spanText);
                    break;
                }
            }
        } else
            textview.setText(text);
    }

    public static void setClickablePart(TextView textview, int color, String text, String startIndex, String endIndex) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(new BackgroundColorSpan(color), text.indexOf(startIndex),
                text.indexOf(endIndex, text.indexOf(startIndex)) + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), text.indexOf(startIndex),
                text.indexOf(endIndex, text.indexOf(startIndex)) + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Log.d(TAG, "match was clicked");
            }
        }, text.indexOf(startIndex), text.indexOf(endIndex, text.indexOf(startIndex)) + 1, 0);
        textview.setText(ssb);
    }

    public static void cannotConnectToLocationServices() {
        try {
            AlertDialog.Builder ADB = new AlertDialog.Builder(Singleton.getMainActivity());
            ADB.setTitle(Constants.LOCATION_FAIL_TITLE);
            ADB.setMessage(Constants.LOCATION_FAIL_MESSAGE);
            ADB.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = ADB.show();
            TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);

            TextView titleView = (TextView) dialog.findViewById(CONTEXT.getResources().getIdentifier("alertTitle", "id",
                    "android"));
            if (titleView != null) {
                titleView.setGravity(Gravity.CENTER);
            }
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasImageCaptureBug() {
        ArrayList<String> devices = new ArrayList<>();
        devices.add("android-devphone1/dream_devphone/dream");
        devices.add("generic/sdk/generic");
        devices.add("vodafone/vfpioneer/sapphire");
        devices.add("tmobile/kila/dream");
        devices.add("verizon/voles/sholes");
        devices.add("google_ion/google_ion/sapphire");
        return devices.contains(Build.BRAND + "/" + Build.PRODUCT + "/"
                + Build.DEVICE);
    }

    public static String getShortFileExtension(String fileName) {
        String filenameArray[] = fileName.split("\\.");
        if (filenameArray.length > 0)
            return filenameArray[filenameArray.length - 1];
        else
            return null;
    }


    public static Date returnFormattedDate(String dateToFormat, int type) {
        Date date = null;
        String formattedDate = "";
        try {
            Log.d(TAG, "dateToFormat = " + dateToFormat);
            switch (type) {
                case 0:
                    date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(dateToFormat);
                    break;
                case 1:
                    date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateToFormat);
                    break;
                case 2:
                    date = new SimpleDateFormat("yyyyMMdd:HHmmss").parse(dateToFormat);
                    break;
                case 3:
                    date = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm").parse(dateToFormat);
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void setDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
        String formattedDate = df.format(c.getTime());
        try {
            DATE = df.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String returnElapsedDate(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        String date = "";
        if (elapsedDays == 1)
            return elapsedDays + " day ago";
        else if (elapsedDays != 0)
            return elapsedDays + " days ago";
        else if (elapsedHours == 1)
            return elapsedHours + " hour ago";
        else if (elapsedHours != 0)
            return elapsedHours + " hours ago";
        else if (elapsedMinutes == 1)
            return elapsedMinutes + " minute ago";
        else if (elapsedMinutes != 0)
            return elapsedMinutes + " minutes ago";
        else if (elapsedSeconds == 1)
            date += elapsedSeconds + " second ago";
        else if (elapsedSeconds != 0)
            date += elapsedSeconds + " seconds ago";
        return date;
    }

    public static boolean checkConnection() {
        ConnectivityManager conn = ((ConnectivityManager) getMainActivity().getSystemService(Activity.CONNECTIVITY_SERVICE));
        if (conn.getActiveNetworkInfo() == null || !conn.getActiveNetworkInfo().isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getMainActivity());
            builder.setTitle(Constants.CONNECTION_FAIL_TITLE);
            builder.setMessage(Constants.CONNECTION_FAIL_MESSAGE);
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog dialog = builder.show();
            TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);
            TextView titleView = (TextView) dialog.findViewById(getMainActivity().getResources().getIdentifier("alertTitle", "id",
                    "android"));
            if (titleView != null)
                titleView.setGravity(Gravity.CENTER);
            return false;
        }
        return true;
    }

    public static void formatImageFile(File file, int maxWidth, int maxHeight) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        try {
            ExifInterface ei = new ExifInterface(file.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d(TAG, "orientation = " + String.valueOf(orientation));

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                bitmap = rotateImage(bitmap, 90);
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                bitmap = rotateImage(bitmap, 180);
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                bitmap = rotateImage(bitmap, 270);

            if (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight)
                bitmap = resizeImage(bitmap, bitmap.getWidth() > bitmap.getHeight() ? maxWidth * 1.0f / bitmap.getWidth() : maxHeight * 1.0f / bitmap.getHeight());

            saveBitmapToFile(bitmap, file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Bitmap resizeImage(Bitmap bitmap, float scale) {
        Log.d(TAG, "resizeImage = " + String.valueOf(scale));
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        return Bitmap.createScaledBitmap(bitmap, (int) (width * scale), (int) (height * scale), false);
    }

    private static Bitmap rotateImage(Bitmap bitmap, int angle) {
        Log.d(TAG, "rotateImage = " + String.valueOf(angle) + " degrees");
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static void saveBitmapToFile(Bitmap bitmap, File file) throws IOException {
        OutputStream fOut = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
    }

    private static String addDayOfMonthSuffix(String dayOfMonthSuffix, String formattedDate) {
        return formattedDate.substring(0, formattedDate.indexOf(" ", formattedDate.indexOf(", ") + 2)) + dayOfMonthSuffix +
                formattedDate.substring(formattedDate.indexOf(" ", formattedDate.indexOf(", ") + 2), formattedDate.length());
    }

    private static String getDayOfMonthSuffix(final int n) {
        if (n < 1 || n > 31) {
            return "illegal day of month: " + n;
        }
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static boolean saveSelectedFile(Intent data) {
        Log.d(TAG, "saveSelectedFile: " + data.getData().toString());
        Uri uri = data.getData();
        try {
            String path = getPath(getAppContext(), uri);
            if (path != null) {
                setFileToSave(path);
                Log.d(TAG, "Copy from Path " + path + " to " + LOCAL_FILE);
                copyFile(new File(path), new File(LOCAL_FILE));
            } else {
                setFileToSave(Constants.FILETYPE_PNG);
                Log.d(TAG, "Copy from Uri " + uri + " to " + LOCAL_FILE);
                InputStream input = getAppContext().getContentResolver().openInputStream(uri);
                OutputStream output = new FileOutputStream(LOCAL_FILE);
                byte[] buffer = new byte[4 * 1024];
                int read;
                assert input != null;
                while ((read = input.read(buffer)) != -1)
                    output.write(buffer, 0, read);
                output.flush();
                output.close();
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage("Invalid file type: cannot be uploaded");
            return false;
        }
        return true;
    }

    public static void setFileToSave(String fileName) {
        SimpleDateFormat date = new SimpleDateFormat("MMddyy-hhmmss", Locale.getDefault());
        String ext = getShortFileExtension(fileName);
        if (ext != null) {
            LOCAL_FILE = FOLDER_ROOT + File.separator + date.format(new Date()) + "." + ext;
        } else {
            toastMessage("Invalid File Type.");
        }
    }

    public static void removeFiles(ArrayList<String> files) {
        for (String file : files) {
            Log.d(TAG, "Removing File: " + String.valueOf(new File(file).delete()));
        }
    }

    public static String repeat(String s, int times) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < times; i++) {
            b.append(s);
        }
        return b.toString();
    }

    public static boolean isParsable(String string, String type) {
        boolean parsable = true;
        try {
            if (type.contentEquals("int"))
                Integer.parseInt(string);
            else if (type.contentEquals("float"))
                Float.parseFloat(string);
            else if (type.contentEquals("double"))
                Double.parseDouble(string);
        } catch (NumberFormatException e) {
            parsable = false;
        }
        return parsable;
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst())
                    return cursor.getString(column_index);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static void copyFile(File inFile, File outFile) throws IOException {
        if (!inFile.exists()) {
            Log.d(TAG, inFile.toString() + " does not exist");
            return;
        }
        FileChannel source = new FileInputStream(inFile).getChannel();
        FileChannel destination = new FileOutputStream(outFile).getChannel();
        destination.transferFrom(source, 0, source.size());
        source.close();
        destination.close();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static ValueAnimator getValueAnimator(final View v1, final View v2, int from, int to, final int type) {
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((Integer)
                        valueAnimator.getAnimatedValue(), RelativeLayout.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                v2.setLayoutParams(params);
                v1.setLayoutParams(params);
            }
        });
        return anim;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) CONTEXT.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER, model = Build.MODEL;
        return model.startsWith(manufacturer) ? capitalizePhrase(model) : capitalizePhrase(manufacturer) + " " + model;
    }

    private static String capitalizePhrase(String s) {
        if (s == null || s.length() == 0)
            return s;
        else {
            StringBuilder phrase = new StringBuilder();
            boolean next = true;
            for (char c : s.toCharArray()) {
                if (next && Character.isLetter(c) || Character.isWhitespace(c))
                    next = Character.isWhitespace(c = Character.toUpperCase(c));
                phrase.append(c);
            }
            return phrase.toString();
        }
    }

}
