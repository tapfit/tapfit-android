package co.tapfit.android;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import co.tapfit.android.fragment.SignInFragment;
import co.tapfit.android.fragment.SignUpFragment;
import co.tapfit.android.request.UserRequest;

public class SignInActivity extends BaseActivity {

    private SignInFragment mSignInFragment;
    private SignUpFragment mSignUpFragment;
    private static final String SIGN_IN_FRAGMENT = "sign_in_fragment";
    private static final String SIGN_UP_FRAGMENT = "sign_up_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_activity);

        setupActionBar();

        setUpFragments();
    }

    private void setUpFragments() {

        mSignInFragment = new SignInFragment();
        mSignUpFragment = new SignUpFragment();

        Bundle args = new Bundle();
        args.putString(UserRequest.EMAIL, null);
        args.putString(UserRequest.PASSWORD, null);
        mSignUpFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mSignUpFragment, SIGN_UP_FRAGMENT).commit();
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

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mSignUpFragment, SIGN_UP_FRAGMENT).commit();


    }

    public void showSignInFragment(String email, String password) {

        Bundle args = new Bundle();
        args.putString(UserRequest.EMAIL, email);
        args.putString(UserRequest.PASSWORD, password);
        mSignInFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mSignInFragment, SIGN_IN_FRAGMENT).commit();

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
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}