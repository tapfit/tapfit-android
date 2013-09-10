package co.tapfit.android;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.google.android.gms.maps.model.LatLng;

import co.tapfit.android.R;
import co.tapfit.android.helper.ImageCache;

public class BaseActivity extends ActionBarActivity {

    protected ImageCache imageCache = ImageCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }
    
}
