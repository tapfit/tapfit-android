package co.tapfit.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.monolithic.sdk.impl.ada;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import co.tapfit.android.PassActivity;
import co.tapfit.android.R;
import co.tapfit.android.SignInActivity;
import co.tapfit.android.adapter.PassListAdapter;
import co.tapfit.android.model.Pass;

/**
 * Created by zackmartinsek on 9/10/13.
 */
public class PassListFragment extends BaseFragment {

    private View mView;
    private ListView mPassList;
    private PassListAdapter mPassAdapter;
    private TextView mNoPassText;

    @Override
    public void onResume() {
        super.onResume();

        setUpView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_pass_list, null);

        if (dbWrapper.getCurrentUser() == null) {
            Intent signInIntent = new Intent(getActivity(), SignInActivity.class);
            startActivity(signInIntent);
        }

        setUpView();

        return mView;
    }

    private void setUpView() {
        List<Pass> passes = dbWrapper.getPasses();

        Collections.sort(passes);

        Collections.reverse(passes);

        mPassAdapter = new PassListAdapter(getActivity(), passes);

        mPassList = (ListView) mView.findViewById(R.id.pass_list);

        mNoPassText = (TextView) mView.findViewById(R.id.no_pass_text);

        if (passes.size() > 0) {

            mPassList.setAdapter(mPassAdapter);

            mPassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(getActivity(), PassActivity.class);
                    intent.putExtra(PassFragment.PASS_ID, ((Pass) adapterView.getItemAtPosition(i)).id);
                    startActivity(intent);
                }
            });
        }
        else
        {
            mPassList.setVisibility(View.GONE);
            mNoPassText.setVisibility(View.VISIBLE);

        }
    }
}
