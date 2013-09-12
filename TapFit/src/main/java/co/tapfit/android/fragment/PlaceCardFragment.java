package co.tapfit.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import co.tapfit.android.PlaceInfoActivity;
import co.tapfit.android.R;
import co.tapfit.android.model.Place;

/**
 * Created by zackmartinsek on 9/11/13.
 */
public class PlaceCardFragment extends BaseFragment {

    private View mView;
    private Place mPlace;

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

        textView = (TextView) mView.findViewById(R.id.place_distance);
        textView.setText(String.format("%.1f", mPlace.getDistance()) + " miles away");
    }
}
