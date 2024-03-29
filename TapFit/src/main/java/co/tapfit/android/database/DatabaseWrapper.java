package co.tapfit.android.database;

import android.content.Context;
import android.database.SQLException;
import android.graphics.PointF;

import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.helper.Log;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import co.tapfit.android.helper.Range;
import co.tapfit.android.helper.Search;
import co.tapfit.android.helper.SharePref;
import co.tapfit.android.model.Address;
import co.tapfit.android.model.Category;
import co.tapfit.android.model.CategoryPlace;
import co.tapfit.android.model.ClassTime;
import co.tapfit.android.model.CreditCard;
import co.tapfit.android.model.Instructor;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.Region;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;
import co.tapfit.android.view.TouchableWrapper;

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

    public List<Place> getPlaces(LatLng location, float radius) {
        try
        {
            PointF center = new PointF((float) location.latitude, (float) location.longitude);
            PointF p1 = calculateDerivedPosition(center, radius, 0);
            PointF p2 = calculateDerivedPosition(center, radius, 90);
            PointF p3 = calculateDerivedPosition(center, radius, 180);
            PointF p4 = calculateDerivedPosition(center, radius, 270);

            QueryBuilder<Address, Integer> addressBuilder = helper.getAddressDao().queryBuilder();

            addressBuilder.where().between("lat", p3.x, p1.x).and().between("lon", p4.y, p2.y);

            helper.getAddressDao().query(addressBuilder.prepare());

            QueryBuilder<Place, Integer> builder = helper.getPlaceDao().queryBuilder();

            builder.join(addressBuilder);

            return helper.getPlaceDao().query(builder.prepare());
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get places with location: " + location.latitude + ", " + location.longitude, e);
            return null;
        }
    }

    public static Address getAddress(Integer addressId) {
        try
        {
            return helper.getAddressDao().queryForId(addressId);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get address: " + addressId);
            return null;
        }
    }

    public static PointF calculateDerivedPosition(PointF point,
                                                  double range, double bearing)
    {
        double EarthRadius = 6371; // mi

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;

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

    public Instructor getInstructor(Integer instructorId) {
        try
        {
            return helper.getInstructorDao().queryForId(instructorId);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get Instructor: " + instructorId, e);
            return null;
        }
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
                //user.credit_amount = currentUser.credit_amount;
            }

            if (currentUser != null && user.auth_token == null) {
                user.auth_token = currentUser.auth_token;
            }

            if (user.credit_amount == null) {
                user.credit_amount = 0.0;
            }

            SharePref.setStringPref(mContext, SharePref.AUTH_TOKEN, user.auth_token);

            helper.getUserDao().createOrUpdate(user);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to create user: " + user, e);
        }
    }

    public void createClassTime(ClassTime classTime, Integer placeId) {

        try
        {
            QueryBuilder<ClassTime, Integer> builder = helper.getClassTimeDao().queryBuilder();
            builder.where().eq("place_id", placeId).and().eq("classTime", classTime.classTime);
            List<ClassTime> times = helper.getClassTimeDao().query(builder.prepare());
            if (times.size() < 1) {
                helper.getClassTimeDao().create(classTime);
            }
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

    public void deleteClassTime(ClassTime classTime, Integer placeId) {
        try
        {
            QueryBuilder<ClassTime, Integer> builder = helper.getClassTimeDao().queryBuilder();
            builder.where().eq("place_id", placeId).and().eq("classTime", classTime.classTime);
            final List<ClassTime> times = helper.getClassTimeDao().query(builder.prepare());
            helper.getClassTimeDao().callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (ClassTime time : times) {
                        helper.getClassTimeDao().delete(time);
                    }
                    return null;
                }
            });
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

            if (place.facility_type == null || place.facility_type == 0) {
                QueryBuilder<Workout, Integer> builder = helper.getWorkoutDao().queryBuilder();
                builder.where().eq("place_id", place.id).and().gt("start_time", DateTime.now());
                return helper.getWorkoutDao().query(builder.prepare());
            }
            else
            {
                QueryBuilder<Workout, Integer> builder = helper.getWorkoutDao().queryBuilder();
                builder.where().eq("place_id", place.id).and().gt("end_time", DateTime.now());
                return helper.getWorkoutDao().query(builder.prepare());
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get upcoming workouts for place: " + place.id, e);
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

    public Pass getPass(Integer passId)
    {
        try
        {
            return helper.getPassDao().queryForId(passId);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get pass: " + passId, e);
            return null;
        }
    }

    public void createOrUpdatePass(Pass pass) {
        try
        {
            helper.getPassDao().createOrUpdate(pass);

            helper.getPlaceDao().update(pass.place);
            helper.getWorkoutDao().createOrUpdate(pass.workout);
            helper.getUserDao().update(pass.user);
            helper.getInstructorDao().createOrUpdate(pass.workout.instructor);
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

    public void createCategoryPlace(CategoryPlace relation) {
        try
        {
            QueryBuilder<CategoryPlace, Integer> builder = helper.getCategoryPlaceDao().queryBuilder();
            builder.where().eq("place_id", relation.place.id).and().eq("category_id", relation.category.id);

            List<CategoryPlace> categoryPlaces = helper.getCategoryPlaceDao().query(builder.prepare());

            if (categoryPlaces.size() < 1) {
                helper.getCategoryPlaceDao().createOrUpdate(relation);
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to create relation: " + relation, e);
        }
    }

    public void createOrUpdateCategory(Category category) {
        try
        {
            helper.getCategoryDao().createOrUpdate(category);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to create category: " + category, e);
        }
    }

    public List<Region> getRegions() {
        try
        {
            return helper.getRegionDao().queryForAll();
        }
        catch (Exception e)
        {
            Log.d(TAG, "Faild to get regions", e);
            return null;
        }
    }

    public void createOrUpdateRegion(Region region) {

        try
        {
            helper.getRegionDao().createOrUpdate(region);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to created or update region: " + region, e);
        }
    }

    public List<Place> getFilteredPlaces(LatLng location, int radius) {
        try
        {
            PointF center = new PointF((float) location.latitude, (float) location.longitude);
            PointF p1 = calculateDerivedPosition(center, radius, 0);
            PointF p2 = calculateDerivedPosition(center, radius, 90);
            PointF p3 = calculateDerivedPosition(center, radius, 180);
            PointF p4 = calculateDerivedPosition(center, radius, 270);

            QueryBuilder<Address, Integer> addressBuilder = helper.getAddressDao().queryBuilder();

            addressBuilder.where().between("lat", p3.x, p1.x).and().between("lon", p4.y, p2.y);

            HashMap<DateTime, DateTime> timeIntervals = Search.getInstance(mContext).getWorkoutTimes();
            List<Range<Double>> priceRanges = Search.getPriceRanges();
            List<String> categories = Search.getCategoryStrings();

            QueryBuilder<Place, Integer> builder = helper.getPlaceDao().queryBuilder();
            QueryBuilder<Place, Integer> gymBuilder = helper.getPlaceDao().queryBuilder();
            Where<Place, Integer> gymWhere = gymBuilder.where();

            if (timeIntervals != null) {

                QueryBuilder<ClassTime, Integer> classTimeBuilder = helper.getClassTimeDao().queryBuilder();

                Where<ClassTime, Integer> where = classTimeBuilder.where();

                for (DateTime begin : timeIntervals.keySet()) {
                    where.between("classTime", begin.minusSeconds(1), timeIntervals.get(begin).plusSeconds(1));
                }
                if (timeIntervals.size() > 1) {
                    where.or(timeIntervals.size());
                }

                builder.join(classTimeBuilder);
            }
            Where<Place, Integer> where = null;
            if (priceRanges != null || categories != null)
            {
                where = builder.where();
            }
            if (priceRanges != null) {

                for (Range<Double> range : priceRanges) {
                    where.between("lowest_price", range.low - 0.01, range.high + 0.01);
                    gymWhere.between("lowest_price", range.low - 0.01, range.high + 0.01);
                }
                if (priceRanges.size() > 1) {
                    where.or(priceRanges.size());
                    gymWhere.or(priceRanges.size());
                }
            }

            if (categories != null) {

                QueryBuilder<CategoryPlace, Integer> categoryPlace = makeCategoriesForUser(categories);

                List<CategoryPlace> places = helper.getCategoryPlaceDao().query(categoryPlace.prepare());

                Set<Integer> placeIds = new HashSet<Integer>();
                for (CategoryPlace place : places)
                {
                    placeIds.add(place.place.id);
                }

                if (categoryPlace != null)
                {
                    where.in("category", categories);
                    where.in("place_id", placeIds);
                    where.or(2);
                    if (priceRanges != null)
                    {
                        where.and(2);
                    }
                    gymWhere.in("category", categories);
                    gymWhere.in("place_id", placeIds);
                    gymWhere.or(2);
                }
            }


            builder.join(addressBuilder).distinct();

            Set<Place> places = new HashSet<Place>(helper.getPlaceDao().query(builder.prepare()));

            gymWhere.gt("facility_type", 0);
            int addCount = 1;
            if (priceRanges != null) {
                addCount = addCount + 1;
            }

            if (categories != null) {
                addCount = addCount + 1;
            }

            if (addCount > 1)
            {
                gymWhere.and(addCount);
            }

            List<Place> dayPassGyms = helper.getPlaceDao().query(gymBuilder.prepare());
            for (Place place : dayPassGyms) {
                if (!places.contains(place)) {
                    places.add(place);
                }
            }

            return new ArrayList<Place>(places);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get filtered places with location: " + location.latitude + ", " + location.longitude, e);
            return null;
        }
    }

    private QueryBuilder<CategoryPlace, Integer> makeCategoriesForUser(List<String> categories) {

        try
        {
            QueryBuilder<Category, Integer> categoryBuilder = helper.getCategoryDao().queryBuilder();

            categoryBuilder.where().in("name", categories);

            List<Category> categoryList = helper.getCategoryDao().query(categoryBuilder.prepare());

            QueryBuilder<CategoryPlace, Integer> categoryPlaceQb = helper.getCategoryPlaceDao().queryBuilder();

            categoryPlaceQb.selectColumns("place_id");
            categoryPlaceQb.where().in("category_id", categoryList);

            return categoryPlaceQb;
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to getCategories: " + categories, e);
            return null;
        }
    }
}
