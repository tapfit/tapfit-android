package co.tapfit.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.model.LatLng;

import co.tapfit.android.R;
import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.ImageCache;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.request.UserRequest;

public class BaseActivity extends ActionBarActivity {

    protected ImageCache imageCache = ImageCache.getInstance();
    protected DatabaseWrapper dbWrapper;

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, getResources().getString(R.string.flurry_api_key));
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbWrapper = DatabaseWrapper.getInstance(getApplicationContext());

        UserRequest.getMyInfo(getApplicationContext(), null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public void onResume(){
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
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
}
