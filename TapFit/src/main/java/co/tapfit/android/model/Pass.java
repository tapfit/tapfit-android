package co.tapfit.android.model;

import android.renderscript.Element;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by zackmartinsek on 9/11/13.
 */
@DatabaseTable(tableName = "pass")
public class Pass implements Comparable<Pass> {

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

    @DatabaseField(dataType = DataType.DATE_TIME)
    public DateTime created_at;

    @DatabaseField(foreign = true)
    public Workout workout;

    @DatabaseField(foreign = true)
    public Place place;

    @DatabaseField(foreign = true)
    public User user;


    @Override
    public int compareTo(Pass pass) {
        if (this == pass) {
            return 0;
        }

        return this.created_at.compareTo(pass.created_at);
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

        return this.id.equals(((Pass) other).id);
    }
}
