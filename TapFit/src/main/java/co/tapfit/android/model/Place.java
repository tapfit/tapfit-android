package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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
    public String avg_rating;

    @DatabaseField
    public String url;

    @DatabaseField
    public String icon_photo;

    @DatabaseField
    public String cover_photo;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    public Address address;



}
