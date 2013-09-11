package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zackmartinsek on 9/11/13.
 */
@DatabaseTable(tableName = "instructor")
public class Instructor {

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField
    public String first_name;

    @DatabaseField
    public String last_name;

    @ForeignCollectionField
    public Workout workouts;
}
