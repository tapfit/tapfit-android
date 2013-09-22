package co.tapfit.android.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.venmo.touch.view.SingleLineCardEntryView;

import java.util.Timer;
import java.util.TimerTask;

import co.tapfit.android.FirstUseActivity;
import co.tapfit.android.MapListActivity;
import co.tapfit.android.R;
import co.tapfit.android.helper.BraintreePayments;

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

    private Button mButton;

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

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    ((FirstUseActivity) getActivity()).endFirstUse();
                }
            }, 3000);

        }
    };

    private void getToggleButtons() {

        mMorning = (ToggleButton) mView.findViewById(R.id.morning_toggle);
        mAfternoon = (ToggleButton) mView.findViewById(R.id.afternoon_toggle);
        mEvening = (ToggleButton) mView.findViewById(R.id.evening_toggle);
        mYoga = (ToggleButton) mView.findViewById(R.id.yoga_toggle);
        mPilates = (ToggleButton) mView.findViewById(R.id.pilates_toggle);
        mSpin = (ToggleButton) mView.findViewById(R.id.cardio_toggle);
        mDance = (ToggleButton) mView.findViewById(R.id.dance_toggle);
        mWeights = (ToggleButton) mView.findViewById(R.id.weights_toggle);
        mCrossfit = (ToggleButton) mView.findViewById(R.id.crossfit_toggle);

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
        }
    }

    private void changeToggleButtonTextColor(ToggleButton button) {
        button.setTextColor(getResources().getColor(R.color.black));
    }
}
