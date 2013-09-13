package co.tapfit.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_workout_list, null);

        mPlace = dbWrapper.getPlace(getArguments().getInt(PlaceInfoActivity.PLACE_ID, -1));

        setUpWorkoutList();

        return mView;
    }

    private void setUpWorkoutList() {

        mWorkoutList = (ListView) mView.findViewById(R.id.workout_list);

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

    }
}
