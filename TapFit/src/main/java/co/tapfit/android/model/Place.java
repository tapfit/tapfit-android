package co.tapfit.android.model;

import android.content.Context;

import com.flurry.android.monolithic.sdk.impl.cl;
import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.helper.Log;

/**
 * Created by zackmartinsek on 9/7/13.
 */
@DatabaseTable(tableName = "places")
public class Place implements Comparable<Place> {

    private static final String TAG = Place.class.getSimpleName();

    public Place()
    {

    }

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField
    public String name;

    @DatabaseField
    public Boolean can_buy;

    @DatabaseField
    public String category;

    @DatabaseField
    public String phone_number;

    @DatabaseField
    public String source_description;

    @DatabaseField
    public Double lowest_price;

    @DatabaseField
    public Integer facility_type;

    @DatabaseField
    public Float distance;

    @DatabaseField
    public String avg_rating;

    @DatabaseField
    public String url;

    @DatabaseField
    public String icon_photo;

    @DatabaseField
    public String cover_photo;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    public Address address;

    @ForeignCollectionField
    public Collection<ClassTime> classTimes;

    @DatabaseField(foreign = true)
    public User user;

    @ForeignCollectionField
    public ForeignCollection<Pass> passes;

    @ForeignCollectionField
    public ForeignCollection<Workout> workouts;

    public void addClassTime(DatabaseWrapper dbWrapper, ClassTime classTime) {

        if (classTimes == null) {
            classTimes = new ArrayList<ClassTime>();
            classTimes.add(classTime);
            dbWrapper.createClassTime(classTime, id);
            return;
        }
        Iterator<ClassTime> currentTimes = classTimes.iterator();

        boolean shouldAdd = true;

        while (currentTimes.hasNext()) {
            ClassTime currentTime = currentTimes.next();
            //Log.d(TAG, "place: " + name + ", currentTime: " + currentTime.classTime + ", now: " + DateTime.now());
            if (currentTime.classTime.compareTo(DateTime.now()) < 0) {
                dbWrapper.deleteClassTime(currentTime, id);
                classTimes.remove(currentTime);
            }
            if (currentTime.classTime.equals(classTime.classTime)) {
                shouldAdd = false;
            }
        }
        if (shouldAdd) {
            classTimes.add(classTime);
            dbWrapper.createClassTime(classTime, id);
        }
    }

    public double getDistance() {

        LatLng userLocation = LocationServices.getLatLng();

        return distanceBetweenPoints(userLocation, new LatLng(address.lat, address.lon));
    }

    private static double deg2rad(double deg)
    {
        return deg * (Math.PI / 180);
    }

    private double rad2deg(double rad)
    {
        return rad * (180 / Math.PI);
    }

    public static double distanceBetweenPoints(LatLng target, LatLng source) {

        double R = 6371;

        double maxLat = Math.max(target.latitude, source.latitude);
        double maxLon = Math.max(target.longitude, source.longitude);
        double minLat = Math.min(target.latitude, source.latitude);
        double minLon = Math.min(target.longitude, source.longitude);

        double dLat = deg2rad(maxLat - minLat);
        double dLon = deg2rad(minLon - maxLon);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(maxLat)) * Math.cos(deg2rad(minLat)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    @Override
    public int compareTo(Place place) {

        if (this == place) {
            return 0;
        }

        double thisDistance = this.getDistance();
        double otherDistance = place.getDistance();

        if (thisDistance > otherDistance) {
            return 1;
        }
        else if (thisDistance < otherDistance) {
            return -1;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
        {
            return false;
        }

        if (this.getClass() != other.getClass())
        {
            return false;
        }

        return this.id.equals(((Place) other).id);
    }
}
