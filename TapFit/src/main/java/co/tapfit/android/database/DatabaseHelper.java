package co.tapfit.android.database;

import co.tapfit.android.R;

import android.app.ActionBar;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import co.tapfit.android.helper.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

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

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "tapfit.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 22;

    // the DAO model we use to access the SimpleData table
    private Dao<Place, Integer> placeDao = null;
    private RuntimeExceptionDao<Place, Integer> placeRuntimeDao = null;

    private Dao<Address, Integer> addressDao = null;
    private RuntimeExceptionDao<Address, Integer> addressRuntimeDao = null;

    private Dao<User, Integer> userDao = null;
    private RuntimeExceptionDao<User, Integer> userRuntimeDao = null;

    private Dao<ClassTime, Integer> classTimeDao = null;
    private RuntimeExceptionDao<ClassTime, Integer> classTimeRuntimeDao = null;

    private Dao<Workout, Integer> workoutDao = null;
    private RuntimeExceptionDao<Workout, Integer> workoutRuntimeDao = null;

    private Dao<Instructor, Integer> instructorDao = null;
    private RuntimeExceptionDao<Instructor, Integer> instructorRuntimeDao = null;

    private Dao<Pass, Integer> passDao = null;
    private RuntimeExceptionDao<Pass, Integer> passRuntimeDao = null;

    private Dao<CreditCard, Integer> creditCardDao = null;
    private RuntimeExceptionDao<CreditCard, Integer> creditCardRuntimeDao = null;

    private Dao<Region, Integer> regionDao = null;
    private RuntimeExceptionDao<Region, Integer> regionRuntimeDao = null;

    private Dao<Category, Integer> categoryDao = null;
    private RuntimeExceptionDao<Category, Integer> categoryRuntimeDao = null;

    private Dao<CategoryPlace, Integer> categoryPlaceDao = null;
    private RuntimeExceptionDao<CategoryPlace, Integer> categoryPlaceRuntimeDao = null;

    public DatabaseHelper(Context context) {
        //super(context, DATABASE_NAME, null, DATABASE_VERSION);
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);

        Log.d("DatabaseHelper", "Thread: " + Thread.currentThread().getName());
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Place.class);
            TableUtils.createTable(connectionSource, Address.class);
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, ClassTime.class);
            TableUtils.createTable(connectionSource, Workout.class);
            TableUtils.createTable(connectionSource, Instructor.class);
            TableUtils.createTable(connectionSource, Pass.class);
            TableUtils.createTable(connectionSource, CreditCard.class);
            TableUtils.createTable(connectionSource, Region.class);
            TableUtils.createTable(connectionSource, Category.class);
            TableUtils.createTable(connectionSource, CategoryPlace.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Place.class, true);
            TableUtils.dropTable(connectionSource, Address.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, ClassTime.class, true);
            TableUtils.dropTable(connectionSource, Workout.class, true);
            TableUtils.dropTable(connectionSource, Instructor.class, true);
            TableUtils.dropTable(connectionSource, Pass.class, true);
            TableUtils.dropTable(connectionSource, CreditCard.class, true);
            TableUtils.dropTable(connectionSource, Region.class, true);
            TableUtils.dropTable(connectionSource, Category.class, true);
            TableUtils.dropTable(connectionSource, CategoryPlace.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<Place, Integer> getPlaceDao() throws SQLException {
        if (placeDao == null) {
            placeDao = getDao(Place.class);
        }
        return placeDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<Place, Integer> getPlaceRuntimeDao() {
        if (placeRuntimeDao == null) {
            placeRuntimeDao = getRuntimeExceptionDao(Place.class);
        }
        return placeRuntimeDao;
    }

    public Dao<Address, Integer> getAddressDao() throws SQLException {
        if (addressDao == null) {
            addressDao = getDao(Address.class);
        }
        return addressDao;
    }

    public RuntimeExceptionDao<Address, Integer> getAddressRuntimeDao() {
        if (addressRuntimeDao == null) {
            addressRuntimeDao = getRuntimeExceptionDao(Address.class);
        }
        return addressRuntimeDao;
    }

    public Dao<User, Integer> getUserDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(User.class);
        }
        return userDao;
    }

    public RuntimeExceptionDao<User, Integer> getUserRuntimeDao() {
        if (userRuntimeDao == null) {
            userRuntimeDao = getRuntimeExceptionDao(User.class);
        }
        return userRuntimeDao;
    }

    public Dao<ClassTime, Integer> getClassTimeDao() throws SQLException {
        if (classTimeDao == null) {
            classTimeDao = getDao(ClassTime.class);
        }
        return classTimeDao;
    }

    public RuntimeExceptionDao<ClassTime, Integer> getClassTimeRuntimeDao() {
        if (classTimeRuntimeDao == null) {
            classTimeRuntimeDao = getRuntimeExceptionDao(ClassTime.class);
        }
        return classTimeRuntimeDao;
    }

    public Dao<Workout, Integer> getWorkoutDao() throws SQLException {
        if (workoutDao == null) {
            workoutDao = getDao(Workout.class);
        }
        return workoutDao;
    }

    public RuntimeExceptionDao<Workout, Integer> getWorkoutRuntimeDao() {
        if (workoutRuntimeDao == null) {
            workoutRuntimeDao = getRuntimeExceptionDao(Workout.class);
        }
        return workoutRuntimeDao;
    }

    public Dao<Instructor, Integer> getInstructorDao() throws SQLException {
        if (instructorDao == null) {
            instructorDao = getDao(Instructor.class);
        }
        return instructorDao;
    }

    public RuntimeExceptionDao<Instructor, Integer> getInstructorRuntimeDao() {
        if (instructorRuntimeDao == null) {
            instructorRuntimeDao = getRuntimeExceptionDao(Instructor.class);
        }
        return instructorRuntimeDao;
    }

    public Dao<Pass, Integer> getPassDao() throws SQLException {
        if (passDao == null) {
            passDao = getDao(Pass.class);
        }
        return passDao;
    }

    public RuntimeExceptionDao<Pass, Integer> getPassRuntimeDao() {
        if (passRuntimeDao == null) {
            passRuntimeDao = getRuntimeExceptionDao(Pass.class);
        }
        return passRuntimeDao;
    }

    public Dao<CreditCard, Integer> getCreditCardDao() throws SQLException {
        if (creditCardDao == null) {
            creditCardDao = getDao(CreditCard.class);
        }
        return creditCardDao;
    }

    public RuntimeExceptionDao<CreditCard, Integer> getCreditCardRuntimeDao() {
        if (creditCardRuntimeDao == null) {
            creditCardRuntimeDao = getRuntimeExceptionDao(CreditCard.class);
        }
        return creditCardRuntimeDao;
    }

    public Dao<Region, Integer> getRegionDao() throws SQLException {
        if (regionDao == null) {
            regionDao = getDao(Region.class);
        }
        return regionDao;
    }

    public RuntimeExceptionDao<Region, Integer> getRegionRuntimeDao() {
        if (regionRuntimeDao == null) {
            regionRuntimeDao = getRuntimeExceptionDao(Region.class);
        }
        return regionRuntimeDao;
    }

    public Dao<Category, Integer> getCategoryDao() throws SQLException {
        if (categoryDao == null) {
            categoryDao = getDao(Category.class);
        }
        return categoryDao;
    }

    public RuntimeExceptionDao<Category, Integer> getCategoryRuntimeDao() {
        if (categoryRuntimeDao == null) {
            categoryRuntimeDao = getRuntimeExceptionDao(Category.class);
        }
        return categoryRuntimeDao;
    }

    public Dao<CategoryPlace, Integer> getCategoryPlaceDao() throws SQLException {
        if (categoryPlaceDao == null) {
            categoryPlaceDao = getDao(CategoryPlace.class);
        }
        return categoryPlaceDao;
    }

    public RuntimeExceptionDao<CategoryPlace, Integer> getCategoryPlaceRuntimeDao() {
        if (categoryPlaceRuntimeDao == null) {
            categoryPlaceRuntimeDao = getRuntimeExceptionDao(CategoryPlace.class);
        }
        return categoryPlaceRuntimeDao;
    }
    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        placeRuntimeDao = null;
    }
}