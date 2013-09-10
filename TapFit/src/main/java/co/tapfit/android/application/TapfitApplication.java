package co.tapfit.android.application;

import android.app.Application;

import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.ImageCache;

/**
 * Created by zackmartinsek on 9/9/13.
 */
public class TapfitApplication extends Application {

    protected DatabaseWrapper dbWrapper;

    @Override
    public void onCreate() {

        super.onCreate();

        dbWrapper = DatabaseWrapper.getInstance(getApplicationContext());

        ImageCache.initImageLoader(getApplicationContext());
    }

}
