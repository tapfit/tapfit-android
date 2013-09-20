package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by zackmartinsek on 9/11/13.
 */
@DatabaseTable(tableName = "pass")
public class Pass {

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField
    public Double price;

    @DatabaseField
    public Date expiration_date;

    @DatabaseField
    public Boolean has_used;

    @DatabaseField
    public String instructions;

    @DatabaseField
    public String fine_print;

    @DatabaseField(foreign = true)
    public Workout workout;

    @DatabaseField(foreign = true)
    public Place place;

    @DatabaseField(foreign = true)
    public User user;

}
