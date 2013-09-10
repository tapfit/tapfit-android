package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zackmartinsek on 9/8/13.
 */
@DatabaseTable(tableName = "user")
public class User {

    public User() {

    }

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField
    public String first_name;

    @DatabaseField
    public String last_name;

    @DatabaseField
    public String email;

    @DatabaseField
    public boolean is_guest;

    @DatabaseField
    public String auth_token;
}
