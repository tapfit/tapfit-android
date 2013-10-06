package co.tapfit.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import co.tapfit.android.fragment.ConfirmPurchaseFragment;
import co.tapfit.android.fragment.WorkoutCardFragment;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.Workout;

public class ConfirmPurchaseActivity extends BaseActivity {

    Place mPlace;
    Workout mWorkout;

    WorkoutCardFragment mWorkoutCardFragment;
    ConfirmPurchaseFragment mConfirmPurchaseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        mPlace = dbWrapper.getPlace(getIntent().getIntExtra(PlaceInfoActivity.PLACE_ID, -1));
        mWorkout = dbWrapper.getWorkout(getIntent().getIntExtra(ConfirmPurchaseFragment.WORKOUT_ID, -1));

        setUpFragments();

        setupActionBar();
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mPlace.name);
    }


    private void setUpFragments() {
        mWorkoutCardFragment = new WorkoutCardFragment();

        Bundle args = new Bundle();
        args.putInt(PlaceInfoActivity.PLACE_ID, mPlace.id);
        args.putInt(ConfirmPurchaseFragment.WORKOUT_ID, mWorkout.id);

        mWorkoutCardFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mWorkoutCardFragment).commit();
    }

    public void confirmPurchasePage(Workout workout) {

        if (mConfirmPurchaseFragment == null) {
            mConfirmPurchaseFragment = new ConfirmPurchaseFragment();
        }

        Bundle args = new Bundle();
        args.putInt(WorkoutCardFragment.WORKOUT_ID, workout.id);
        args.putInt(PlaceInfoActivity.PLACE_ID, mPlace.id);

        mConfirmPurchaseFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mConfirmPurchaseFragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.confirm_purchase, menu);
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
    
}
