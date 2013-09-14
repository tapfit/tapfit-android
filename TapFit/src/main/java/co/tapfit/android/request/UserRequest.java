package co.tapfit.android.request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

import co.tapfit.android.R;
import co.tapfit.android.database.DatabaseHelper;
import co.tapfit.android.helper.DateTimeDeserializer;
import co.tapfit.android.helper.SharePref;
import co.tapfit.android.model.ClassTime;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;
import co.tapfit.android.service.ApiService;

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class UserRequest extends Request {

    private static String TAG = UserRequest.class.getSimpleName();

    public static final String AUTH_TOKEN = "auth_token";

    public static void registerGuest(final Context context, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    User user = null;

                    try
                    {
                        JsonElement element = parser.parse(json);

                        user = gson.fromJson(element, User.class);

                        dbWrapper.createOrUpdateUser(user);

                        Log.d(TAG, "Registered a guest user: " + user.auth_token);

                        SharePref.setIntPref(context, SharePref.CURRENT_USER_ID, user.id);

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null)
                    {
                        if (user == null)
                        {
                            callback.sendCallback(user, "Failed to register user");
                        }
                        else
                        {
                            callback.sendCallback(user, "Success registering user");
                        }
                    }
                }
            }

        };


        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "users/guest");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);


    }

    public static final String EMAIL = "user[email]";
    public static final String PASSWORD = "user[password]";
    public static final String FIRST_NAME = "user[first_name]";
    public static final String LAST_NAME = "user[last_name]";

    public static void registerUser(final Context context, Bundle args, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    User user = null;

                    try
                    {
                        JsonElement element = parser.parse(json);

                        JsonElement errors = element.getAsJsonObject().get("error");

                        if (errors != null)
                        {
                            callback.sendCallback(null, errors.getAsString());
                            return;
                        }

                        user = gson.fromJson(element, User.class);

                        dbWrapper.createOrUpdateUser(user);

                        SharePref.setIntPref(context, SharePref.CURRENT_USER_ID, user.id);

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null)
                    {
                        if (user == null)
                        {
                            callback.sendCallback(user, "Failed to register user");
                        }
                        else
                        {
                            callback.sendCallback(user, "Success registering user");
                        }
                    }
                }
            }

        };

        User user = dbWrapper.getCurrentUser();
        if (user != null && user.is_guest) {
            args.putString(AUTH_TOKEN, user.auth_token);
        }
        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "users/register");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
        intent.putExtra(ApiService.PARAMS, args);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);

    }

    public static void loginUser(final Context context, Bundle args, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    User user = null;

                    try
                    {
                        JsonElement element = parser.parse(json);

                        JsonElement errors = element.getAsJsonObject().get("errors");

                        if (errors != null)
                        {
                            if (callback != null)
                                callback.sendCallback(null, errors.getAsString());
                            return;
                        }

                        user = gson.fromJson(element, User.class);

                        dbWrapper.createOrUpdateUser(user);

                        SharePref.setIntPref(context, SharePref.CURRENT_USER_ID, user.id);

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null)
                    {
                        if (user == null)
                        {
                            callback.sendCallback(user, "Failed to login user");
                        }
                        else
                        {
                            callback.sendCallback(user, "Success loging in user");
                            UserRequest.favorites(context, null);
                        }
                    }
                }
            }

        };

        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "users/login");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
        intent.putExtra(ApiService.PARAMS, args);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);

    }

    public static void favorites(final Context context, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        final ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        JsonArray array = object.getAsJsonArray("places");

                        User currentUser = dbWrapper.getCurrentUser();

                        for (JsonElement element : array)
                        {
                            Place place = gson.fromJson(element, Place.class);

                            for (JsonElement time : element.getAsJsonObject().getAsJsonArray("class_times")) {
                                DateTime dateTime = DateTime.parse(time.getAsString());

                                ClassTime classTime = new ClassTime(dateTime);
                                dbWrapper.createClassTime(classTime);
                                place.addClassTime(context, classTime);
                            }

                            dbWrapper.createOrUpdatePlace(place);

                            dbWrapper.addPlaceToFavorites(currentUser, place);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null) {
                        callback.sendCallback(dbWrapper.getFavorites(), "Success getting favorites");
                    }
                }
            }

        };

        User currentUser = dbWrapper.getCurrentUser();

        if (currentUser != null) {

            Bundle args = new Bundle();
            args.putString(AUTH_TOKEN, currentUser.auth_token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me/favorites");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        } else {
            if (callback != null) {
                callback.sendCallback(null, "Need to sign in");
            }
        }

    }

    public static void getMyInfo(final Context context, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        final ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        JsonElement element = object.get("user");

                        if (element != null) {
                            User user = gson.fromJson(element, User.class);

                            dbWrapper.createOrUpdateUser(user);

                            SharePref.setIntPref(context, SharePref.CURRENT_USER_ID, user.id);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null) {
                        callback.sendCallback(dbWrapper.getPasses(), "Success getting favorites");
                    }
                }
            }

        };

        User currentUser = dbWrapper.getCurrentUser();

        if (currentUser != null) {

            Bundle args = new Bundle();
            args.putString(AUTH_TOKEN, currentUser.auth_token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        } else {
            if (callback != null) {
                callback.sendCallback(null, "Need to sign in");
            }
        }

    }

    public static void getPasses(final Context context, final ResponseCallback callback) {

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        final ResultReceiver receiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        JsonArray array = object.getAsJsonArray("receipts");

                        User currentUser = dbWrapper.getCurrentUser();

                        for (JsonElement element : array)
                        {
                            Pass pass = gson.fromJson(element, Pass.class);

                            Workout workout = gson.fromJson(element.getAsJsonObject().get("workout_json"), Workout.class);

                            Place place = gson.fromJson(element.getAsJsonObject().get("place_json"), Place.class);

                            dbWrapper.createOrUpdatePlace(place);

                            dbWrapper.createOrUpdateWorkout(workout);

                            pass.workout = workout;
                            pass.place = place;

                            dbWrapper.createOrUpdatePass(pass);

                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    if (callback != null) {
                        callback.sendCallback(dbWrapper.getPasses(), "Success getting favorites");
                    }
                }
            }

        };

        User currentUser = dbWrapper.getCurrentUser();

        if (currentUser == null) {

            Bundle args = new Bundle();
            args.putString(AUTH_TOKEN, currentUser.auth_token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "me/receipts");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        } else {
            if (callback != null) {
                callback.sendCallback(null, "Need to sign in");
            }
        }

    }

}
