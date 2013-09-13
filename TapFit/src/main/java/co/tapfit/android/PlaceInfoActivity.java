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

import co.tapfit.android.fragment.PlaceCardFragment;
import co.tapfit.android.fragment.WorkoutListFragment;
import co.tapfit.android.model.Place;

public class PlaceInfoActivity extends BaseActivity {

    public static final String PLACE_ID = "place_id";
    private static final String PLACE_FRAGMENT = "place_fragment";
    private static final String WORKOUT_FRAGMENT = "workout_fragment";
    private Place mPlace;

    private PlaceCardFragment mPlaceCardFragment;
    private WorkoutListFragment mWorkoutListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        // Show the Up button in the action bar.
        setupActionBar();

        mPlace = dbWrapper.getPlace(getIntent().getIntExtra(PLACE_ID, -1));

        setUpFragments();
    }

    private void setUpFragments() {
        mPlaceCardFragment = new PlaceCardFragment();
        Bundle args = new Bundle();
        args.putInt(PLACE_ID, mPlace.id);
        mPlaceCardFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mPlaceCardFragment, PLACE_FRAGMENT).commit();
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

    public void openClassSchedule(Integer placeId) {

        if (mWorkoutListFragment == null) {
            mWorkoutListFragment = new WorkoutListFragment();
        }

        Bundle args = new Bundle();
        args.putInt(PLACE_ID, placeId);

        mWorkoutListFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mWorkoutListFragment, WORKOUT_FRAGMENT).addToBackStack(PLACE_FRAGMENT).commit();

    }
}
