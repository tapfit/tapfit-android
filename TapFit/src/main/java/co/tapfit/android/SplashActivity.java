package co.tapfit.android;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.Menu;

import com.google.android.gms.maps.model.LatLng;

import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.request.PlaceRequest;
import co.tapfit.android.request.ResponseCallback;

public class SplashActivity extends Activity {

    private static final long SPLASH_TIME = 3000;
    Handler mHandler;
    Runnable mJumpRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LatLng location = LocationServices.getInstance(getApplicationContext()).getLatLng();
        if (location == null)
        {
            //TODO: Make regions call and prompt user
        }
        PlaceRequest.getPlaces(this, new ResponseCallback() {
            @Override
            public void sendCallback(Object responseObject, String message) {
                startActivity(new Intent(SplashActivity.this, MapListActivity.class));
                finish();
            }
        });

        /*mJumpRunnable = new Runnable() {

            public void run() {
                jump();
            }
        };*/
        //mHandler = new Handler();
        //mHandler.postDelayed(mJumpRunnable, SPLASH_TIME);
    }

    private void jump() {
        //it is safe to use this code even if you
        //do not intend to allow users to skip the splash
        if(isFinishing())
            return;
        startActivity(new Intent(this, MapListActivity.class));
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }
    
}
