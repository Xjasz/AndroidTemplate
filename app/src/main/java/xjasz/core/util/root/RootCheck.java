package xjasz.core.util.root;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import xjasz.core.Singleton;
import xjasz.core.util.contstant.Constants;

public class RootCheck {
    private static final String TAG = RootCheck.class.getSimpleName();

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su",
                "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"};
        for (String path : paths)
            if (new File(path).exists()) return true;
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return (in.readLine() != null);
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    public static boolean checkForSystemApp() {
        List<ApplicationInfo> packages = Singleton.getAppContext().getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        Log.d(TAG, "PACKAGE_NAME " + Singleton.PACKAGE_NAME);
        Log.d(TAG, "packages size = " + packages.size());
        for (ApplicationInfo packageInfo : packages) {
            if (Singleton.PACKAGE_NAME.contains(packageInfo.packageName)) {
                Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
                return packageInfo.sourceDir.contains(Constants.SYSTEM_DIRECTORY);
            }
        }
        return false;
    }
}
