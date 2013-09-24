package co.tapfit.android;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import co.tapfit.android.fragment.ConfirmPurchaseFragment;
import co.tapfit.android.fragment.PassFragment;
import co.tapfit.android.fragment.PlaceCardFragment;
import co.tapfit.android.fragment.WorkoutCardFragment;
import co.tapfit.android.fragment.WorkoutListFragment;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.Workout;
import co.tapfit.android.request.PlaceRequest;
import co.tapfit.android.request.ResponseCallback;

public class PlaceInfoActivity extends BaseActivity {

    public static final String PLACE_ID = "place_id";
    private static final String PLACE_FRAGMENT = "place_fragment";
    private static final String WORKOUT_LIST_FRAGMENT = "workout_fragment";
    private static final String WORKOUT_CARD_FRAGMENT = "workout_card_fragment";
    private static final String CONFIRM_PURCHASE_FRAGMENT = "confirm_purchase_fragment";
    private static final String PASS_FRAGMENT = "pass_fragment";
    private Place mPlace;

    private PlaceCardFragment mPlaceCardFragment;
    private WorkoutListFragment mWorkoutListFragment;
    private WorkoutCardFragment mWorkoutCardFragment;
    private ConfirmPurchaseFragment mConfirmPurchaseFragment;
    private PassFragment mPassFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        // Show the Up button in the action bar.
        setupActionBar();

        mPlace = dbWrapper.getPlace(getIntent().getIntExtra(PLACE_ID, -1));

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

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mWorkoutListFragment, WORKOUT_LIST_FRAGMENT).addToBackStack(PLACE_FRAGMENT).commit();

    }

    public void openWorkoutCardFromList(Integer workoutId) {

        if (mWorkoutCardFragment == null) {
            mWorkoutCardFragment = new WorkoutCardFragment();
        }

        Bundle args = new Bundle();
        args.putInt(WorkoutCardFragment.WORKOUT_ID, workoutId);
        args.putInt(PLACE_ID, mPlace.id);

        mWorkoutCardFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mWorkoutCardFragment, WORKOUT_CARD_FRAGMENT).addToBackStack(WORKOUT_LIST_FRAGMENT).commit();
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
}
