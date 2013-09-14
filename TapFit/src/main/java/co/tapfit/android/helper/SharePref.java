package co.tapfit.android.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zackmartinsek on 9/11/13.
 */
public class SharePref {

    public static final String CURRENT_USER_ID = "currentUser";
    public static final String KEY_PREFS_FIRST_USE = "firstUse";

    private static final String APP_SHARED_PREFS = SharePref.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private Editor _prefsEditor;

    /**
     * Allow the user to instantiate an instance of our shared preferences class.
     *
     * @param context is required for access to SharedPreferences
     */
    public SharePref(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    /**
     * Set whether or not this is the first use of the application.
     *
     * @param isFirstUse
     */
    public void setFirstUse(boolean isFirstUse) {
        _prefsEditor.putBoolean(KEY_PREFS_FIRST_USE, isFirstUse);
        _prefsEditor.commit();
    }

    /**
     * Get whether or not this is the first use of the application.
     *
     * @return true if this is the first use.
     */
    public boolean getFirstUse() {
        return _sharedPrefs.getString(KEY_PREFS_FIRST_USE, true);
    }

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
