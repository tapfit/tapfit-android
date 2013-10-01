package co.tapfit.android.fragment;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import co.tapfit.android.FirstUseActivity;
import co.tapfit.android.MapListActivity;
import co.tapfit.android.R;
import co.tapfit.android.helper.Search;
import co.tapfit.android.helper.SharePref;

/**
 * Created by zackmartinsek on 9/20/13.
 */
public class PreferencesFragment extends BaseFragment {

    private View mView;

    private static final String TAG = PreferencesFragment.class.getSimpleName();

    private ToggleButton mMorning;
    private ToggleButton mAfternoon;
    private ToggleButton mEvening;

    private ToggleButton mYoga;
    private ToggleButton mPilates;
    private ToggleButton mSpin;
    private ToggleButton mCrossfit;
    private ToggleButton mDance;
    private ToggleButton mWeights;

    private ToggleButton mLowPrice;
    private ToggleButton mMedPrice;
    private ToggleButton mHighPrice;

    private Button mButton;

    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_select_time, null);

        getToggleButtons();

        mButton = (Button) mView.findViewById(R.id.bottom_button);
        mButton.setOnClickListener(savePreferences);

        return mView;
    }

    View.OnClickListener savePreferences = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Finding your workouts...");
            progressDialog.show();

            savePreferencesToShared();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog.cancel();
                            getActivity().onBackPressed();
                        }
                    });
                }
            }, 3000);



        }
    };

    private void savePreferencesToShared() {

        SharePref.setBooleanPref(getActivity(), Search.TIME_MORNING, mMorning.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.TIME_AFTERNOON, mAfternoon.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.TIME_EVENING, mEvening.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.WORKOUT_CROSSFIT, mCrossfit.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.WORKOUT_DANCE, mDance.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.WORKOUT_PILATES, mPilates.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.WORKOUT_SPIN, mSpin.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.WORKOUT_WEIGHTS, mWeights.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.WORKOUT_YOGA, mYoga.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.PRICE_LOW, mLowPrice.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.PRICE_MED, mMedPrice.isChecked());
        SharePref.setBooleanPref(getActivity(), Search.PRICE_HIGH, mHighPrice.isChecked());

    }

    private void getToggleButtons() {

        mMorning = (ToggleButton) mView.findViewById(R.id.morning_toggle);
        mMorning.setChecked(SharePref.getBooleanPref(getActivity(), Search.TIME_MORNING, false));
        mAfternoon = (ToggleButton) mView.findViewById(R.id.afternoon_toggle);
        mAfternoon.setChecked(SharePref.getBooleanPref(getActivity(), Search.TIME_AFTERNOON, false));
        mEvening = (ToggleButton) mView.findViewById(R.id.evening_toggle);
        mEvening.setChecked(SharePref.getBooleanPref(getActivity(), Search.TIME_EVENING, false));
        mYoga = (ToggleButton) mView.findViewById(R.id.yoga_toggle);
        mYoga.setChecked(SharePref.getBooleanPref(getActivity(), Search.WORKOUT_YOGA, false));
        mPilates = (ToggleButton) mView.findViewById(R.id.pilates_toggle);
        mPilates.setChecked(SharePref.getBooleanPref(getActivity(), Search.WORKOUT_PILATES, false));
        mSpin = (ToggleButton) mView.findViewById(R.id.cardio_toggle);
        mSpin.setChecked(SharePref.getBooleanPref(getActivity(), Search.WORKOUT_SPIN, false));
        mDance = (ToggleButton) mView.findViewById(R.id.dance_toggle);
        mDance.setChecked(SharePref.getBooleanPref(getActivity(), Search.WORKOUT_DANCE, false));
        mWeights = (ToggleButton) mView.findViewById(R.id.weights_toggle);
        mWeights.setChecked(SharePref.getBooleanPref(getActivity(), Search.WORKOUT_WEIGHTS, false));
        mCrossfit = (ToggleButton) mView.findViewById(R.id.crossfit_toggle);
        mCrossfit.setChecked(SharePref.getBooleanPref(getActivity(), Search.WORKOUT_CROSSFIT, false));
        mLowPrice = (ToggleButton) mView.findViewById(R.id.low_price_toggle);
        mLowPrice.setChecked(SharePref.getBooleanPref(getActivity(), Search.PRICE_LOW, false));
        mMedPrice = (ToggleButton) mView.findViewById(R.id.med_price_toggle);
        mMedPrice.setChecked(SharePref.getBooleanPref(getActivity(), Search.PRICE_MED, false));
        mHighPrice = (ToggleButton) mView.findViewById(R.id.high_price_toggle);
        mHighPrice.setChecked(SharePref.getBooleanPref(getActivity(), Search.PRICE_HIGH, false));

        if (Build.VERSION.SDK_INT < 11) {
            changeToggleButtonTextColor(mMorning);
            changeToggleButtonTextColor(mAfternoon);
            changeToggleButtonTextColor(mEvening);
            changeToggleButtonTextColor(mYoga);
            changeToggleButtonTextColor(mPilates);
            changeToggleButtonTextColor(mSpin);
            changeToggleButtonTextColor(mDance);
            changeToggleButtonTextColor(mWeights);
            changeToggleButtonTextColor(mCrossfit);
            changeToggleButtonTextColor(mLowPrice);
            changeToggleButtonTextColor(mMedPrice);
            changeToggleButtonTextColor(mHighPrice);
        }

        setUpPriceToggle();
    }

    private void setUpPriceToggle() {
        mLowPrice.setOnClickListener(lowPriceClicked);
        mMedPrice.setOnClickListener(medPriceClicked);
        mHighPrice.setOnClickListener(highPriceClicked);

        if (SharePref.getBooleanPref(getActivity(), Search.PRICE_LOW, false))
        {
            mLowPrice.setChecked(true);
            mMedPrice.setChecked(false);
            mHighPrice.setChecked(false);
        }
        else if (SharePref.getBooleanPref(getActivity(), Search.PRICE_MED, false))
        {
            mLowPrice.setChecked(false);
            mMedPrice.setChecked(true);
            mHighPrice.setChecked(false);
        }
        else
        {
            mLowPrice.setChecked(false);
            mMedPrice.setChecked(false);
            mHighPrice.setChecked(true);
        }
    }

    private View.OnClickListener lowPriceClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMedPrice.setChecked(false);
            mHighPrice.setChecked(false);
        }
    };

    private View.OnClickListener medPriceClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mLowPrice.setChecked(false);
            mHighPrice.setChecked(false);
        }
    };

    private View.OnClickListener highPriceClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMedPrice.setChecked(false);
            mLowPrice.setChecked(false);
        }
    };

    private void changeToggleButtonTextColor(ToggleButton button) {
        button.setTextColor(getResources().getColor(R.color.black));
    }
}
