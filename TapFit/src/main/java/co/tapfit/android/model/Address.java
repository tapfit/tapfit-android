package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zackmartinsek on 9/8/13.
 */
@DatabaseTable(tableName = "address")
public class Address {

    public Address(){

    }

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField
    public String line1;

    @DatabaseField
    public String line2;

    @DatabaseField
    public String city;

    @DatabaseField
    public String state;

    @DatabaseField
    public String zip;

    @DatabaseField
    public float lat;

    @DatabaseField
    public float lon;
}
