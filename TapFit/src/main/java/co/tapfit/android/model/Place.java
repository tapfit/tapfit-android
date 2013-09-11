package co.tapfit.android.model;

import android.content.Context;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Iterator;

import co.tapfit.android.database.DatabaseWrapper;

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
    public ForeignCollection<ClassTime> classTimes;

    @DatabaseField(foreign = true)
    public User favorite_place;

    public void addClassTime(Context context, ClassTime classTime) {

        Iterator<ClassTime> currentTimes = classTimes.iterator();

        boolean shouldAdd = true;

        while (currentTimes.hasNext()) {
            ClassTime currentTime = currentTimes.next();
            if (currentTime.classTime.compareTo(new Date()) < 0) {
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

}
