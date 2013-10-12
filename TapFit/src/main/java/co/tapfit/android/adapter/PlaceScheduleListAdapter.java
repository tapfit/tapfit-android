package co.tapfit.android.adapter;

import android.app.ActionBar;
import android.content.Context;
import co.tapfit.android.helper.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import co.tapfit.android.R;
import co.tapfit.android.helper.WorkoutFormat;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.Workout;

/**
 * Created by zackmartinsek on 9/12/13.
 */
public class PlaceScheduleListAdapter extends BaseAdapter {

    private static final int MAX_CLASSES = 2;
    private Context mContext;
    private LayoutInflater mInflater;

    private static final String TAG = PlaceScheduleListAdapter.class.getSimpleName();

    public static Workout BOTTOM_BAR = new Workout(-1);
    public static Workout NO_WORKOUTS_TODAY = new Workout(-3);
    private Workout PROGRESS_BAR = new Workout(-2);

    private Boolean mNoMoreWorkouts = false;

    private ArrayList<Workout> mWorkouts = new ArrayList<Workout>();

    private Place mPlace;

    public PlaceScheduleListAdapter(Context context, List<Workout> workouts, Place place) {
        super();

        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPlace = place;

        setWorkoutList(workouts);
    }


    public void updateWorkouts(List<Workout> upcomingWorkouts) {

        setWorkoutList(upcomingWorkouts);
        if (upcomingWorkouts.size() < 1){
            mNoMoreWorkouts = true;
            mWorkouts.remove(NO_WORKOUTS_TODAY);
        }
        else if (mWorkouts.contains(PROGRESS_BAR)) {
            mWorkouts.clear();
            mWorkouts.add(NO_WORKOUTS_TODAY);
            mWorkouts.add(BOTTOM_BAR);
        }
        Log.d(TAG, "Removing Progress Bar");
        mWorkouts.remove(PROGRESS_BAR);
        notifyDataSetChanged();
    }

    private void setWorkoutList(List<Workout> workouts) {
        if (workouts == null){
            mWorkouts = new ArrayList<Workout>();
        }
        else if (workouts.size() > 0)
        {
            mWorkouts = new ArrayList<Workout>(workouts);
        }
        else
        {
            mWorkouts = new ArrayList<Workout>();
        }

        Iterator<Workout> iterator = mWorkouts.iterator();

        DateTime date = new DateTime().toDateMidnight().toDateTime();
        DateTime tomorrow = date.plusDays(1);

        while (iterator.hasNext()) {
            if (iterator.next().start_time.isAfter(tomorrow)) {
                iterator.remove();
            }
        }

        Collections.sort(mWorkouts);

        /*if (mWorkouts.size() > MAX_CLASSES) {
            mWorkouts = new ArrayList<Workout>(mWorkouts.subList(0, MAX_CLASSES));
        }*/


        if (mWorkouts.size() == 0) {
            mWorkouts.add(PROGRESS_BAR);
            //mWorkouts.add(NO_WORKOUTS_TODAY);
        }

        mWorkouts.add(BOTTOM_BAR);
    }

    @Override
    public int getCount() {
        return mWorkouts.size();
    }

    @Override
    public Object getItem(int i) {
        return mWorkouts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Workout workout = mWorkouts.get(i);
        if (workout.equals(BOTTOM_BAR)) {
            view = mInflater.inflate(R.layout.button_navigation, null);
            TextView header = (TextView) view.findViewById(R.id.content_title);
            if (mPlace.facility_type == null || mPlace.facility_type == 0)
            {
                if (mNoMoreWorkouts){
                    header.setText("No Schedule Available");
                    view.findViewById(R.id.carrot).setVisibility(View.GONE);
                    view.setTag(false);
                }
                else {
                    header.setText("View full schedule");
                    view.findViewById(R.id.carrot).setVisibility(View.VISIBLE);
                    view.setTag(true);
                }
            }
            else
            {
                header.setText("Days passes available");
            }
        }
        else if (workout.equals(PROGRESS_BAR)){
            view = mInflater.inflate(R.layout.class_stub_progress, null);

            ProgressBar bar = (ProgressBar) view.findViewById(R.id.progress_bar);
            bar.setIndeterminate(true);
            bar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.anim.spinning_logo_animation));
        }
        else if (workout.equals(NO_WORKOUTS_TODAY)) {
            view = mInflater.inflate(R.layout.class_stub_item, null);
            TextView textView = (TextView) view.findViewById(R.id.class_time);
            textView.setVisibility(View.GONE);

            textView = (TextView) view.findViewById(R.id.class_name);
            textView.setText("No more workouts today");
        }
        else
        {
            Log.d(TAG, "Inflating class name");
            view = mInflater.inflate(R.layout.class_stub_item, null);

            TextView className = (TextView) view.findViewById(R.id.class_name);
            className.setText(workout.name);

            TextView classTime = (TextView) view.findViewById(R.id.class_time);

            String timeString = WorkoutFormat.getStartEndDateTime(workout.start_time, workout.end_time);
            classTime.setText(timeString);
        }

        if (view != null) {
            view.setLayoutParams(new AbsListView.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.normal_button_size)));
            int padding = (int)mContext.getResources().getDimension(R.dimen.padding);
            view.setPadding(padding, 0, padding, 0);
        }

        return view;
    }
}
