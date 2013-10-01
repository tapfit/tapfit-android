package co.tapfit.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zackmartinsek on 9/30/13.
 */
@DatabaseTable(tableName = "category_place")
public class CategoryPlace {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(foreign = true, columnName = "place_id")
    public Place place;

    @DatabaseField(foreign = true, columnName = "category_id")
    public Category category;

    public CategoryPlace(){

    }

    public CategoryPlace(Place place, Category category) {
        this.place = place;
        this.category = category;
    }
}
