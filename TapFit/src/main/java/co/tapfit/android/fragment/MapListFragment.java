package co.tapfit.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import co.tapfit.android.MapListActivity;
import co.tapfit.android.PlaceInfoActivity;
import co.tapfit.android.R;
import co.tapfit.android.adapter.PlaceListAdapter;
import co.tapfit.android.database.DatabaseWrapper;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.model.Place;

/**
 * Created by zackmartinsek on 9/9/13.
 */
public class MapListFragment extends BaseFragment {

    View mView;
    ListView mPlaceList;

    public static final String LIST_TYPE = "list_type";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final int MAP_LIST = 0;
    public static final int FAVORITE_LIST = 1;

    private static int mCurrentListType = MAP_LIST;

    private static LatLng mLocation;

    PlaceListAdapter mPlaceListAdapter;

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments().getInt(LIST_TYPE) == MAP_LIST) {
            ((MapListActivity) getActivity()).getBottomButton().setVisibility(View.VISIBLE);
            ((MapListActivity) getActivity()).getBottomButtonText().setText("View Map");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map_list, null);

        mCurrentListType = getArguments().getInt(LIST_TYPE);

        if (mCurrentListType == MAP_LIST) {
            mLocation = new LatLng(getArguments().getDouble(LAT, LocationServices.getLatLng().latitude), getArguments().getDouble(LON, LocationServices.getLatLng().longitude));
            initializeMapList();
        }
        else if (mCurrentListType == FAVORITE_LIST) {
            initializeFavoriteList();
        }


        return mView;
    }

    private void initializeMapList() {
        mPlaceList = (ListView) mView.findViewById(R.id.place_list);
        List<Place> places = dbWrapper.getPlaces(mLocation, 35);

        if (places != null) {

            mPlaceListAdapter = new PlaceListAdapter(getActivity(), places);

            mPlaceList.setAdapter(mPlaceListAdapter);

            mPlaceList.setOnItemClickListener(placeListClickListener);
        }
    }

    public void receivedPlacesCallback() {
        if (mCurrentListType == MAP_LIST) {
            updatePlaceList();
        }
    }

    private void updatePlaceList() {
        List<Place> places = dbWrapper.getPlaces(mLocation, 35);
        if (places != null) {
            mPlaceListAdapter.replaceAll(places);
        }
    }

    private void initializeFavoriteList() {
        mPlaceList = (ListView) mView.findViewById(R.id.place_list);

        List<Place> places = dbWrapper.getFavorites();

        if (places != null) {

            mPlaceListAdapter = new PlaceListAdapter(getActivity(), dbWrapper.getFavorites());

            mPlaceList.setAdapter(mPlaceListAdapter);

            mPlaceList.setOnItemClickListener(placeListClickListener);
        }
    }

    private AdapterView.OnItemClickListener placeListClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            Place place = (Place) adapterView.getItemAtPosition(position);

            Intent intent = new Intent(getActivity(), PlaceInfoActivity.class);

            intent.putExtra(PlaceInfoActivity.PLACE_ID, place.id);

            startActivity(intent);
        }
    };

}
