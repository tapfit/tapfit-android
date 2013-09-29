package co.tapfit.android.helper;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zackmartinsek on 9/10/13.
 */
public class LocationServices implements LocationListener {

    private static final Integer mGPSMillisecondsToWait = 30 * 60 * 1000;

    private static LocationManager mLocationManager;
    private String mProvider;
    private static Location mLocation;
    private static Timer gpsTimer;

    private static final String TAG = LocationServices.class.getSimpleName();

    private static LocationServices ourInstance;

    public static LocationServices getInstance(Context context) {
        if (ourInstance == null)
        {
            ourInstance = new LocationServices(context);
        }
        return ourInstance;
    }

    private LocationServices(Context context) {

        gpsTimer = new Timer();

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mLocation = getBestLocation();

    }

    public static void stopRecording() {
        mLocationManager.removeUpdates(ourInstance);
    }

    public static void startRecording() {
        gpsTimer.cancel();
        gpsTimer = new Timer();
        long checkInterval = mGPSMillisecondsToWait;
        long minDistance = 10;
        // receive updates
        for (String s : mLocationManager.getAllProviders()) {
            mLocationManager.requestLocationUpdates(s, checkInterval,
                    minDistance, ourInstance);
        }
        // start the gps receiver thread
        gpsTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Location location = getBestLocation();
                doLocationUpdate(location, false);
            }
        }, 0, checkInterval);
    }

    public static void doLocationUpdate(Location l, boolean force) {
        long minDistance = 10;
        Log.d(TAG, "update received:" + l);
        if (l == null) {
            Log.d(TAG, "Empty location");
            return;
        }
        if (mLocation != null) {
            float distance = l.distanceTo(mLocation);
            Log.d(TAG, "Distance to last: " + distance);
            if (l.distanceTo(mLocation) < minDistance && !force) {
                Log.d(TAG, "Position didn't change");
                return;
            }
            if (l.getAccuracy() >= mLocation.getAccuracy()
                    && l.distanceTo(mLocation) < l.getAccuracy() && !force) {
                Log.d(TAG,
                        "Accuracy got worse and we are still "
                                + "within the accuracy range.. Not updating");
                return;
            }
        }

        mLocation = l;
        // upload/store your location here
    }

    private static Location getBestLocation() {
        Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation =
                getLocationByProvider(LocationManager.NETWORK_PROVIDER);
        // if we have only one location available, the choice is easy
        if (gpslocation == null) {
            Log.d(TAG, "No GPS Location available.");
            return networkLocation;
        }
        if (networkLocation == null) {
            Log.d(TAG, "No Network Location available");
            return gpslocation;
        }
        // a locationupdate is considered 'old' if its older than the configured
        // update interval. this means, we didn't get a
        // update from this provider since the last check
        long old = System.currentTimeMillis() - mGPSMillisecondsToWait;
        boolean gpsIsOld = (gpslocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);
        // gps is current and available, gps is better than network
        if (!gpsIsOld) {
            Log.d(TAG, "Returning current GPS Location");
            return gpslocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d(TAG, "GPS is old, Network is current, returning network");
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            Log.d(TAG, "Both are old, returning gps(newer)");
            return gpslocation;
        } else {
            Log.d(TAG, "Both are old, returning network(newer)");
            return networkLocation;
        }
    }

    /**
     * get the last known location from a specific provider (network/gps)
     */
    private static Location getLocationByProvider(String provider) {
        Location location = null;
        try {
            if (mLocationManager.isProviderEnabled(provider)) {
                location = mLocationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Cannot acces Provider " + provider);
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getProvider().equals(
                LocationManager.GPS_PROVIDER)) {
            doLocationUpdate(location, true);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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
