package co.tapfit.android.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import co.tapfit.android.helper.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import co.tapfit.android.PlaceInfoActivity;
import co.tapfit.android.R;
import co.tapfit.android.SignInActivity;
import co.tapfit.android.helper.WorkoutFormat;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;

/**
 * Created by zackmartinsek on 9/12/13.
 */
public class WorkoutCardFragment extends BaseFragment {

    private static final String TAG = WorkoutCardFragment.class.getSimpleName();
    public static final String WORKOUT_ID = "workout_id";
    private View mView;

    private Workout mWorkout;
    private Place mPlace;

    private FrameLayout mBuyButton;

    @Override
    public void onResume() {
        super.onResume();

        mWorkout = dbWrapper.getWorkout(getArguments().getInt(WORKOUT_ID, -1));

        mPlace = dbWrapper.getPlace(getArguments().getInt(PlaceInfoActivity.PLACE_ID, -1));

        HashMap<String, String> args = new HashMap<String, String>();
        args.put("Workout", String.valueOf(mWorkout.id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_workout_card, null);

        mWorkout = dbWrapper.getWorkout(getArguments().getInt(WORKOUT_ID, -1));

        mPlace = dbWrapper.getPlace(getArguments().getInt(PlaceInfoActivity.PLACE_ID, -1));

        setUpWorkoutCard();

        return mView;
    }

    private void setUpWorkoutCard() {

        ImageView imageView = (ImageView) mView.findViewById(R.id.place_image);
        imageCache.loadImageForPlacePage(imageView, mPlace.cover_photo);

        TextView textView = (TextView) mView.findViewById(R.id.workout_name);
        textView.setText(mWorkout.name);

        textView = (TextView) mView.findViewById(R.id.workout_time);
        textView.setText(WorkoutFormat.getStartEndDateTime(mWorkout.start_time, mWorkout.end_time));

        textView = (TextView) mView.findViewById(R.id.workout_place_name);
        textView.setText(mPlace.name);

        textView = (TextView) mView.findViewById(R.id.workout_instructor);
        textView.setText(WorkoutFormat.getInstructor(mWorkout.instructor));

        textView = (TextView) mView.findViewById(R.id.workout_description);
        if (mWorkout.source_description == null)
        {
            textView.setText(R.string.no_description);
        }
        else
        {
            textView.setText(mWorkout.source_description);
        }

        textView = (TextView) mView.findViewById(R.id.fine_print_text);
        textView.setText(mWorkout.fine_print);

        mBuyButton = (FrameLayout) mView.findViewById(R.id.bottom_button);
        mBuyButton.setOnClickListener(confirmPurchase);
    }

    View.OnClickListener confirmPurchase = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            User user = dbWrapper.getCurrentUser();

            HashMap<String, String> args = new HashMap<String, String>();
            args.put("Workout", String.valueOf(mWorkout.id));

            if (user != null)
            {
                args.put("User", String.valueOf(user.id));
                ((PlaceInfoActivity) getActivity()).confirmPurchasePage(mWorkout);
            }
            else
            {
                args.put("User", "Signed Out");
                Intent loginActivity = new Intent(getActivity(), SignInActivity.class);
                startActivityForResult(loginActivity, 1);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == getActivity().RESULT_OK){
                ((PlaceInfoActivity) getActivity()).confirmPurchasePage(mWorkout);
            }
            if (resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Need to Sign up to buy", 1000).show();
            }
        }
    }
}
