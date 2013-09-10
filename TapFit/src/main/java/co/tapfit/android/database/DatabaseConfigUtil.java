package co.tapfit.android.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import co.tapfit.android.model.Place;

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[] {
            Place.class,
    };

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
