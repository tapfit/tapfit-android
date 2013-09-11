package co.tapfit.android.request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import co.tapfit.android.R;
import co.tapfit.android.database.DatabaseHelper;
import co.tapfit.android.model.User;
import co.tapfit.android.service.ApiService;

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class UserRequest extends Request {

    private static String TAG = UserRequest.class.getSimpleName();

    public static void registerGuest(final Context context, ResponseCallback callback) {

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

                    try
                    {
                        JsonElement element = parser.parse(json);

                        User user = gson.fromJson(element, User.class);

                        DatabaseHelper helper = new DatabaseHelper(context);

                        helper.getUserDao().createOrUpdate(user);

                        Log.d(TAG, "Registered a guest user: " + user.auth_token);

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
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

    public static void registerUser(final Context context, Bundle args, ResponseCallback callback) {

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

                    try
                    {
                        JsonElement element = parser.parse(json);

                        User user = gson.fromJson(element, User.class);

                        DatabaseHelper helper = new DatabaseHelper(context);

                        helper.getUserDao().createOrUpdate(user);

                        //Log.d(TAG, "Registered a guest user: " + user.auth_token);

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
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

}
