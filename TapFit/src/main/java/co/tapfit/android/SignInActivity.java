package co.tapfit.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import co.tapfit.android.fragment.ChooseSignInFragment;
import co.tapfit.android.fragment.ForgotPasswordFragment;
import co.tapfit.android.fragment.SignInFragment;
import co.tapfit.android.fragment.SignUpFragment;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;

public class SignInActivity extends BaseActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();

    private UiLifecycleHelper uiHelper;

    private SignInFragment mSignInFragment;
    private SignUpFragment mSignUpFragment;
    private ChooseSignInFragment mChooseSignInFragment;
    private ForgotPasswordFragment mForgotPasswordFragment;
    private static final String SIGN_IN_FRAGMENT = "sign_in_fragment";
    private static final String SIGN_UP_FRAGMENT = "sign_up_fragment";
    private static final String CHOOSE_SIGN_IN_FRAGMENT = "choose_sign_in_fragment";
    private static final String FORGOT_PASSWORD_FRAGMENT = "forgot_password_fragment";

    private String mCurrentFragmnt = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_activity);


        setupActionBar();

        setUpFragments();
    }



    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
            progressDialog.setMessage("Logging In....");
            progressDialog.show();
            Bundle args = new Bundle();
            args.putString("access_token", session.getAccessToken());
            UserRequest.loginUser(this, args, new ResponseCallback() {
                @Override
                public void sendCallback(Object responseObject, String message) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).setTitle("Welcome!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            signUpSuccess();
                        }
                    }).setMessage("You are now a part of TapFit. Enjoy your workout!").create();
                    alertDialog.show();

                }
            });
        }
    }

    private void setUpFragments() {

        mSignInFragment = new SignInFragment();
        mSignUpFragment = new SignUpFragment();
        mChooseSignInFragment = new ChooseSignInFragment();
        mForgotPasswordFragment = new ForgotPasswordFragment();

        mCurrentFragmnt = CHOOSE_SIGN_IN_FRAGMENT;

        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mChooseSignInFragment, CHOOSE_SIGN_IN_FRAGMENT).commit();
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_in, menu);
        return true;
    }

    public void showSignUpFragment(String email, String password) {

        Bundle args = new Bundle();
        args.putString(UserRequest.EMAIL, email);
        args.putString(UserRequest.PASSWORD, password);
        mSignUpFragment.setArguments(args);

        getSupportActionBar().setTitle("Sign Up");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.content_frame, mSignUpFragment, SIGN_UP_FRAGMENT);

        if (mCurrentFragmnt.equals(CHOOSE_SIGN_IN_FRAGMENT)) {
            ft.addToBackStack(null);
        }
        mCurrentFragmnt = SIGN_UP_FRAGMENT;
        ft.commit();


    }

    public void showSignInFragment(String email, String password) {

        Bundle args = new Bundle();
        args.putString(UserRequest.EMAIL, email);
        args.putString(UserRequest.PASSWORD, password);
        mSignInFragment.setArguments(args);

        getSupportActionBar().setTitle("Sign In");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.content_frame, mSignInFragment, SIGN_IN_FRAGMENT);

        if (mCurrentFragmnt.equals(CHOOSE_SIGN_IN_FRAGMENT)) {
            ft.addToBackStack(null);
        }
        mCurrentFragmnt = SIGN_IN_FRAGMENT;
        ft.commit();

    }

    public void showForgotPasswordFragment(String email) {

        Bundle args = new Bundle();
        args.putString(UserRequest.EMAIL, email);
        mForgotPasswordFragment.setArguments(args);

        getSupportActionBar().setTitle("Forgot Password?");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.content_frame, mForgotPasswordFragment, FORGOT_PASSWORD_FRAGMENT);

        ft.addToBackStack(null);

        mCurrentFragmnt = FORGOT_PASSWORD_FRAGMENT;
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            mCurrentFragmnt = CHOOSE_SIGN_IN_FRAGMENT;
            getSupportActionBar().setTitle("Sign In");
        }
        super.onBackPressed();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValidPassword(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return (target.length() >= 8);
        }
    }

    public final static boolean isValidName(CharSequence target) {
        if (target == null) {
            return false;
        }
        else {
            return (!target.equals(""));
        }
    }

    public void signUpSuccess() {

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}