package co.tapfit.android.request;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import co.tapfit.android.R;
import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.DateTimeDeserializer;

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class Request {

    protected static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
           // .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
           // .create();

    protected static Gson getGson(){
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
            .create();
        return gson;
    }


    public static final String AUTH_TOKEN = "auth_token";

    public static String getUrl(Context context)
    {
        if (Boolean.parseBoolean(context.getString(R.string.is_debug)))
        {
            return context.getString(R.string.debug_tapfit_api);
        }
        else
        {
            return context.getString(R.string.tapfit_api);
        }
    }

    protected static DatabaseWrapper dbWrapper;

    protected static void setDatabaseWrapper(Context context)
    {
        dbWrapper = DatabaseWrapper.getInstance(context.getApplicationContext());
    }

    protected static ArrayList<ResponseCallback> callbacks = new ArrayList<ResponseCallback>();
}
