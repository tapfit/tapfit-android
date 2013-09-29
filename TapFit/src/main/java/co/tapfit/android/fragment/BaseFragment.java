package co.tapfit.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.flurry.android.FlurryAgent;

import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.ImageCache;
import co.tapfit.android.helper.LocationServices;

/**
 * Created by zackmartinsek on 9/11/13.
 */
public class BaseFragment extends Fragment {

    protected DatabaseWrapper dbWrapper;
    protected ImageCache imageCache;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        imageCache = ImageCache.getInstance();
        dbWrapper = DatabaseWrapper.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public void onResume(){
        super.onResume();

        FlurryAgent.onPageView();

        LocationServices.getInstance(getActivity().getApplicationContext());
    }
}
