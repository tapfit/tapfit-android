package co.tapfit.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.tapfit.android.R;
import co.tapfit.android.helper.WorkoutFormat;
import co.tapfit.android.model.Workout;

/**
 * Created by zackmartinsek on 9/12/13.
 */
public class WorkoutAdapter extends BaseAdapter {

    private static final String TAG = WorkoutAdapter.class.getSimpleName();

    private ArrayList<Workout> mWorkouts = new ArrayList<Workout>();
    private Context mContext;
    private LayoutInflater mInflater;

    public WorkoutAdapter(Context context, List<Workout> workouts) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mWorkouts = new ArrayList<Workout>(workouts);

        Collections.sort(mWorkouts);


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

        ViewHolder holder = new ViewHolder();
        if (view == null) {
            view = mInflater.inflate(R.layout.workout_list_item, null);

            holder.workout_name = (TextView) view.findViewById(R.id.workout_name);
            holder.workout_time = (TextView) view.findViewById(R.id.workout_time);
            holder.workout_instructor = (TextView) view.findViewById(R.id.workout_instructor);
            holder.workout_price = (TextView) view.findViewById(R.id.workout_price);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Workout workout = mWorkouts.get(i);

        holder.workout_name.setText(workout.name);
        holder.workout_instructor.setText(WorkoutFormat.getInstructor(workout.instructor));
        holder.workout_price.setText("$" + Math.round(workout.price));

        String timeString = WorkoutFormat.getStartEndDateTime(workout.start_time, workout.end_time);
        holder.workout_time.setText(timeString);

        return view;
    }

    private class ViewHolder {

        public TextView workout_name;
        public TextView workout_price;
        public TextView workout_time;
        public TextView workout_instructor;
    }
}
