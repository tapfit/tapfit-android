package co.tapfit.android.request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Date;

import co.tapfit.android.R;
import co.tapfit.android.database.DatabaseHelper;
import co.tapfit.android.helper.SharePref;
import co.tapfit.android.model.ClassTime;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.service.ApiService;

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class UserRequest extends Request {

    private static String TAG = UserRequest.class.getSimpleName();

    private static final String AUTH_TOKEN = "auth_token";

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

                    Gson gson = new Gson();

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

                    Gson gson = new Gson();

                    JsonParser parser = new JsonParser();

                    User user = null;

                    try
                    {
                        JsonElement element = parser.parse(json);

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

        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "users/register");
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

                    Gson gson = new Gson();

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
                                Date dateTime = mDateFormat.parse(time.getAsString());

                                ClassTime classTime = new ClassTime(dateTime);
                                dbWrapper.createClassTime(classTime);
                                place.addClassTime(context, classTime);
                            }

                            currentUser.favorite_places.add(place);

                            dbWrapper.createOrUpdateUser(currentUser);

                            dbWrapper.createOrUpdatePlace(place);
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

        if (currentUser == null) {

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

}
