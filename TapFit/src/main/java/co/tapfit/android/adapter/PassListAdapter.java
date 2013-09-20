package co.tapfit.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import co.tapfit.android.R;
import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.RoundedBackgroundDisplayer;
import co.tapfit.android.model.Instructor;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.Workout;

/**
 * Created by zackmartinsek on 9/18/13.
 */
public class PassListAdapter extends BaseAdapter {

    private static final String TAG = PassListAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private ArrayList<Pass> mPasses = new ArrayList<Pass>();
    private int mResource = R.layout.pass_list_item;
    private Context mContext;
    private DatabaseWrapper dbWrapper;

    public PassListAdapter(Context context, List<Pass> passes) {
        super();

        mPasses = new ArrayList<Pass>(passes);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbWrapper = DatabaseWrapper.getInstance(mContext.getApplicationContext());
    }



    @Override
    public int getCount() {
        return mPasses.size();
    }

    @Override
    public Object getItem(int i) {
        return mPasses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if (view == null) {
            view = mInflater.inflate(mResource, null);

            holder = new ViewHolder(view);

            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        Pass pass = mPasses.get(i);

        Place place = dbWrapper.getPlace(pass.place.id);
        Workout workout = dbWrapper.getWorkout(pass.workout.id);
        Instructor instructor = dbWrapper.getInstructor(workout.instructor.id);

        holder.place_name.setText(place.name);
        if (workout.is_day_pass) {
            DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("MMM d");
            DateTimeFormatter hourFormatter = DateTimeFormat.forPattern("h:mma");

            String time = dayFormatter.print(workout.start_time) + " from " + hourFormatter.print(workout.start_time) + " - " + hourFormatter.print(workout.end_time);

            holder.workout_time.setText(time);
        }
        else
        {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM d 'at' h:mma");
            holder.workout_time.setText(formatter.print(workout.start_time));
        }

        String workoutName = workout.name;

        if (instructor != null) {
            workoutName = workoutName + " with " + instructor.getInstructorName();
        }

        holder.workout_name.setText(workoutName);

        return view;
    }

    private class ViewHolder {

        public ViewHolder(View view){

            place_name = (TextView) view.findViewById(R.id.place_name);
            workout_time = (TextView) view.findViewById(R.id.workout_time);
            workout_name = (TextView) view.findViewById(R.id.workout_name);
        }

        public TextView place_name;
        public TextView workout_time;
        public TextView workout_name;
    }
}
