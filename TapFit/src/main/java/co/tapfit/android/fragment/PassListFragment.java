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

import com.flurry.android.monolithic.sdk.impl.ada;

import java.util.ArrayList;
import java.util.List;

import co.tapfit.android.PassActivity;
import co.tapfit.android.R;
import co.tapfit.android.adapter.PassListAdapter;
import co.tapfit.android.model.Pass;

/**
 * Created by zackmartinsek on 9/10/13.
 */
public class PassListFragment extends BaseFragment {

    private View mView;
    private ListView mPassList;
    private PassListAdapter mPassAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_pass_list, null);

        mPassAdapter = new PassListAdapter(getActivity(), dbWrapper.getPasses());

        mPassList = (ListView) mView.findViewById(R.id.pass_list);

        mPassList.setAdapter(mPassAdapter);

        mPassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), PassActivity.class);
                intent.putExtra(PassFragment.PASS_ID, ((Pass) adapterView.getItemAtPosition(i)).id);
                startActivity(intent);
            }
        });

        return mView;
    }
}
