package co.tapfit.android.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.tapfit.android.R;

/**
 * Created by zackmartinsek on 9/16/13.
 */
public class TapfitInfoFragment extends BaseFragment {

    private static final String TAG = TapfitInfoFragment.class.getSimpleName();
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tapfit_info, null);

        setUpTapfitInfo();

        return mView;
    }

    private void setUpTapfitInfo() {

        setUpNavButton(R.id.about, "ABOUT TAPFIT", null);

        setUpNavButton(R.id.privacy, "PRIVACY", null);

        setUpNavButton(R.id.terms, "TERMS", null);

        setUpNavButton(R.id.faq, "FAQ", null);

    }

    private void setUpNavButton(Integer resource, String title, View.OnClickListener listener) {

        View view = mView.findViewById(resource);
        ((TextView) view.findViewById(R.id.content_title)).setText(title);
        view.setClickable(true);
        view.setOnClickListener(listener);
        int padding = (int) getResources().getDimension(R.dimen.padding);
        view.setPadding(padding, 0, padding, 0);
    }
}
