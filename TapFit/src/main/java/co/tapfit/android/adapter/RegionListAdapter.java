package co.tapfit.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.tapfit.android.R;
import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.ImageCache;
import co.tapfit.android.model.Region;

/**
 * Created by zackmartinsek on 9/19/13.
 */
public class RegionListAdapter extends BaseAdapter {

    private static final String TAG = RegionListAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private ArrayList<Region> mRegions = new ArrayList<Region>();
    public static Region CURRENT_LOCATION = new Region(-1);
    public static Region HEADER = new Region(-2);
    private int mResource = R.layout.pass_list_item;
    private Context mContext;
    private DatabaseWrapper dbWrapper;

    public RegionListAdapter(Context context, List<Region> regions) {
        super();

        mRegions = new ArrayList<Region>(regions);
        mRegions.add(0, HEADER);
        mRegions.add(0, CURRENT_LOCATION);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbWrapper = DatabaseWrapper.getInstance(mContext.getApplicationContext());
    }

    @Override
    public int getCount() {
        return mRegions.size();
    }

    @Override
    public Object getItem(int i) {
        return mRegions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Region region = mRegions.get(i);

        if (region == HEADER) {
            view = mInflater.inflate(R.layout.region_header_item, null);
        }
        else
        {
            view = mInflater.inflate(R.layout.region_list_item, null);
            if (region == CURRENT_LOCATION){
                ((TextView) view.findViewById(R.id.region_name)).setText("Current Location");
            }
            else
            {
                ((TextView) view.findViewById(R.id.region_name)).setText(region.name);
            }
            view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.normal_button_size)));
        }

        return view;
    }
}
