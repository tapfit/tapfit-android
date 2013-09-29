package co.tapfit.android.application;

import android.app.Activity;
import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import co.tapfit.android.MapListActivity;
import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.AnalyticsHelper;
import co.tapfit.android.helper.ImageCache;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.helper.Log;
import co.tapfit.android.R;
import co.tapfit.android.helper.RegionBounds;

/**
 * Created by zackmartinsek on 9/9/13.
 */
public class TapfitApplication extends Application {

    private static final String TAG = TapfitApplication.class.getSimpleName();

    protected DatabaseWrapper dbWrapper;

    @Override
    public void onCreate() {

        super.onCreate();
        Crashlytics.start(this);

        // Set whether we're logging and at what level
        try {
            Log.setLogging(Boolean.valueOf(getString(R.string.logging)));
            Log.setLogLevel(Integer.valueOf(getString(R.string.logging_level)));
        } catch (Exception e) {
            Log.e(TAG, "Something went wrong setting logging and/or logging level.  App will set what it can and use defaults otherwise.", e);
        }
        // This should be the first LogCat entry specific to this application
        Log.v(TAG, "onCreate()");

        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dbWrapper = DatabaseWrapper.getInstance(getApplicationContext());
            }
        });
        newThread.start();
        ImageCache.initImageLoader(getApplicationContext());
        RegionBounds.getInstance(getApplicationContext());
        AnalyticsHelper.getInstance(getApplicationContext());
    }

}
