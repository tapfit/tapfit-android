package co.tapfit.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import co.tapfit.android.FirstUseActivity;
import co.tapfit.android.R;
import co.tapfit.android.model.User;

/**
 * Created by zackmartinsek on 9/20/13.
 */
public class WelcomeFragment extends BaseFragment {

    private View mView;

    private User mUser;

    private static final String TAG = AccountFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_welcome, null);

        Button getStarted = (Button) mView.findViewById(R.id.get_started);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FirstUseActivity)getActivity()).endFirstUse();
            }
        });

        return mView;
    }
}
