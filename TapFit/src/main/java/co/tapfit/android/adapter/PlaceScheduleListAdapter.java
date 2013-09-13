package co.tapfit.android.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.tapfit.android.R;
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
    private Workout PROGRESS_BAR = new Workout(-2);

    private ArrayList<Workout> mWorkouts = new ArrayList<Workout>();

    public PlaceScheduleListAdapter(Context context, List<Workout> workouts) {
        super();

        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setWorkoutList(workouts);
    }


    public void updateWorkouts(List<Workout> upcomingWorkouts) {

        setWorkoutList(upcomingWorkouts);
        mWorkouts.remove(PROGRESS_BAR);
        notifyDataSetChanged();
    }

    private void setWorkoutList(List<Workout> workouts) {
        mWorkouts = new ArrayList<Workout>(workouts);

        Collections.sort(mWorkouts);

        if (mWorkouts.size() > MAX_CLASSES) {
            mWorkouts = new ArrayList<Workout>(mWorkouts.subList(0, MAX_CLASSES));
        }

        if (mWorkouts.size() == 0) {
            mWorkouts.add(PROGRESS_BAR);
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
            Log.d(TAG, "Inflating Bottom bar");
            view = mInflater.inflate(R.layout.button_navigation, null);
        }
        else if (workout.equals(PROGRESS_BAR)){
            Log.d(TAG, "Inflating Progress bar");
            view = mInflater.inflate(R.layout.class_stub_progress, null);
        }
        else
        {
            Log.d(TAG, "Inflating class name");
            view = mInflater.inflate(R.layout.class_stub_item, null);

            TextView className = (TextView) view.findViewById(R.id.class_name);
            className.setText(workout.name);

            TextView classTime = (TextView) view.findViewById(R.id.class_time);

            DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
                    .appendClockhourOfHalfday(1)
                    .appendLiteral(":")
                    .appendMinuteOfHour(2)
                    .appendHalfdayOfDayText()
                    .toFormatter();


            String start = dateFormatter.print(workout.start_time);
            String end = dateFormatter.print(workout.end_time);

            start = start.substring(0, start.length() - 1).toLowerCase();
            end = end.substring(0, end.length() - 1).toLowerCase();

            String timeString = start + " - " + end;
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
