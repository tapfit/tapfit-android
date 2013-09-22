package co.tapfit.android.request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import co.tapfit.android.helper.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import co.tapfit.android.helper.DateTimeDeserializer;
import co.tapfit.android.helper.StopWatch;
import co.tapfit.android.model.ClassTime;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;
import co.tapfit.android.service.ApiService;

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class PlaceRequest extends Request {

    private static String TAG = PlaceRequest.class.getSimpleName();

    private static boolean mWaitingForPlacesResponse = false;

    public static final String LAT = "lat";
    public static final String LON = "lon";

    private static StopWatch sw;

    public static boolean getPlaces(final Context context, LatLng location, boolean forceCall, ResponseCallback callback)
    {
        setDatabaseWrapper(context);

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        if (mWaitingForPlacesResponse && !forceCall)
            return true;

        ResultReceiver receiver = new ResultReceiver(new Handler()){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    mWaitingForPlacesResponse = false;
                    Log.d(TAG, "Failed to get result");

                    Iterator iterator = callbacks.iterator();
                    while (iterator.hasNext()) {
                        ResponseCallback cb = (ResponseCallback) iterator.next();
                        cb.sendCallback(null, "Failed to get back result");
                        iterator.remove();
                    }
                }
                else
                {
                    String json = resultData.getString(ApiService.REST_RESULT);
                    Log.d(TAG, "code: " + resultCode + ", json: " + json);

                    JsonParser parser = new JsonParser();

                    try
                    {
                        JsonObject object = parser.parse(json).getAsJsonObject();

                        JsonArray array = object.getAsJsonArray("places");

                        //User currentUser = dbWrapper.getCurrentUser();

                        Log.d(TAG, "array count: " + array.size());

                        ExecutorService execs = Executors.newFixedThreadPool(6, new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable runnable) {
                                Thread thread = new Thread(runnable);

                                thread.setPriority(Thread.MAX_PRIORITY - 3);

                                return thread;
                            }
                        });

                        List<Future<Integer>> results = new ArrayList<Future<Integer>>();

                        for (JsonElement element : array)
                        {
                            Future<Integer> result = execs.submit(new ProcessPlace(element));
                            results.add(result);
                        }

                        execs.shutdown();

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e, e);
                    }

                    mWaitingForPlacesResponse = false;
                    Iterator iterator = callbacks.iterator();

                    while (iterator.hasNext()) {
                        ResponseCallback cb = (ResponseCallback) iterator.next();
                        cb.sendCallback(dbWrapper.getPlaces(), "Success");
                        iterator.remove();
                    }

                    sw.stop();
                    Log.d(TAG, "places request took: " + sw.getElapsedTime() + "ms");
                }
            }
        };

        mWaitingForPlacesResponse = true;

        sw = new StopWatch();
        sw.start();

        Bundle args = new Bundle();
        args.putDouble(LAT, location.latitude);
        args.putDouble(LON, location.longitude);

        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "places");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
        intent.putExtra(ApiService.PARAMS, args);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);

        return false;
    }

    private static class ProcessPlace implements Callable<Integer> {

        private final JsonElement element;

        private ProcessPlace(JsonElement jsonElement){
            element = jsonElement;
        }

        public Integer call() throws InterruptedException {
            return parsePlaceJson(element).id;
        }
    }

    public static Place parsePlaceJson(JsonElement element) {
        Place place = gson.fromJson(element, Place.class);

        for (JsonElement time : element.getAsJsonObject().getAsJsonArray("class_times")) {
            //Log.d(TAG, "place: " + place.name + ", class_time: " + time);
            DateTime dateTime = DateTime.parse(time.getAsString());

            ClassTime classTime = new ClassTime(dateTime);
            classTime.place = place;
            dbWrapper.createClassTime(classTime);
            place.addClassTime(dbWrapper, classTime);
        }

        Log.d(TAG, "place: + " + place.name + " times: " + place.classTimes.size());

        Place oldPlace = dbWrapper.getPlace(place.id);
        if (oldPlace != null) {
            place.user = oldPlace.user;
        }

        dbWrapper.createOrUpdatePlace(place);

        return place;
    }

    public static void favoritePlace(final Context context, final Place place, final User user, final ResponseCallback callback) {
        setDatabaseWrapper(context);

        ResultReceiver receiver = new ResultReceiver(new Handler()){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                    callback.sendCallback(null, "Failed Request");
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

                        JsonElement element = object.get("success_code");
                        if (element == null) {
                            if (callback != null)
                                callback.sendCallback(null, "Failed to favorite");
                        }
                        else
                        {
                            if (element.getAsInt() == 0) {
                                if (place.user == user) {
                                    dbWrapper.removePlaceFromFavorites(user, place);
                                }
                            }
                            else {
                                if (place.user == null) {
                                    dbWrapper.addPlaceToFavorites(user, place);
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e.getMessage());
                        if (callback != null)
                            callback.sendCallback(null, "Failed to favorite");
                        return;
                    }
                    if (callback != null)
                        callback.sendCallback(place, "Success");
                }
            }

        };

        Bundle args = new Bundle();
        args.putString(AUTH_TOKEN, user.auth_token);

        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "places/" + place.id + "/favorite");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.POST);
        intent.putExtra(ApiService.PARAMS, args);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);
    }

    public static void getWorkouts(final Context context, final Place place, final ResponseCallback callback)
    {
        setDatabaseWrapper(context);

        ResultReceiver receiver = new ResultReceiver(new Handler()){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");
                    callback.sendCallback(null, "Failed Request");
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

                    ArrayList<Workout> workouts = new ArrayList<Workout>();

                    try
                    {
                        JsonArray array = parser.parse(json).getAsJsonArray();

                        for (JsonElement element : array)
                        {
                            Workout workout = gson.fromJson(element, Workout.class);

                            workout.place = place;

                            dbWrapper.createOrUpdatePlace(place);
                            dbWrapper.createOrUpdateWorkout(workout);
                            dbWrapper.createOrUpdateInstructor(workout.instructor);

                            workouts.add(workout);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e.getMessage());
                        callback.sendCallback(null, "Failed to get workouts");
                        return;
                    }

                    callback.sendCallback(workouts, "Success");
                }
            }

        };

        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "places/" + place.id + "/workouts");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);
    }

}
