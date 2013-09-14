package co.tapfit.android.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by zackmartinsek on 9/10/13.
 */
@DatabaseTable(tableName = "class_time")
public class ClassTime {

    public ClassTime()
    {

    }

    public ClassTime(DateTime classTime) {
        this.classTime = classTime;
    }


    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField(dataType = DataType.DATE_TIME)
    public DateTime classTime;

    @DatabaseField(foreign=true)
    public Place place;
}
