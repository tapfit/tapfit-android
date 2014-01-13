package co.tapfit.android.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import co.tapfit.android.MapListActivity;
import co.tapfit.android.PlaceInfoActivity;
import co.tapfit.android.helper.Log;

import co.tapfit.android.R;

import android.os.Debug;
import android.os.Handler;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogRecord;

import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.helper.RegionBounds;
import co.tapfit.android.model.Place;
import co.tapfit.android.request.PlaceRequest;
import co.tapfit.android.view.TouchableWrapper;
import pl.mg6.android.maps.extensions.ClusterOptions;
import pl.mg6.android.maps.extensions.ClusterOptionsProvider;
import pl.mg6.android.maps.extensions.ClusteringSettings;
import pl.mg6.android.maps.extensions.DefaultClusterOptionsProvider;
import pl.mg6.android.maps.extensions.GoogleMap;
import pl.mg6.android.maps.extensions.Marker;
import pl.mg6.android.maps.extensions.MarkerOptions;
import pl.mg6.android.maps.extensions.SupportMapFragment;

/**
 * Created by zackmartinsek on 9/9/13.
 */
public class PlaceMapFragment extends SupportMapFragment implements TouchableWrapper.UpdateMapAfterUserInterection {

    private static String TAG = PlaceMapFragment.class.getSimpleName();
    private GoogleMap mMap;

    private DatabaseWrapper dbWrapper;

    private Handler handler = new Handler();

    private CameraPosition mCameraPosition;
    private LatLngBounds mCurrentBounds;

    private CameraPosition mMapLocation;

    private Integer mSentCallToDB = 0;

    private Boolean mIsTouched = false;

    private View mContentView;
    public TouchableWrapper mTouchView;

    private Boolean mHasShownOutOfAreaMessage = false;

    private HashMap<String, Place> mMarkerIds = new HashMap<String, Place>();

    @Override
    public void onResume() {
        super.onResume();
        ((MapListActivity) getParentActivity()).getBottomButton().setVisibility(View.VISIBLE);
        ((MapListActivity) getParentActivity()).getBottomButtonText().setText("View List");
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        dbWrapper = DatabaseWrapper.getInstance(getParentActivity().getApplicationContext());

        try {
            MapsInitializer.initialize(this.getParentActivity());
        } catch (GooglePlayServicesNotAvailableException e){
            e.printStackTrace();
        }

        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        setMapTransparent((ViewGroup) mContentView);

        mTouchView = new TouchableWrapper(getActivity(), this);
        mTouchView.addView(mContentView);

        onMapReady();

        return mTouchView;
    }

    private void onMapReady() {
        if(getExtendedMap() != null && mMap == null) {
            Log.v(TAG, "Map ready for use!");
            mMap = getExtendedMap();
        }

        if (mMap != null) {
            initMap();
        }
    }

    @Override
    public View getView() {
        return mContentView;
    }

    public CameraPosition getMapLocationCenter()
    {
        return mMap.getCameraPosition();
    }


    private GoogleMap.OnCameraChangeListener cameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

            Log.d(TAG, "onCameraChange: " + mMap.getCameraPosition().target.latitude + ", " + mMap.getCameraPosition().target.longitude);

            if (mIsTouched) {
                mIsTouched = false;

                mLoadingBar.setVisibility(View.VISIBLE);
                mSentCallToDB++;
                Log.d(TAG, "Sending places call. mSentCallToDB: " + mSentCallToDB);
                ((MapListActivity) getParentActivity()).getPlaces(cameraPosition.target);
                mCameraPosition = cameraPosition;

                if (RegionBounds.isInRegion(cameraPosition.target)) {
                    mHasShownOutOfAreaMessage = false;
                    ((MapListActivity) getActivity()).setHasShownOutOfAreaMessage(false);
                }
                else if (!mHasShownOutOfAreaMessage) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setMessage("Purchasing is only available in select U.S. cities at the moment. You can still use TapFit to find nearby gyms and studios!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .create();
                    alertDialog.show();
                    mHasShownOutOfAreaMessage = true;
                    ((MapListActivity) getActivity()).setHasShownOutOfAreaMessage(true);
                }
            }

