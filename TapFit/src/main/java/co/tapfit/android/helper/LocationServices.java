package co.tapfit.android.helper;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by zackmartinsek on 9/10/13.
 */
public class LocationServices implements LocationListener {

    private LocationManager mLocationManager;
    private String mProvider;
    private static Location mLocation;

    private static LocationServices ourInstance;

    public static LocationServices getInstance(Context context) {
        if (ourInstance == null)
        {
            ourInstance = new LocationServices(context);
        }
        return ourInstance;
    }

    private LocationServices(Context context) {

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, false);
        if (mProvider != null)
        {
            mLocation = mLocationManager.getLastKnownLocation(mProvider);

            if (mLocation != null)
            {
                onLocationChanged(mLocation);
            }
            else
            {
                //TODO: leave mLocation null and handle with Regions call
            }
        }
        else
        {

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
    }

    public static LatLng getLatLng()
    {
        if (mLocation == null)
        {
            //TODO: Don't do this
            return new LatLng(39.110874, -84.5157);
        }
        else
        {
            return new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        }
    }
}
