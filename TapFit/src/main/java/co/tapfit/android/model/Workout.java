package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by zackmartinsek on 9/11/13.
 */
@DatabaseTable(tableName = "workout")
public class Workout {

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField
    public String name;

    @DatabaseField
    public Date start_time;

    @DatabaseField
    public Date end_time;

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

    @DatabaseField(foreign = true)
    public Instructor instructor;
}
