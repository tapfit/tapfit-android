package co.tapfit.android.fragment;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import co.tapfit.android.helper.AnalyticsHelper;
import co.tapfit.android.helper.ImageCache;
import co.tapfit.android.helper.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;

import co.tapfit.android.PlaceInfoActivity;
import co.tapfit.android.R;
import co.tapfit.android.SignInActivity;
import co.tapfit.android.adapter.PlaceScheduleListAdapter;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.model.Address;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;
import co.tapfit.android.request.PlaceRequest;
import co.tapfit.android.request.ResponseCallback;

/**
 * Created by zackmartinsek on 9/11/13.
 */
public class PlaceCardFragment extends BaseFragment {

    private static final String TAG = PlaceCardFragment.class.getSimpleName();
    private View mView;
    private Place mPlace;

    private ImageButton mSaveButton;

    private FrameLayout mBottomButton;

    private ListView mWorkoutList;
    private PlaceScheduleListAdapter mWorkoutListAdapter;

    private Boolean toggledFavorite = false;

    @Override
    public void onResume() {
        super.onResume();

        mPlace = dbWrapper.getPlace(getArguments().getInt(PlaceInfoActivity.PLACE_ID, -1));

        HashMap<String, String> args = new HashMap<String, String>();
        args.put("Place", mPlace.name);

        AnalyticsHelper.getInstance(getActivity()).logEvent("Place Card", args);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_place_preview, null);

        mPlace = dbWrapper.getPlace(getArguments().getInt(PlaceInfoActivity.PLACE_ID, -1));

        setUpPlaceCardViews();

