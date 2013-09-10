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

    PlaceListAdapter mPlaceListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map_list, null);
        dbWrapper = DatabaseWrapper.getInstance(getActivity().getApplicationContext());
        initializePlaceList();

        return mView;
    }

    private void initializePlaceList() {
        mPlaceList = (ListView) mView.findViewById(R.id.place_list);

        mPlaceListAdapter = new PlaceListAdapter(getActivity(), dbWrapper.getPlaces());

        mPlaceList.setAdapter(mPlaceListAdapter);
    }


}
