package co.tapfit.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import co.tapfit.android.R;
import co.tapfit.android.adapter.PlaceListAdapter;
import co.tapfit.android.database.DatabaseWrapper;

/**
 * Created by zackmartinsek on 9/9/13.
 */
public class MapListFragment extends Fragment {

    View mView;
    ListView mPlaceList;
    DatabaseWrapper dbWrapper;

    public static final String LIST_TYPE = "list_type";
    public static final int MAP_LIST = 0;
    public static final int FAVORITE_LIST = 1;

    PlaceListAdapter mPlaceListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map_list, null);
        dbWrapper = DatabaseWrapper.getInstance(getActivity().getApplicationContext());
        if (getArguments().getInt(LIST_TYPE) == MAP_LIST) {
            initializeMapList();
        }
        else if (getArguments().getInt(LIST_TYPE) == FAVORITE_LIST) {
            initializeFavoriteList();
        }


        return mView;
    }

    private void initializeMapList() {
        mPlaceList = (ListView) mView.findViewById(R.id.place_list);

        mPlaceListAdapter = new PlaceListAdapter(getActivity(), dbWrapper.getPlaces());

        mPlaceList.setAdapter(mPlaceListAdapter);
    }

    private void initializeFavoriteList() {
        mPlaceList = (ListView) mView.findViewById(R.id.place_list);
        //mPlaceListAdapter = new PlaceListAdapter(getActivity(), dbWra)
    }


}
