package co.tapfit.android.request;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import co.tapfit.android.R;
import co.tapfit.android.database.DatabaseWrapper;

/**
 * Created by zackmartinsek on 9/8/13.
 */
public class Request {

    protected static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static final String AUTH_TOKEN = "auth_token";

    protected static String getUrl(Context context)
    {
        return context.getString(R.string.tapfit_api);
    }

    protected static DatabaseWrapper dbWrapper;

    protected static void setDatabaseWrapper(Context context)
    {
        dbWrapper = DatabaseWrapper.getInstance(context.getApplicationContext());
    }

    protected static ArrayList<ResponseCallback> callbacks = new ArrayList<ResponseCallback>();
}
