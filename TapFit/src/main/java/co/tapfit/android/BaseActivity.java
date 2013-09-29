package co.tapfit.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.WindowManager;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.model.LatLng;

import co.tapfit.android.R;
import co.tapfit.android.application.TapfitApplication;
import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.AnalyticsHelper;
import co.tapfit.android.helper.ImageCache;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.helper.Log;
import co.tapfit.android.request.UserRequest;
import ly.count.android.api.Countly;

public class BaseActivity extends ActionBarActivity {

    protected ImageCache imageCache = ImageCache.getInstance();
    protected DatabaseWrapper dbWrapper;

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbWrapper = DatabaseWrapper.getInstance(getApplicationContext());



        UserRequest.getMyInfo(getApplicationContext(), null);

        if (Build.VERSION.SDK_INT < 11) {
            getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.transparent_icon));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isNetworkAvailable()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Connection Issues")
                    .setMessage("Check your network connection and come back to app")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();

            alertDialog.show();
        }

        LocationServices.getInstance(getApplicationContext());
        LocationServices.startRecording();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onPause() {
        super.onPause();

        LocationServices.stopRecording();
    }
}
