package co.tapfit.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import co.tapfit.android.R;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.Workout;

/**
 * Created by zackmartinsek on 9/18/13.
 */
public class PassFragment extends BaseFragment {

    private static final String TAG = PassFragment.class.getSimpleName();

    private View mView;

    public static final String PASS_ID = "pass_id";

    private Pass mPass;
    private Workout mWorkout;
    private Place mPlace;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern("EEEE, MMMM d 'at' h:mma");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_pass, null);

        mPass = dbWrapper.getPass(getArguments().getInt(PASS_ID));

        mWorkout = dbWrapper.getWorkout(mPass.workout.id);

        mPlace = dbWrapper.getPlace(mPass.place.id);

        setUpPassViews();

        return mView;
    }

    private void setUpPassViews() {

        TextView text = (TextView) mView.findViewById(R.id.workout_name);
        text.setText(mWorkout.name);

        text = (TextView) mView.findViewById(R.id.workout_time);
        text.setText(formatter.print(mWorkout.start_time));

        text = (TextView) mView.findViewById(R.id.workout_place);
        text.setText(mPlace.name);

        text = (TextView) mView.findViewById(R.id.workout_instructor);
        text.setText(mWorkout.instructor.first_name + " " + mWorkout.instructor.last_name);

        text = (TextView) mView.findViewById(R.id.instructions);
        text.setText(mPass.instructions);

        text = (TextView) mView.findViewById(R.id.fine_print);
        text.setText(mPass.fine_print);
    }
}
