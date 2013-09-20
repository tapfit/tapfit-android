package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zackmartinsek on 9/19/13.
 */
@DatabaseTable(tableName = "region")
public class Region {

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField
    public String name;

    @DatabaseField
    public String city;

    @DatabaseField
    public String state;

    @DatabaseField
    public Float lat;

    @DatabaseField
    public Float lon;

    @DatabaseField
    public Integer radius;

    public Region() {

    }

    public Region(int i) {
        id = i;
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

        return this.id.equals(((Region) other).id);
    }

}