        return mView;
    }

    private void setUpPlaceCardViews() {

        ImageView imageView = (ImageView) mView.findViewById(R.id.place_image);
        if (mPlace.cover_photo == null) {
            imageCache.loadImageForPlacePage(imageView, ImageCache.getCoverPhotoUrl(mPlace.category));
        }
        else {
            imageCache.loadImageForPlacePage(imageView, mPlace.cover_photo);
        }

        /*TextView textView = (TextView) mView.findViewById(R.id.place_description);
        if (mPlace.source_description == null || mPlace.source_description.equals("")) {
            textView.setText(R.string.no_description);
        }
        else
        {
            textView.setText(mPlace.source_description);
        }*/

        TextView textView = (TextView) mView.findViewById(R.id.place_name);
        textView.setText(mPlace.name);

        textView = (TextView) mView.findViewById(R.id.category);
        textView.setText(mPlace.category);

        Address address = dbWrapper.getAddress(mPlace.address.id);
        textView = (TextView) mView.findViewById(R.id.address_line1);
        textView.setText(address.line1);

        textView = (TextView) mView.findViewById(R.id.address_line2);
        textView.setText(address.city + ", " + address.state + " " + address.zip);

        /*textView = (TextView) mView.findViewById(R.id.description_header);
        textView.setText("About " + mPlace.name);

        String addressString = address.line1;
        if (address.line2 != null){
            addressString = addressString + " " + address.line2;
        }
        addressString = addressString + "\n" + address.city + ", " + address.state;
        textView = (TextView) mView.findViewById(R.id.directions_header);
        textView.setText(addressString);

        ImageView directionsMap = (ImageView) mView.findViewById(R.id.directions_map);

        directionsMap.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageCache.convertDpToPixels(100)));

        //setUpSaveButton();
        */

        LinearLayout viewMore = (LinearLayout) mView.findViewById(R.id.view_more);
        viewMore.setClickable(true);
        viewMore.setOnClickListener(showMorePlaceInfo);

        mWorkoutList = (ListView) mView.findViewById(R.id.upcoming_class_list);

        mWorkoutListAdapter = new PlaceScheduleListAdapter(getActivity(), dbWrapper.getUpcomingWorkouts(mPlace), mPlace);
        mWorkoutList.setAdapter(mWorkoutListAdapter);

        mWorkoutList.setOnItemClickListener(openClassSchedule);

        mWorkoutList.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, getListViewHeight()));


        if (PlaceInfoActivity.WORKOUT_CALLBACK_RECEIVED) {
            receivedWorkouts();
        }

        /*String mapUrl = getGoogleMapsStaticUrl();

        Log.d(TAG, "Map URL: " + mapUrl);

        imageCache.loadImageForPlacePage(directionsMap, mapUrl);

        directionsMap.setClickable(true);

        directionsMap.setOnClickListener(openGoogleMaps);

        TextView phoneNumber = (TextView) mView.findViewById(R.id.phone_number);
        phoneNumber.setText(mPlace.phone_number);

        RelativeLayout callPhoneNumber = (RelativeLayout) mView.findViewById(R.id.call_phone_number);
        callPhoneNumber.setClickable(true);
        callPhoneNumber.setOnClickListener(callNumber);
        */

        mBottomButton = (FrameLayout) mView.findViewById(R.id.bottom_button);
        mBottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnalyticsHelper.getInstance(getActivity()).logEvent("Book a Pass");
                ((PlaceInfoActivity) getActivity()).openClassSchedule(mPlace.id);
            }
        });

        if (mPlace.can_buy == null || mPlace.can_buy == false) {
            mBottomButton.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener showMorePlaceInfo = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            ((PlaceInfoActivity) getActivity()).showMorePlaceInfo(mPlace.id);
        }
    };

    private AdapterView.OnItemClickListener openClassSchedule = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Workout workout = (Workout) adapterView.getItemAtPosition(i);
            if (workout.equals(PlaceScheduleListAdapter.BOTTOM_BAR)) {
                AnalyticsHelper.getInstance(getActivity()).logEvent("View Class Schedule");
                ((PlaceInfoActivity) getActivity()).openClassSchedule(mPlace.id);
            }
            else
            {
                HashMap<String, String> args = new HashMap<String, String>();
                args.put("Workout", String.valueOf(workout.id));
                AnalyticsHelper.getInstance(getActivity()).logEvent("View Class");
                ((PlaceInfoActivity) getActivity()).openWorkoutCardFromList(workout.id);
            }
        }
    };


    /*private void setUpSaveButton() {

        mSaveButton = (ImageButton) mView.findViewById(R.id.place_favorite_button);

        User user = dbWrapper.getCurrentUser();
        if (user != null) {
            if (mPlace.user != null && mPlace.user.id == user.id) {

                mSaveButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.save_button_saved));
                mSaveButton.setTag(true);
            }
            else
            {
                mSaveButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.save_button_unsaved));
                mSaveButton.setTag(false);
            }
        }
        else
        {
            mSaveButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.save_button_unsaved));
            mSaveButton.setTag(false);
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
    } */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == getActivity().RESULT_OK){
                User user = dbWrapper.getCurrentUser();
                Log.d(TAG, "Successfully signed in");
                mSaveButton.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.save_button_saved));
                mSaveButton.setTag(true);
                dbWrapper.addPlaceToFavorites(user, mPlace);
                toggledFavorite = !toggledFavorite;
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

        String url = "http://maps.googleapis.com/maps/api/staticmap?center=" + mPlace.address.lat + "," + mPlace.address.lon + "&zoom=19&size=" + width + "x" + height + "&sensor=false";

        Log.d(TAG, "Google Maps Static URL: " + url);

        return url;
    }

    private View.OnClickListener callNumber = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            HashMap<String, String> args = new HashMap<String, String>();
            args.put("Place", mPlace.name);

            AnalyticsHelper.getInstance(getActivity()).logEvent("Call Number", args);
            String url = "tel:" + mPlace.phone_number;
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
            startActivity(intent);
        }
    };

    private View.OnClickListener openGoogleMaps = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            HashMap<String, String> args = new HashMap<String, String>();
            args.put("Place", mPlace.name);
            AnalyticsHelper.getInstance(getActivity()).logEvent("Get Directions", args);
            String url = "http://maps.google.com/maps?saddr=" + LocationServices.getLatLng().latitude + "," + LocationServices.getLatLng().longitude + "&daddr="
                    + mPlace.address.lat + "," + mPlace.address.lon;

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(url));
            startActivity(intent);
        }
    };


    private int getListViewHeight() {
        Log.d(TAG, "getListViewHeight: mWorkoutListAdapter: " + mWorkoutListAdapter + ", getActivity: " + getActivity());
        return (int) (getActivity().getResources().getDimension(R.dimen.normal_button_size) * mWorkoutListAdapter.getCount());
    }


    public void receivedWorkouts(){

        mWorkoutListAdapter.updateWorkouts(dbWrapper.getUpcomingWorkouts(mPlace));

        mWorkoutList.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, getListViewHeight()));

        mWorkoutList.invalidate();
    }

    @Override
    public void onPause(){
        if (toggledFavorite) {
            Log.d(TAG, "About to favorite a place");
            PlaceRequest.favoritePlace(getActivity(), mPlace, dbWrapper.getCurrentUser(), null);
        }
        super.onPause();
    }
}
