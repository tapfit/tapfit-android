package co.tapfit.android.database;

import android.content.Context;
import co.tapfit.android.helper.Log;

import com.flurry.android.monolithic.sdk.impl.up;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.joda.time.DateTime;

import java.util.List;

import co.tapfit.android.helper.SharePref;
import co.tapfit.android.model.ClassTime;
import co.tapfit.android.model.CreditCard;
import co.tapfit.android.model.Instructor;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;

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
            User currentUser = helper.getUserDao().queryForId(user.id);

            if (currentUser != null && user.credit_amount == null) {
                user.credit_amount = currentUser.credit_amount;
            }

            if (currentUser != null && user.auth_token == null) {
                user.auth_token = currentUser.auth_token;
            }

            if (user.credit_amount == null) {
                user.credit_amount = 0.0;
            }

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
            Log.d(TAG, "Current user id: " + userId);
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

    public void removePlaceFromFavorites(User user, Place place) {
        try
        {
            place.user = null;
            helper.getPlaceDao().update(place);

            helper.getUserDao().refresh(user);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed remove place from favorites");
        }
    }

    public void deleteCreditCard(CreditCard creditCard) {
        try
        {
            helper.getCreditCardDao().delete(creditCard);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to delete card: " + creditCard.token, e);
        }
    }

    public void setDefaultCardFromToken(String token) {
        try
        {
            CreditCard card = helper.getCreditCardDao().queryForEq("token", token).get(0);
            card.default_card = true;
            setDefaultCardForUser(getCurrentUser(), card);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Error setting default card from token: " + token, e);
        }
    }

    public void setDefaultCardForUser(User user, CreditCard creditCard) {
        try
        {
            UpdateBuilder<CreditCard, Integer> updateBuilder = helper.getCreditCardDao().updateBuilder();
            updateBuilder.updateColumnValue("default_card", false);
            helper.getCreditCardDao().update(updateBuilder.prepare());

            creditCard.default_card = true;

            helper.getCreditCardDao().createOrUpdate(creditCard);
        }
        catch (Exception e) {
            Log.d(TAG, "Failed to set default card for user: " + creditCard.token, e);
        }
    }

    public void addCreditCardToUser(User user, CreditCard creditCard) {
        try
        {
            creditCard.user = user;

            if (creditCard.default_card) {
                UpdateBuilder<CreditCard, Integer> updateBuilder = helper.getCreditCardDao().updateBuilder();
                updateBuilder.updateColumnValue("default_card", false);
                helper.getCreditCardDao().update(updateBuilder.prepare());
            }

            helper.getCreditCardDao().createOrUpdate(creditCard);

            helper.getUserDao().refresh(user);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to add credit card to user");
        }
    }

    public List<CreditCard> getCreditCards(User user) {
        try {
            return helper.getCreditCardDao().queryForEq("user_id", user.id);
        } catch (Exception e) {
            Log.d(TAG, "Failed to get credit cards");
            return null;
        }
    }

    public void removeCreditCardFromUser(User user, CreditCard creditCard) {
        try
        {
            creditCard.user = null;
            helper.getCreditCardDao().delete(creditCard);

            helper.getUserDao().refresh(user);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to remove credit card from user");
        }
    }

    public void addPlaceToFavorites(User user, Place place) {
        try
        {
            place.user = user;
            helper.getPlaceDao().update(place);

            helper.getUserDao().refresh(user);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to add place to favorites");
        }
    }

    public List<ClassTime> getClassTimes(Integer id) {

        try {

            return helper.getClassTimeDao().queryForEq("place_id", id);
        }
        catch (Exception e) {
            Log.d(TAG, "Failed to get class times for id: " + id, e);
            return null;
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

    public void createOrUpdateWorkout(Workout workout) {
        try
        {
            helper.getWorkoutDao().createOrUpdate(workout);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to create workout: " + workout, e);
        }
    }

    public List<Workout> getUpcomingWorkouts(Place place) {
        try
        {
            QueryBuilder<Workout, Integer> builder = helper.getWorkoutDao().queryBuilder();
            builder.where().eq("place_id", place.id).and().gt("start_time", DateTime.now());
            return helper.getWorkoutDao().query(builder.prepare());
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get upcoming workouts for place: " + place.id);
            return null;
        }
    }

    public List<Workout> getWorkouts(Place place) {
        try
        {
            return helper.getWorkoutDao().queryForEq("place_id", place.id);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get workouts: " + place.id, e);
            return null;
        }
    }

    public void createOrUpdatePass(Pass pass) {
        try
        {
            helper.getPassDao().createOrUpdate(pass);

            helper.getPlaceDao().update(pass.place);
            helper.getWorkoutDao().update(pass.workout);
            helper.getUserDao().update(pass.user);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to create pass: " + pass, e);
        }
    }

    public List<Pass> getPasses() {
        try
        {
            return helper.getPassDao().queryForEq("user_id", SharePref.getIntPref(mContext, SharePref.CURRENT_USER_ID));
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get pass for user.", e);
            return null;
        }
    }

    public Place getPlace(Integer placeId) {
        try
        {
            return helper.getPlaceDao().queryForId(placeId);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to place for id: " + placeId, e);
            return null;
        }
    }

    public void createOrUpdateInstructor(Instructor instructor) {
        try
        {
            helper.getInstructorDao().createOrUpdate(instructor);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to create instructor: " + instructor, e);
        }
    }

    public Workout getWorkout(Integer workoutId) {
        try
        {
            return helper.getWorkoutDao().queryForId(workoutId);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get workout with id: " + workoutId, e);
            return null;
        }
    }

    public CreditCard getDefaulCard(User mUser) {
        try {
            QueryBuilder<CreditCard, Integer> queryBuilder = helper.getCreditCardDao().queryBuilder();
            queryBuilder.where().eq("user_id", mUser.id).and().eq("default_card", true);
            List<CreditCard> default_cards = helper.getCreditCardDao().query(queryBuilder.prepare());
            for (CreditCard card : default_cards) {
                return card;
            }
            return null;
        }
        catch (Exception e) {
            Log.d(TAG, "Failed to get default card");
            return null;
        }
    }
}
