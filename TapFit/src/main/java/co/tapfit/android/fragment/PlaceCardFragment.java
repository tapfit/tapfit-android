package co.tapfit.android.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import co.tapfit.android.helper.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.tapfit.android.PlaceInfoActivity;
import co.tapfit.android.R;
import co.tapfit.android.SignInActivity;
import co.tapfit.android.adapter.PlaceScheduleListAdapter;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;
import co.tapfit.android.request.PlaceRequest;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;

/**
 * Created by zackmartinsek on 9/11/13.
 */
public class PlaceCardFragment extends BaseFragment {

    private static final String TAG = PlaceCardFragment.class.getSimpleName();
    private View mView;
    private Place mPlace;

    private ImageButton mSaveButton;

    private ListView mWorkoutList;
    private PlaceScheduleListAdapter mWorkoutListAdapter;

    private Boolean toggledFavorite = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_place_card, null);

        mPlace = dbWrapper.getPlace(getArguments().getInt(PlaceInfoActivity.PLACE_ID, -1));

        setUpPlaceCardViews();

        return mView;
    }

    private void setUpPlaceCardViews() {

        ImageView imageView = (ImageView) mView.findViewById(R.id.place_image);
        imageCache.loadImageForPlacePage(imageView, mPlace.cover_photo);

        TextView textView = (TextView) mView.findViewById(R.id.place_description);
        textView.setText(mPlace.source_description);

        textView = (TextView) mView.findViewById(R.id.place_name);
        textView.setText(mPlace.name);

        textView = (TextView) mView.findViewById(R.id.description_header);
        textView.setText("About " + mPlace.name);

        textView = (TextView) mView.findViewById(R.id.place_distance);
        textView.setText(String.format("%.1f", mPlace.getDistance()) + " miles away");

        mWorkoutListAdapter = new PlaceScheduleListAdapter(getActivity(), dbWrapper.getUpcomingWorkouts(mPlace));

        mWorkoutList = (ListView) mView.findViewById(R.id.upcoming_class_list);
        mWorkoutList.setAdapter(mWorkoutListAdapter);
        mWorkoutList.setOnItemClickListener(openClassSchedule);

        mWorkoutList.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, getListViewHeight()));

        ImageView directionsMap = (ImageView) mView.findViewById(R.id.directions_map);

        setUpSaveButton();

        String mapUrl = getGoogleMapsStaticUrl();

        Log.d(TAG, "Map URL: " + mapUrl);

        imageCache.loadImageForPlacePage(directionsMap, mapUrl);

        directionsMap.setClickable(true);

        directionsMap.setOnClickListener(openGoogleMaps);

        TextView phoneNumber = (TextView) mView.findViewById(R.id.phone_number);
        phoneNumber.setText(mPlace.phone_number);

        RelativeLayout callPhoneNumber = (RelativeLayout) mView.findViewById(R.id.call_phone_number);
        callPhoneNumber.setClickable(true);
        callPhoneNumber.setOnClickListener(callNumber);

        PlaceRequest.getWorkouts(getActivity(), mPlace, workoutResponseCallback);

    }

    private AdapterView.OnItemClickListener openClassSchedule = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Workout workout = (Workout) adapterView.getItemAtPosition(i);
            if (workout.equals(PlaceScheduleListAdapter.BOTTOM_BAR)) {
                ((PlaceInfoActivity) getActivity()).openClassSchedule(mPlace.id);
            }
        }
    };


    private void setUpSaveButton() {

        mSaveButton = (ImageButton) mView.findViewById(R.id.place_favorite_button);

        User user = dbWrapper.getCurrentUser();
        if (user != null) {
            if (mPlace.favorite_place != null && mPlace.favorite_place.id == user.id) {

                mSaveButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.save_button_saved));
                mSaveButton.setTag(true);
            }
            else
            {
                mSaveButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.save_button_unsaved));
                mSaveButton.setTag(false);
            }
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                User user = dbWrapper.getCurrentUser();

                if (user == null) {
                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                    startActivityForResult(intent, 1);
                    return;
                }

                //TODO: Add logic for server
                if ((Boolean) mSaveButton.getTag()) {
                    mSaveButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.save_button_unsaved));
                    mSaveButton.setTag(false);
                    dbWrapper.removePlaceFromFavorites(user, mPlace);
                    toggledFavorite = !toggledFavorite;
                }
                else
                {
                    mSaveButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.save_button_saved));
                    mSaveButton.setTag(true);
                    dbWrapper.addPlaceToFavorites(user, mPlace);
                    toggledFavorite = !toggledFavorite;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == getActivity().RESULT_OK){
                Log.d(TAG, "Successfully signed in");
            }
            if (resultCode == getActivity().RESULT_CANCELED) {
                Log.d(TAG, "Didn't log in");
            }
        }
    }

    private String getGoogleMapsStaticUrl() {

        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();

        Log.d(TAG, "screen: " + width);

        width = width - imageCache.convertDpToPixels(40);
        int height = imageCache.convertDpToPixels(80);

        return "http://maps.googleapis.com/maps/api/staticmap?center=" + mPlace.address.lat + "," + mPlace.address.lon + "&zoom=19&size=" + width + "x" + height + "&sensor=false";
    }

    private View.OnClickListener callNumber = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String url = "tel:" + mPlace.phone_number;
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
            startActivity(intent);
        }
    };

    private View.OnClickListener openGoogleMaps = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String url = "http://maps.google.com/maps?saddr=" + LocationServices.getLatLng().latitude + "," + LocationServices.getLatLng().longitude + "&daddr="
                    + mPlace.address.lat + "," + mPlace.address.lon;

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(url));
            startActivity(intent);
        }
    };

    private int getListViewHeight() {
        return (int) (getActivity().getResources().getDimension(R.dimen.normal_button_size) * mWorkoutListAdapter.getCount());
    }

    ResponseCallback workoutResponseCallback = new ResponseCallback() {
        @Override
        public void sendCallback(Object responseObject, String message) {
            mWorkoutListAdapter.updateWorkouts(dbWrapper.getUpcomingWorkouts(mPlace));

            mWorkoutList.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, getListViewHeight()));

            mWorkoutList.invalidate();
        }
    };

    @Override
    public void onPause(){
        if (toggledFavorite) {
            Log.d(TAG, "About to favorite a place");
            PlaceRequest.favoritePlace(getActivity(), mPlace, dbWrapper.getCurrentUser(), null);
        }
        super.onPause();
    }
}
