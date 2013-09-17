package co.tapfit.android.fragment;

import android.os.Bundle;

import co.tapfit.android.MapListActivity;
import co.tapfit.android.helper.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.model.Place;

/**
 * Created by zackmartinsek on 9/9/13.
 */
public class PlaceMapFragment extends SupportMapFragment {

    private static String TAG = PlaceMapFragment.class.getSimpleName();
    private GoogleMap mMap;

    private DatabaseWrapper dbWrapper;

    public static PlaceMapFragment newInstance(GoogleMapOptions options)
    {
        return (PlaceMapFragment) SupportMapFragment.newInstance(options);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MapListActivity) getActivity()).getBottomButton().setVisibility(View.VISIBLE);
        ((MapListActivity) getActivity()).getBottomButtonText().setText("View List");
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        dbWrapper = DatabaseWrapper.getInstance(getActivity().getApplicationContext());

        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e){
            e.printStackTrace();
        }

        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(getMap() != null){
            Log.v(TAG, "Map ready for use!");
            mMap = getMap();
            initMap();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        setMapTransparent((ViewGroup) root);

        return root;
    }

    private void initMap() {

        CameraPosition position = CameraPosition.builder()
                .target(LocationServices.getInstance(getActivity().getApplicationContext()).getLatLng())
                .zoom(14)
                .build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(position);

        getMap().setMyLocationEnabled(true);
        getMap().moveCamera(cameraUpdate);
        UiSettings uiSettings = getMap().getUiSettings();

        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(true);

        addLocationIcons();
    }

    private void addLocationIcons(){

        List<Place> places = dbWrapper.getPlaces();

        if (places == null){
            return;
        }

        Log.d(TAG, "places count: " + places.size());
        for (Place place : places)
        {
            Log.d(TAG, "place to put marker: " + place.name);
            getMap().addMarker(new MarkerOptions()
                    .position(new LatLng(place.address.lat, place.address.lon))
                    .title(place.name)
                    .snippet(place.address.line1)
                    .draggable(false));
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
}
