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
import android.widget.Toast;

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
        mPlace = dbWrapper.getPlace(getIntent().getIntExtra(PLACE_ID, -1));

        setupActionBar();

        //setUpFragments();

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
        getSupportActionBar().setTitle(mPlace.name);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Bundle args = new Bundle();
        args.putInt(PLACE_ID, mPlace.id);

        ActionBar.Tab scheduleTab = actionBar.newTab().setText("Passes").setTabListener(new TabListener<WorkoutListFragment>(this, WORKOUT_LIST_FRAGMENT, WorkoutListFragment.class, args));


        ActionBar.Tab infoTab = actionBar.newTab().setText("Info").setTabListener(new TabListener<PlaceCardFragment>(this, "PlaceCard", PlaceCardFragment.class, args));

        actionBar.addTab(scheduleTab);
        actionBar.addTab(infoTab);
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
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(WORKOUT_LIST_FRAGMENT);

                if (fragment != null && fragment instanceof WorkoutListFragment) {
                    WorkoutListFragment workoutListFragment = (WorkoutListFragment) fragment;
                    workoutListFragment.receivedWorkouts();
                }
            }
            else
            {

            }
        }
    };

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final ActionBarActivity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(ActionBarActivity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(ActionBarActivity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
            }
            ft.replace(android.R.id.content, mFragment, mTag);
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            //Toast.makeText(mActivity, "Unselected!", Toast.LENGTH_SHORT).show();
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            //Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
        }
    }
}
