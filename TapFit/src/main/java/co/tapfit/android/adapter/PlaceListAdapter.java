package co.tapfit.android.adapter;

import android.content.Context;
import co.tapfit.android.helper.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.tapfit.android.R;
import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.RoundedBackgroundDisplayer;
import co.tapfit.android.helper.WorkoutFormat;
import co.tapfit.android.model.ClassTime;
import co.tapfit.android.model.Place;

/**
 * Created by zackmartinsek on 9/10/13.
 */
public class PlaceListAdapter extends BaseAdapter {

    private DisplayImageOptions mOptions;
    private int mResource = R.layout.place_list_item;
    private Context mContext;
    private ImageLoader mImageLoader;
    private LayoutInflater mInflater;
    private List<Place> mPlaceData;

    private DateFormat df = new SimpleDateFormat("h:mma");

    private static final String TAG = PlaceListAdapter.class.getSimpleName();

    public PlaceListAdapter(Context context, List<Place> places) {
        super();

        mPlaceData = new ArrayList<Place>(places);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageScaleType ist = ImageScaleType.EXACTLY_STRETCHED;

        mOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ist)
                .displayer(new RoundedBackgroundDisplayer(mContext.getResources().getDimension(R.dimen.rounded_edges)))
                .build();

        mImageLoader = ImageLoader.getInstance();
    }


    @Override
    public int getCount() {
        return mPlaceData.size();
    }

    @Override
    public Object getItem(int i) {
        return mPlaceData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null)
        {
            view = mInflater.inflate(mResource, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        Place place = (Place) getItem(i);

        mImageLoader.displayImage(place.cover_photo, holder.place_image, mOptions);
        holder.place_name_text.setText(place.name);
        holder.place_price_text.setText("$" + Math.round(place.lowest_price));
        holder.place_distance_text.setText(String.format("%.1f", place.getDistance()) + " mi");

        List<ClassTime> classTimes = DatabaseWrapper.getInstance(mContext.getApplicationContext()).getClassTimes(place.id);

        int timeCount = 0;

        String classTimeString = "";
        for (ClassTime classTime : classTimes) {
            Log.d(TAG, "place: " + place.name + ", classTime: " + classTime.classTime);
            if (LocalDate.now().getDayOfYear() == classTime.classTime.getDayOfYear()) {
                if ((DateTime.now()).compareTo(classTime.classTime) < 0)
                {
                    classTimeString = classTimeString + WorkoutFormat.getDateTimeString(classTime.classTime) + " ";
                    timeCount++;
                    if (timeCount > 4) {
                        break;
                    }
                }
            }
        }

        Log.d(TAG, "classTime: " + classTimeString);
        if (classTimeString.equals("")) {
            for (ClassTime classTime : classTimes) {
                if (LocalDate.now().getDayOfYear() + 1 == classTime.classTime.getDayOfYear()) {
                    classTimeString = "Tomorrow's next class: " + WorkoutFormat.getDateTimeString(classTime.classTime);
                    break;
                }
            }
        }

        if (classTimeString.equals("")){
            classTimeString = "No Schedule Available";
        }

        if (classTimeString.equals("")) {
            classTimeString = "Dropin passes available";
        }

        holder.place_class_time_text.setText(classTimeString);

        return view;
    }

    private class ViewHolder {

        public ViewHolder(View view)
        {
            place_image = (ImageView) view.findViewById(R.id.place_image);
            place_name_text = (TextView) view.findViewById(R.id.place_name_text);
            place_class_time_text = (TextView) view.findViewById(R.id.place_class_time_text);
            place_price_text = (TextView) view.findViewById(R.id.place_price_text);
            place_distance_text = (TextView) view.findViewById(R.id.place_distance_text);
        }

        public ImageView place_image;
        public TextView place_name_text;
        public TextView place_class_time_text;
        public TextView place_price_text;
        public TextView place_distance_text;

    }
}
