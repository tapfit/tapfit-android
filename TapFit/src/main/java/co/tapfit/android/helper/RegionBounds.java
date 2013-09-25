package co.tapfit.android.helper;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.model.Region;

/**
 * Created by zackmartinsek on 9/24/13.
 */
public class RegionBounds {

    private static ArrayList<LatLngBounds> mBounds;

    private static RegionBounds ourInstance;

    private static Context mContext;

    private static DatabaseWrapper dbWrapper;

    public static RegionBounds getInstance(Context context) {
        if (ourInstance == null){
            ourInstance = new RegionBounds(context);
        }
        return ourInstance;
    }

    private RegionBounds(Context context) {
        mContext = context;
        dbWrapper = DatabaseWrapper.getInstance(mContext);
    }

    public static ArrayList<LatLngBounds> getRegionBounds() {
        if (mBounds == null) {
            mBounds = new ArrayList<LatLngBounds>();
            for (Region region : dbWrapper.getRegions()) {

                Double latRadius = ((double) region.radius / 2.0) / 69.0;

                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(new LatLng(region.lat - latRadius, region.lon - latRadius))
                        .include(new LatLng(region.lat - latRadius, region.lon + latRadius))
                        .include(new LatLng(region.lat + latRadius, region.lon - latRadius))
                        .include(new LatLng(region.lat + latRadius, region.lon + latRadius))
                        .build();

                mBounds.add(bounds);
            }
        }
        return mBounds;
    }

    public static Boolean isInRegion(LatLng latLng) {
        for (LatLngBounds bound : getRegionBounds()) {
            if (bound.contains(latLng)){
                return true;
            }
        }
        return false;
    }
}
