package co.tapfit.android.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.tapfit.android.R;
import co.tapfit.android.WebViewActivity;

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

        setUpNavButton(R.id.about, "ABOUT TAPFIT", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(WebViewActivity.URL, getResources().getString(R.string.tapfit_url) + "about");
                intent.putExtra(WebViewActivity.TITLE, "About TapFit");
                startActivity(intent);
            }
        });

        setUpNavButton(R.id.privacy, "PRIVACY", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(WebViewActivity.URL, getResources().getString(R.string.tapfit_url) + "privacy");
                intent.putExtra(WebViewActivity.TITLE, "Privacy");
                startActivity(intent);
            }
        });

        setUpNavButton(R.id.terms, "TERMS", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(WebViewActivity.URL, getResources().getString(R.string.tapfit_url) + "terms");
                intent.putExtra(WebViewActivity.TITLE, "Terms of Use");
                startActivity(intent);
            }
        });

        setUpNavButton(R.id.faq, "FAQ", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(WebViewActivity.URL, getResources().getString(R.string.tapfit_url) + "faq");
                intent.putExtra(WebViewActivity.TITLE, "FAQ");
                startActivity(intent);
            }
        });

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
