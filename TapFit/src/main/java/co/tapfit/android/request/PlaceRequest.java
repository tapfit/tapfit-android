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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import co.tapfit.android.model.ClassTime;
import co.tapfit.android.model.Place;
import co.tapfit.android.service.ApiService;

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class PlaceRequest extends Request {

    private static String TAG = PlaceRequest.class.getSimpleName();

    private static boolean mWaitingForPlacesResponse = false;
    private static boolean mWaitingForPlaceResponse = false;

    public static boolean getPlaces(Context context, ResponseCallback callback)
    {
        setDatabaseWrapper(context);

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        if (mWaitingForPlacesResponse)
            return true;

        ResultReceiver receiver = new ResultReceiver(new Handler()){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    mWaitingForPlacesResponse = false;
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

                        for (JsonElement element : array)
                        {
                            Place place = gson.fromJson(element, Place.class);

                            for (JsonElement time : element.getAsJsonObject().getAsJsonArray("class_times")) {
                                Date dateTime = mDateFormat.parse(time.getAsString());

                                ClassTime classTime = new ClassTime(dateTime);
                                dbWrapper.createClassTime(classTime);
                                place.classTimes.add(classTime);
                            }

                            dbWrapper.createOrUpdatePlace(place);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e);
                    }

                    mWaitingForPlacesResponse = false;
                    Iterator iterator = callbacks.iterator();
                    while (iterator.hasNext()) {
                        ResponseCallback cb = (ResponseCallback) iterator.next();
                        cb.sendCallback(json, json);
                        callbacks.remove(cb);
                    }
                }
            }

        };

        mWaitingForPlacesResponse = true;

        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "places");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);

        return false;
    }

}
