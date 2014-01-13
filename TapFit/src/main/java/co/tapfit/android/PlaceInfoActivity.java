package co.tapfit.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import co.tapfit.android.fragment.ConfirmPurchaseFragment;
import co.tapfit.android.fragment.PassFragment;
import co.tapfit.android.fragment.PlaceCardFragment;
import co.tapfit.android.fragment.PlaceCardMoreFragment;
import co.tapfit.android.fragment.WorkoutCardFragment;
import co.tapfit.android.fragment.WorkoutListFragment;
import co.tapfit.android.helper.Log;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;
import co.tapfit.android.request.PlaceRequest;
import co.tapfit.android.request.ResponseCallback;

public class PlaceInfoActivity extends BaseActivity {

    private static final String TAG = PlaceInfoActivity.class.getSimpleName();

    public static final String PLACE_ID = "place_id";
    private static final String PLACE_FRAGMENT = "place_fragment";
    private static final String WORKOUT_LIST_FRAGMENT = "workout_fragment";
    private static final String WORKOUT_CARD_FRAGMENT = "workout_card_fragment";
    private static final String CONFIRM_PURCHASE_FRAGMENT = "confirm_purchase_fragment";
    private static final String PASS_FRAGMENT = "pass_fragment";
    private Place mPlace;

    private PlaceCardFragment mPlaceCardFragment;
    private WorkoutListFragment mWorkoutListFragment;
    private PlaceCardMoreFragment mPlaceCardMoreFragment;
    private WorkoutCardFragment mWorkoutCardFragment;
    private ConfirmPurchaseFragment mConfirmPurchaseFragment;
    private PassFragment mPassFragment;
    private Boolean toggledFavorite = false;
    private Boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        // Show the Up button in the action bar.
        mPlace = dbWrapper.getPlace(getIntent().getIntExtra(PLACE_ID, -1));

        setupActionBar();

        setUpFragments();

        getWorkoutsFromServer();
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

        if (dbWrapper.getFavorites().contains(mPlace)) {
            isFavorite = true;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mPlace.name);
    }

    public void openWorkoutCardFromList(Integer workoutId) {
        Intent intent = new Intent(this, ConfirmPurchaseActivity.class);
        intent.putExtra(PLACE_ID, mPlace.id);
        intent.putExtra(ConfirmPurchaseFragment.WORKOUT_ID, workoutId);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.place_info, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //Log.d(TAG, "drawOpen: " + drawerOpen);
        //menu.findItem(R.id.action_menu).setVisible(!drawerOpen);
        if (isFavorite) {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.favorite_checked);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_favorite:
                User user = dbWrapper.getCurrentUser();

                if (user == null) {
                    Toast.makeText(PlaceInfoActivity.this, "Want to save your location? Sign up to see them in your favorites!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PlaceInfoActivity.this, SignInActivity.class);
                    startActivityForResult(intent, 1);
                    return true;
                }

                if (isFavorite) {
                    dbWrapper.removePlaceFromFavorites(user, mPlace);
                    isFavorite = false;
                }
                else {
                    dbWrapper.addPlaceToFavorites(user, mPlace);
                    isFavorite = true;
                }
                toggledFavorite = !toggledFavorite;
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showMorePlaceInfo(Integer placeId) {
        if (mPlaceCardMoreFragment == null) {
            mPlaceCardMoreFragment = new PlaceCardMoreFragment();
        }

        Bundle args = new Bundle();
        args.putInt(PLACE_ID, placeId);

        mPlaceCardMoreFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mPlaceCardMoreFragment, "PlaceMoreFragment").addToBackStack(null).commit();
    }

    public void openClassSchedule(Integer placeId) {

        if (mWorkoutListFragment == null) {
            mWorkoutListFragment = new WorkoutListFragment();
        }

        Bundle args = new Bundle();
        args.putInt(PLACE_ID, placeId);

        mWorkoutListFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mWorkoutListFragment, WORKOUT_LIST_FRAGMENT).addToBackStack(PLACE_FRAGMENT).commit();

    }

    public void confirmPurchasePage(Workout workout) {

        if (mConfirmPurchaseFragment == null) {
            mConfirmPurchaseFragment = new ConfirmPurchaseFragment();
        }

        Bundle args = new Bundle();
        args.putInt(WorkoutCardFragment.WORKOUT_ID, workout.id);
        args.putInt(PLACE_ID, mPlace.id);

        mConfirmPurchaseFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mConfirmPurchaseFragment, CONFIRM_PURCHASE_FRAGMENT).addToBackStack(WORKOUT_CARD_FRAGMENT).commit();

    }

    public static Boolean WORKOUT_CALLBACK_RECEIVED = false;

    public void getWorkoutsFromServer() {
        WORKOUT_CALLBACK_RECEIVED = false;
        PlaceRequest.getWorkouts(getApplicationContext(), mPlace, workoutCallback);
    }

    private ResponseCallback workoutCallback = new ResponseCallback() {
        @Override
        public void sendCallback(Object responseObject, String message) {
            WORKOUT_CALLBACK_RECEIVED = true;
            if (responseObject != null)
            {
                if (mWorkoutListFragment != null && mWorkoutListFragment.isResumed()) {
                    mWorkoutListFragment.receivedWorkouts();
                }
                else if (mPlaceCardFragment != null && mPlaceCardFragment.isResumed())
                {
                    mPlaceCardFragment.receivedWorkouts();
                }
            }
            else
            {

            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                User user = dbWrapper.getCurrentUser();
                Log.d(TAG, "Successfully signed in");
                isFavorite = true;
                dbWrapper.addPlaceToFavorites(user, mPlace);
                toggledFavorite = !toggledFavorite;
                supportInvalidateOptionsMenu();
            }
            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Didn't log in");
            }
        }
    }

    @Override
    public void onPause(){
        Log.d(TAG, "toggledFavorite: " + toggledFavorite);
        if (toggledFavorite) {
            Log.d(TAG, "About to favorite a place");
            PlaceRequest.favoritePlace(this, mPlace, dbWrapper.getCurrentUser(), null);
        }
        super.onPause();
    }
}
