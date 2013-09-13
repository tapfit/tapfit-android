package co.tapfit.android.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by zackmartinsek on 9/11/13.
 */
@DatabaseTable(tableName = "workout")
public class Workout implements Comparable<Workout> {

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField
    public String name;

    @DatabaseField(dataType = DataType.DATE_TIME)
    public DateTime start_time;

    @DatabaseField(dataType = DataType.DATE_TIME)
    public DateTime end_time;

    @DatabaseField
    public String source_description;

    @DatabaseField(foreign = true)
    public Place place;

    @DatabaseField
    public Double price;

    @DatabaseField
    public Boolean can_buy;

    @DatabaseField
    public Boolean is_day_pass;

    @DatabaseField
    public Double original_price;

    @DatabaseField
    public Double quantity_left;

    @DatabaseField
    public Double avg_rating;

    @DatabaseField
    public String fine_print;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    public Instructor instructor;

    @ForeignCollectionField
    public ForeignCollection<Pass> passes;

    public Workout() {

    }

    public Workout(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Workout workout) {

        if (this == workout) {
            return 0;
        }

        return this.start_time.compareTo(workout.start_time);
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

        return this.id.equals(((Workout) other).id);
    }
}
