package co.tapfit.android.fragment;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.tapfit.android.PlaceInfoActivity;
import co.tapfit.android.R;
import co.tapfit.android.adapter.WorkoutAdapter;
import co.tapfit.android.adapter.WorkoutListAdapter;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.Workout;

/**
 * Created by zackmartinsek on 9/12/13.
 */
public class WorkoutListFragment extends BaseFragment {

    private View mView;
    private Place mPlace;
    private ListView mWorkoutList;
    private WorkoutListAdapter mWorkoutListAdapter;

    private TextView mNoWorkoutText;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_workout_list, null);

        mPlace = dbWrapper.getPlace(getArguments().getInt(PlaceInfoActivity.PLACE_ID, -1));

        if (((PlaceInfoActivity) getActivity()).WORKOUT_CALLBACK_RECEIVED) {
            setUpWorkoutList();
        }
        else
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading Workouts...");
            progressDialog.show();
        }

        return mView;
    }

    private void setUpWorkoutList() {

        mWorkoutList = (ListView) mView.findViewById(R.id.workout_list);

        mNoWorkoutText = (TextView) mView.findViewById(R.id.no_workout_text);

        mNoWorkoutText.setGravity(Gravity.CENTER);

        mWorkoutListAdapter = new WorkoutListAdapter(getActivity());

        List<Workout> workouts = dbWrapper.getUpcomingWorkouts(mPlace);

        Map<String, ArrayList<Workout>> sections = new HashMap<String, ArrayList<Workout>>();

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendDayOfWeekText()
                .appendLiteral(", ")
                .appendMonthOfYearText()
                .appendLiteral(" ")
                .appendDayOfMonth(1)
                .toFormatter();


        Collections.sort(workouts);

        Collections.reverse(workouts);

        for (Workout workout : workouts) {

            String dateString = formatter.print(workout.start_time);

            if (!sections.containsKey(dateString))
            {
                sections.put(dateString, new ArrayList<Workout>());
                sections.get(dateString).add(workout);
            }
            else
            {
                sections.get(dateString).add(workout);
            }
        }

        for (String dateString : sections.keySet()) {
            mWorkoutListAdapter.addSection(dateString, new WorkoutAdapter(getActivity(), sections.get(dateString)));
        }

        mWorkoutList.setAdapter(mWorkoutListAdapter);

        mWorkoutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Workout workout = (Workout) adapterView.getItemAtPosition(i);
                ((PlaceInfoActivity) getActivity()).openWorkoutCardFromList(workout.id);

            }
        });

        if (mWorkoutListAdapter.getCount() < 1) {
            mWorkoutList.setVisibility(View.GONE);
            mNoWorkoutText.setVisibility(View.VISIBLE);
        }
        else
        {
            mWorkoutList.setVisibility(View.VISIBLE);
            mNoWorkoutText.setVisibility(View.GONE);
        }

    }

    public void receivedWorkouts()
    {
        setUpWorkoutList();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }
}
