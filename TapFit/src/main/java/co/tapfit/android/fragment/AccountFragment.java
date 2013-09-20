package co.tapfit.android.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import co.tapfit.android.MapListActivity;
import co.tapfit.android.PaymentsActivity;
import co.tapfit.android.PlaceInfoActivity;
import co.tapfit.android.R;
import co.tapfit.android.SignInActivity;
import co.tapfit.android.helper.Log;
import co.tapfit.android.helper.ShareToFriends;
import co.tapfit.android.model.User;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;

/**
 * Created by zackmartinsek on 9/10/13.
 */
public class AccountFragment extends BaseFragment {

    private View mView;

    private User mUser;

    private static final String TAG = AccountFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mUser = dbWrapper.getCurrentUser();

        if (mUser != null) {
            mView = inflater.inflate(R.layout.fragment_account_signed_in, null);

            setUpSignedInView();
        }
        else
        {
            mView = inflater.inflate(R.layout.fragment_account_signed_out, null);

            setUpSignedOutView();
        }

        return mView;
    }

    private void setUpSignedInView() {

        DecimalFormat df = new DecimalFormat("0.00");

        setUpForm(R.id.credits, "CREDITS", "$" + df.format(mUser.credit_amount), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Implement share to friends
                Toast.makeText(getActivity(), "Implement Credits", 1000).show();
            }
        });

        setUpNavButton(R.id.payments, "PAYMENTS", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PaymentsActivity.class);
                startActivity(intent);
            }
        });

        setUpNavButton(R.id.customer_support, "CUSTOMER SUPPORT", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapListActivity) getActivity()).showCustomerSupport();
            }
        });

        setUpForm(R.id.invite_a_friend, "INVITE A FRIEND", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareToFriends.shareAppToFriends(getActivity());
            }
        });

        setUpNavButton(R.id.tapfit_info, "TAPFIT INFO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapListActivity) getActivity()).showTapfitInfo();
            }
        });

        setUpForm(R.id.logout, "LOG OUT", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Logging out..");
                progressDialog.show();
                UserRequest.logoutUser(getActivity(), new ResponseCallback(){

                    @Override
                    public void sendCallback(Object responseObject, String message) {

                        AccountFragment accountFragment = new AccountFragment();

                        ((MapListActivity) getActivity()).setAccountFragment(accountFragment);

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, accountFragment, MapListActivity.ACCOUNT).commit();
                        progressDialog.cancel();
                    }

                });
            }
        });

    }

    private void setUpSignedOutView() {

        setUpNavButton(R.id.sign_in, "SIGN UP OR SIGN IN", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        setUpNavButton(R.id.customer_support, "CUSTOMER SUPPORT", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapListActivity) getActivity()).showCustomerSupport();
            }
        });

        setUpNavButton(R.id.tapfit_info, "TAPFIT INFO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapListActivity) getActivity()).showTapfitInfo();
            }
        });
    }

    private void setUpForm(Integer resource, String title, String value, View.OnClickListener listener) {
        View form = mView.findViewById(resource);
        ((TextView) form.findViewById(R.id.content_title)).setText(title);
        ((TextView) form.findViewById(R.id.content_value)).setText(value);

        if (listener != null) {
            form.setOnClickListener(listener);
            form.setClickable(true);
        }
    }

    private void setUpNavButton(Integer resource, String title, View.OnClickListener listener) {

        View view = mView.findViewById(resource);
        ((TextView) view.findViewById(R.id.content_title)).setText(title);
        view.setClickable(true);
        view.setOnClickListener(listener);
        int padding = (int) getResources().getDimension(R.dimen.padding);
        view.setPadding(padding, 0, padding, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == getActivity().RESULT_OK){
                Log.d(TAG, "Successfully signed in");
                AccountFragment accountFragment = new AccountFragment();

                ((MapListActivity) getActivity()).setAccountFragment(accountFragment);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, accountFragment, MapListActivity.ACCOUNT).commit();
            }
            if (resultCode == getActivity().RESULT_CANCELED) {
                Log.d(TAG, "Didn't log in");
            }
        }
    }
}
