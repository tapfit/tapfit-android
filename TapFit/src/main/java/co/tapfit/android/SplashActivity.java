package co.tapfit.android;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.Menu;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.helper.Log;
import co.tapfit.android.model.User;
import co.tapfit.android.helper.SharePref;
import co.tapfit.android.request.PlaceRequest;
import co.tapfit.android.request.RegionRequest;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;

public class SplashActivity extends Activity {

    private static final long SPLASH_TIME = 3000;
    Handler mHandler;
    Runnable mJumpRunnable;

    private Boolean mReadyForApp = false;
    private Boolean mPastSplashTime = false;

    Timer splashTimer = new Timer();

    private static final String TAG = SplashActivity.class.getSimpleName();

    DatabaseWrapper dbWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        splashTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mPastSplashTime = true;
                if (mReadyForApp) {
                    launchPostSplashPage();
                }
            }
        }, SPLASH_TIME);

        LocationServices.getInstance(getApplicationContext());

        if (!SharePref.getBooleanPref(getApplicationContext(), SharePref.SELECTED_REGION, false)) {
            RegionRequest.getRegions(getApplicationContext(), new ResponseCallback() {
                @Override
                public void sendCallback(Object responseObject, String message) {
                    mReadyForApp = true;
                    if (mPastSplashTime) {
                        splashTimer.cancel();
                        launchPostSplashPage();
                    }
                }
            });
        }
        else
        {
            RegionRequest.getRegions(getApplicationContext(), null);
        }

        LatLng location = LocationServices.getLatLng();

        if (location != null)
        {
            mReadyForApp = true;
            PlaceRequest.getPlaces(getApplicationContext(), location, false, null);
        }
        /**
         * This is poor design on my part. I'm just calling getPlaces in dev mode.
         * Will change to make the call here and handle the callback when it gets back
         * and not hold up the app for that.
         * TODO: Don't hold up app with getPlaces() call.
         */

        User user = DatabaseWrapper.getInstance(getApplicationContext()).getCurrentUser();
        if (user != null) {
            UserRequest.favorites(getApplicationContext(), null);
        }
        /*mJumpRunnable = new Runnable() {

            public void run() {
                jump();
            }
        };*/
        //mHandler = new Handler();
        //mHandler.postDelayed(mJumpRunnable, SPLASH_TIME);
    }

    private void launchPostSplashPage() {
        if (!isFinishing()) {
            Intent intent;
            if (SharePref.getBooleanPref(SplashActivity.this, SharePref.KEY_PREFS_FIRST_USE, true)) {
                intent = new Intent(SplashActivity.this, FirstUseActivity.class);
            }
            else if (!SharePref.getBooleanPref(SplashActivity.this, SharePref.SELECTED_REGION, false))
            {
                intent = new Intent(SplashActivity.this, RegionListActivity.class);
            }
            else
            {
                intent = new Intent(SplashActivity.this, MapListActivity.class);
            }
            startActivity(intent);
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }
    
}
