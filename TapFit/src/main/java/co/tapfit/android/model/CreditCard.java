package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zackmartinsek on 9/14/13.
 */
@DatabaseTable(tableName = "credit_card")
public class CreditCard {

    @DatabaseField(id = true)
    public String token;

    @DatabaseField
    public String last_four;

    @DatabaseField
    public String card_type;

    @DatabaseField
    public String image_url;

    @DatabaseField
    public Boolean default_card;

    @DatabaseField(foreign = true)
    public User user;
}
