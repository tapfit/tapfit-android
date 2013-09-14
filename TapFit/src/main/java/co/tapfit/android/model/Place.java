package co.tapfit.android.model;

import android.content.Context;

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

/**
 * Created by zackmartinsek on 9/7/13.
 */
@DatabaseTable(tableName = "places")
public class Place {

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
    public User favorite_place;

    @ForeignCollectionField
    public ForeignCollection<Pass> passes;

    @ForeignCollectionField
    public ForeignCollection<Workout> workouts;

    public void addClassTime(Context context, ClassTime classTime) {

        if (classTimes == null) {
            classTimes = new ArrayList<ClassTime>();
            classTimes.add(classTime);
            return;
        }
        Iterator<ClassTime> currentTimes = classTimes.iterator();

        boolean shouldAdd = true;

        while (currentTimes.hasNext()) {
            ClassTime currentTime = currentTimes.next();
            if (currentTime.classTime.compareTo(DateTime.now()) < 0) {
                DatabaseWrapper.getInstance(context).deleteClassTime(currentTime);
                classTimes.remove(currentTime);
            }
            if (currentTime.classTime.equals(classTime.classTime)) {
                shouldAdd = false;
            }
        }
        if (shouldAdd)
            classTimes.add(classTime);
    }

    public double getDistance() {

        LatLng userLocation = LocationServices.getLatLng();

        double R = 6371;

        double maxLat = Math.max(userLocation.latitude, this.address.lat);
        double maxLon = Math.max(userLocation.longitude, this.address.lon);
        double minLat = Math.min(userLocation.latitude, this.address.lat);
        double minLon = Math.min(userLocation.longitude, this.address.lon);

        double dLat = deg2rad( - minLat);
        double dLon = deg2rad(minLon - maxLon);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(maxLat)) * Math.cos(deg2rad(minLat)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    private double deg2rad(double deg)
    {
        return deg * (Math.PI / 180);
    }

    private double rad2deg(double rad)
    {
        return rad * (180 / Math.PI);
    }
}
