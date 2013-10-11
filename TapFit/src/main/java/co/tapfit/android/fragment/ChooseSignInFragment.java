package co.tapfit.android.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.facebook.widget.LoginButton;

import java.util.ArrayList;
import java.util.List;

import co.tapfit.android.R;
import co.tapfit.android.SignInActivity;
import co.tapfit.android.request.UserRequest;

/**
 * Created by zackmartinsek on 9/29/13.
 */
public class ChooseSignInFragment extends BaseFragment {

    private static final String TAG = SignInFragment.class.getSimpleName();
    private View mView;

    private LoginButton mLoginButton;
    private Button mEmailLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_choose_login, null);

        setUpViews();

        return mView;
    }

    private void setUpViews() {
        mLoginButton = (LoginButton) mView.findViewById(R.id.login_button);

        mLoginButton.setBackgroundDrawable(getResources().getDrawable(R.color.com_facebook_blue));
        List<String> permissions = new ArrayList<String>();
        permissions.add("user_likes");
        permissions.add("email");
        permissions.add("user_birthday");
        permissions.add("user_location");
        mLoginButton.setReadPermissions(permissions);

        mEmailLogin = (Button) mView.findViewById(R.id.email_login_button);
        mEmailLogin.setOnClickListener(showEmailLogin);
    }

    private View.OnClickListener showEmailLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((SignInActivity) getActivity()).showSignUpFragment(null, null);
        }
    };
}
