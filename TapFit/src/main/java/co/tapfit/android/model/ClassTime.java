package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by zackmartinsek on 9/10/13.
 */
@DatabaseTable(tableName = "class_time")
public class ClassTime {

    public ClassTime()
    {

    }

    public ClassTime(Date classTime) {
        this.classTime = classTime;
    }


    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField
    public Date classTime;

    @DatabaseField(foreign=true)
    public Place place;
}
