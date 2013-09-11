package co.tapfit.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import co.tapfit.android.model.Place;

public class PlaceInfoActivity extends BaseActivity {

    public static final String PLACE_ID = "place_id";
    private Place mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        // Show the Up button in the action bar.
        setupActionBar();

        mPlace = dbWrapper.getPlace(getIntent().getIntExtra(PLACE_ID, -1));

        setUpResourceInfo();
    }


    private void setUpResourceInfo() {

        ImageView imageView = (ImageView) findViewById(R.id.place_image);
        imageCache.loadImageForPlacePage(imageView, mPlace.cover_photo);

        TextView textView = (TextView) findViewById(R.id.place_description);
        textView.setText(mPlace.source_description);

        textView = (TextView) findViewById(R.id.place_name);
        textView.setText(mPlace.name);

        textView = (TextView) findViewById(R.id.place_distance);
        textView.setText(String.format("%.1f", mPlace.getDistance()) + " miles away");

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.place_info, menu);
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
