package co.tapfit.android.database;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.List;

import co.tapfit.android.helper.SharePref;
import co.tapfit.android.model.Address;
import co.tapfit.android.model.ClassTime;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class DatabaseWrapper {

    private static final String TAG = DatabaseWrapper.class.getSimpleName();
    private static DatabaseWrapper ourInstance;
    private static DatabaseHelper helper;
    private Context mContext;

    public static DatabaseWrapper getInstance(Context context) {
        if (ourInstance == null)
        {
            ourInstance = new DatabaseWrapper(context);
        }
        return ourInstance;
    }

    private DatabaseWrapper(Context context) {
        mContext = context;
        helper = new DatabaseHelper(context);
    }

    public List<Place> getPlaces()
    {
        try
        {
            return helper.getPlaceDao().queryForAll();
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get Places", e);
        }
        return null;
    }

    public void createOrUpdatePlace(Place place){

        try
        {
            if (place.address != null)
            {
                helper.getAddressDao().createOrUpdate(place.address);
            }
            helper.getPlaceDao().createOrUpdate(place);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to save place: " + place.id, e);
        }
    }

    public void createOrUpdateUser(User user) {
        try
        {
            helper.getUserDao().createOrUpdate(user);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to create user: " + user, e);
        }
    }

    public void createClassTime(ClassTime classTime) {

        try
        {
            helper.getClassTimeDao().create(classTime);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to create classTime: " + classTime.classTime, e);
        }
    }

    public User getCurrentUser() {
        try
        {
            Integer userId = SharePref.getIntPref(mContext, SharePref.CURRENT_USER_ID);
            return helper.getUserDao().queryForId(userId);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to find a current user", e);
            return null;
        }
    }

    public void deleteClassTime(ClassTime classTime) {
        try
        {
            helper.getClassTimeDao().delete(classTime);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to delete classTime: " + classTime, e);
        }
    }

    public List<Place> getFavorites() {
        try
        {
            return helper.getPlaceDao().queryForEq("user_id", SharePref.getIntPref(mContext, SharePref.CURRENT_USER_ID));
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get favorites");
            return null;
        }
    }
}
