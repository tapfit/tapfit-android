package co.tapfit.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;

import java.util.ArrayList;
import java.util.List;

import co.tapfit.android.R;
import co.tapfit.android.helper.CroppedBitmapDisplayer;
import co.tapfit.android.helper.ImageCache;
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
    private Display mDisplay;

    private static final String TAG = PlaceListAdapter.class.getSimpleName();

    public PlaceListAdapter(Context context, List<Place> places) {
        super();

        mPlaceData = new ArrayList<Place>(places);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageScaleType ist = ImageScaleType.EXACTLY_STRETCHED;

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();

        mOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ist)
                .displayer(new RoundedBitmapDisplayer(20))
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

        if (holder == null)
            Log.d(TAG, "holder is null");

        if (view == null)
            Log.d(TAG, "View is null");

        Place place = (Place) getItem(i);

        mImageLoader.displayImage(place.cover_photo, holder.place_image, mOptions);

        return view;
    }

    private class ViewHolder {

        public ViewHolder(View view)
        {
            place_image = (ImageView) view.findViewById(R.id.place_image);
        }

        public ImageView place_image;

    }
}
