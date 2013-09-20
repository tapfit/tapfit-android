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
import co.tapfit.android.model.Place;
import co.tapfit.android.model.Region;
import co.tapfit.android.service.ApiService;

/**
 * Created by zackmartinsek on 9/19/13.
 */
public class RegionRequest extends Request {

    private static final String TAG = RegionRequest.class.getSimpleName();

    public static void getRegions(final Context context, final ResponseCallback callback) {

        setDatabaseWrapper(context);

        if (callback != null && !callbacks.contains(callback)){
            callbacks.add(callback);
        }

        ResultReceiver receiver = new ResultReceiver(new Handler()){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == 0)
                {
                    Log.d(TAG, "Failed to get result");

                    if (callback != null){
                        callback.sendCallback(dbWrapper.getRegions(), "Failed to get regions");
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

                    try
                    {
                        JsonArray array = parser.parse(json).getAsJsonArray();

                        Log.d(TAG, "array count: " + array.size());

                        for (JsonElement element : array)
                        {
                            Region region = gson.fromJson(element, Region.class);

                            dbWrapper.createOrUpdateRegion(region);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "Exception: " + e, e);
                    }

                    if (callback != null){
                        callback.sendCallback(dbWrapper.getRegions(), "Success getting regions");
                    }
                }
            }
        };

        Intent intent = new Intent(context, ApiService.class);
        intent.putExtra(ApiService.URL, getUrl(context) + "regions");
        intent.putExtra(ApiService.HTTP_VERB, ApiService.GET);
        intent.putExtra(ApiService.RESULT_RECEIVER, receiver);

        context.startService(intent);
    }
}
