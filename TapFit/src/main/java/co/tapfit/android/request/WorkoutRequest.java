package co.tapfit.android.request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;

import java.util.Iterator;

import co.tapfit.android.helper.DateTimeDeserializer;
import co.tapfit.android.helper.Log;
import co.tapfit.android.model.ClassTime;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;
import co.tapfit.android.service.ApiService;

/**
 * Created by zackmartinsek on 9/18/13.
 */
public class WorkoutRequest extends Request {

    private static final String TAG = WorkoutRequest.class.getSimpleName();

    public static boolean buyWorkout(final Context context, final Workout workout, final ResponseCallback callback)
    {
        setDatabaseWrapper(context);

        ResultReceiver receiver = new ResultReceiver(new Handler()){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                    if (callback != null) {
                        callback.sendCallback(null, "Failed to buy workout");
                    }
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                            .create();

                    JsonParser parser = new JsonParser();

                    Pass pass = null;
                    String message = "";

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        if (object.get("success").getAsBoolean()) {

                            JsonElement receipt = object.get("receipt");

                            pass = UserRequest.parsePassJson(receipt);

                            message = "Success buying pass";
                        }
                        else
                        {
                            message = object.get("error_message").getAsString();
                        }

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e, e);
                        message = "Error getting buy workout payload";
                    }

                    if (callback != null) {
                        callback.sendCallback(pass, message);
                    }
                }
            }

        };

        User user = dbWrapper.getCurrentUser();

        if (user != null)
        {
            Bundle args = new Bundle();
            args.putString(AUTH_TOKEN, user.auth_token);

            Intent intent = new Intent(context, ApiService.class);
            intent.putExtra(ApiService.URL, getUrl(context) + "places/" + workout.place.id + "/workouts/" + workout.id + "/buy");
            intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
            intent.putExtra(ApiService.PARAMS, args);
            intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

            context.startService(intent);
        }
        else
        {
            if (callback != null){
                callback.sendCallback(null, "Need to be signed in");
            }
        }

        return false;
    }
}
