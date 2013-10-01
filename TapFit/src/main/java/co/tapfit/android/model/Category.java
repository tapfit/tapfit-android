package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zackmartinsek on 9/30/13.
 */
@DatabaseTable(tableName = "category")
public class Category {

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField
    public String name;

}
