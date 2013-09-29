package co.tapfit.android.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;

import co.tapfit.android.R;
import ly.count.android.api.Countly;

/**
 * Created by zackmartinsek on 9/16/13.
 */
public class AnalyticsHelper {

    private static final String TAG = AnalyticsHelper.class.getSimpleName();
    private static String mAppVersion;

    private static ArrayList<String> mEventQueue = new ArrayList<String>();

    private static AnalyticsHelper mInstance;

    public static AnalyticsHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AnalyticsHelper(context);
        }
        return mInstance;
    }

    private AnalyticsHelper(Context context) {

        PackageInfo pInfo = null;
        try
        {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.d(TAG, "Failed to get version");
        }
        mAppVersion = pInfo.versionName;
    }

    public void logEvent(String name) {
        logEvent(name, null);
    }

    public void logEvent(final String name, HashMap<String, String> args) {

        if (args == null) {
            args = new HashMap<String, String>();
        }

        args.put("app_version", "android: " + mAppVersion);

        final HashMap<String, String> finalArgs = new HashMap<String, String>(args);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Countly.sharedInstance().recordEvent(name, finalArgs, 1);
                FlurryAgent.logEvent(name, finalArgs);
            }
        });
        thread.start();

        saveEventForFuture(name);
    }

    private void saveEventForFuture(String name) {
        mEventQueue.add(name);
    }

    public void sendEndOfSessionEvent() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (mEventQueue.size() > 0) {
                    String flowString = "";

                    for (String event : mEventQueue) {
                        flowString = flowString + " -> " + event;
                    }

                    flowString = flowString + " -> END";

                    HashMap<String, String> args = new HashMap<String, String>();
                    args.put("app_version", "android: " + mAppVersion);
                    args.put("android-flow", flowString);

                    Countly.sharedInstance().recordEvent("Funnel", args, 1);

                    mEventQueue.clear();
                }
            }
        });

        thread.start();
    }
}
