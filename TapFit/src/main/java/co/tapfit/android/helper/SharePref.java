package co.tapfit.android.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zackmartinsek on 9/11/13.
 */
public class SharePref {

    public static final String CURRENT_USER_ID = "currentUser";

    public static void setIntPref(Context context, String key, Integer value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(key, value);

        editor.commit();
    }

    public static int getIntPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, -1);
    }
}