            addLocationIcons();
            /*if (Place.distanceBetweenPoints(cameraPosition.target, mCameraPosition.target) > 20) {
                mLoadingBar.setVisibility(View.VISIBLE);
                ((MapListActivity) getParentActivity()).getPlaces(cameraPosition.target);
                mCurrentBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                mCameraPosition = cameraPosition;
            }
            else if (Math.abs(cameraPosition.zoom - mCameraPosition.zoom) > 3.0f) {
                mLoadingBar.setVisibility(View.VISIBLE);
                ((MapListActivity) getParentActivity()).getPlaces(cameraPosition.target);
                mCurrentBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                mCameraPosition = cameraPosition;
            }

            if (!mCurrentBounds.contains(cameraPosition.target)){
                mLoadingBar.setVisibility(View.VISIBLE);
                ((MapListActivity) getParentActivity()).getPlaces(cameraPosition.target);
                mCurrentBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                mCameraPosition = cameraPosition;
            }*/

        }
    };


    public void receivedPlacesCallback() {
        addLocationIcons();
        mSentCallToDB--;
        Log.d(TAG, "Received places call. mSentCallToDB: " + mSentCallToDB);
        if (mSentCallToDB <= 0) {
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLoadingBar != null) {
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
    }


    private GoogleMap.OnInfoWindowClickListener clickMarker = new GoogleMap.OnInfoWindowClickListener() {

        public void onInfoWindowClick(Marker marker) {
            Place place = (Place) marker.getData();

            Intent intent = new Intent(getParentActivity(), PlaceInfoActivity.class);

            intent.putExtra(PlaceInfoActivity.PLACE_ID, place.id);

            getParentActivity().startActivity(intent);
        }
    };

    private void initMap() {

        Log.d(TAG, "initMap() - mMapLocation: " + mMapLocation.target.latitude + ", " + mMapLocation.target.longitude + ", zoom: " + mMapLocation.zoom);

        ClusteringSettings clusteringSettings = new ClusteringSettings();
        clusteringSettings.addMarkersDynamically(true);
        mMap.setClustering(clusteringSettings);

        mMap.setOnInfoWindowClickListener(clickMarker);
        mMap.setOnCameraChangeListener(cameraChangeListener);

        CameraPosition position;

        if (mMapLocation != null) {
            position = CameraPosition.builder()
                    .target(mMapLocation.target)
                    .zoom(mMapLocation.zoom)
                    .build();
        }
        else
        {
            position = CameraPosition.builder()
                    .target(LocationServices.getInstance(getParentActivity().getApplicationContext()).getLatLng())
                    .zoom(14)
                    .build();
        }

        mCameraPosition = position;

        mCurrentBounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(position);

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(cameraUpdate);
        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(true);

        addLocationIcons();
    }

    private void addLocationIcons(){

        final CameraPosition cameraPosition = mMap.getCameraPosition();

        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {

                final List<Place> places = dbWrapper.getPlaces(cameraPosition.target, 50);

                //final List<Place> places = new ArrayList<Place>();
                Log.d(TAG, "Camera Position: " + cameraPosition.target.latitude + ", " + cameraPosition.target.longitude);

                if (places == null){
                    return;
                }

                if (mMarkerIds == null) {
                    mMarkerIds = new HashMap<String, Place>();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "places count: " + places.size());
                        for (Place place : places)
                        {
                            if (mMarkerIds.containsValue(place)){
                                continue;
                            }

                            MarkerOptions options = new MarkerOptions()
                                    .position(new LatLng(place.address.lat, place.address.lon))
                                    .title(place.name)
                                    .snippet(place.address.line1)
                                    .draggable(false)
                                    .data(place)
                                    .icon(getIcon(place.category));

                            Marker marker = mMap.addMarker(options);

                            mMarkerIds.put(marker.getId(), place);
                        }

                        Log.d(TAG, "Finished adding locations");
                    }
                });

            }
        });

        newThread.start();
    }


    private BitmapDescriptor getIcon(String category) {

        if (category.equals("Yoga")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.map_pin_yoga);
        }
        else if (category.equals("Pilates Barre") || category.equals("Dance")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.map_pin_barre);
        }
        else if (category.equals("Cardio")) {
            return BitmapDescriptorFactory.fromResource(R.drawable.map_pin_cardio);
        }
        else {
            return BitmapDescriptorFactory.fromResource(R.drawable.map_pin_gym);
        }
    }

    private void setMapTransparent(ViewGroup group)
    {
        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            View child = group.getChildAt(i);
            if (child instanceof ViewGroup)
            {
                setMapTransparent((ViewGroup) child);
            }
            else if (child instanceof SurfaceView)
            {
                child.setBackgroundColor(0x00000000);
            }
        }
    }

    private ProgressBar mLoadingBar;

    public void setLoadingBar(ProgressBar loadingBar) {
        mLoadingBar = loadingBar;
    }

    public void setLocation(CameraPosition location) {
        mMapLocation = location;
    }

    @Override
    public void onUpdateMapAfterUserInterection() {
        mIsTouched = true;
    }

    public void setHasShowOutOfAreaMessage(Boolean hasShownOutOfAreaMessage) {
        mHasShownOutOfAreaMessage = hasShownOutOfAreaMessage;
    }
}
