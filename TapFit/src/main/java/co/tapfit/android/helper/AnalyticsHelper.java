package co.tapfit.android.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.net.ContentHandler;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import co.tapfit.android.R;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;
import co.tapfit.android.request.Request;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;
import co.tapfit.android.service.ApiService;

/**
 * Created by zackmartinsek on 9/16/13.
 */
public class AnalyticsHelper {

    private static final String TAG = AnalyticsHelper.class.getSimpleName();
    private static String mAppVersion;

    private static ArrayList<String> mEventQueue = new ArrayList<String>();

    private static AnalyticsHelper mInstance;
    private static Context mContext;

    public static AnalyticsHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AnalyticsHelper(context);
        }
        return mInstance;
    }

    private AnalyticsHelper(Context context) {

        mContext = context;
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
                //TODO: Add mixpanel
            }
        });
        thread.start();
    }

    public void sendEndOfSessionEvent() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (mEventQueue.size() > 0) {

                    mEventQueue.clear();
                }
            }
        });

        thread.start();
    }
}
