package co.tapfit.android.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
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

    @DatabaseField
    public Double credit_amount;

    @ForeignCollectionField
    public ForeignCollection<Place> favorite_places;

    @ForeignCollectionField
    public ForeignCollection<Pass> passes;
}
