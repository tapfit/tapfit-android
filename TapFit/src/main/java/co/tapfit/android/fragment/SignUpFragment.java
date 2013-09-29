package co.tapfit.android.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import co.tapfit.android.R;
import co.tapfit.android.SignInActivity;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;

/**
 * Created by zackmartinsek on 9/13/13.
 */
public class SignUpFragment extends BaseFragment {

    private static final String TAG = SignInFragment.class.getSimpleName();
    private View mView;

    private EditText mEmail;
    private EditText mPassword;
    private EditText mFirstName;
    private EditText mLastName;

    private Button mSignUp;

    private FrameLayout mShowSignIn;

    AlertDialog.Builder alertDialog;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_sign_up, null);

        setUpViews(getArguments().getString(UserRequest.EMAIL), getArguments().getString(UserRequest.PASSWORD));

        setUpAlertDialog();

        setUpProgressDialog();

        return mView;
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.setMessage("Signing up....");
    }

    private void setUpAlertDialog() {
        alertDialog = new AlertDialog.Builder(getActivity()).setTitle("Oops!? Something Went Wrong")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

    private void setUpViews(String email, String password) {

        mEmail = (EditText) mView.findViewById(R.id.email);
        if (email != null && !email.equals("")) {
            mEmail.setText(email);
        }
        mPassword = (EditText) mView.findViewById(R.id.password);
        if (password != null && !password.equals("")) {
            mPassword.setText(password);
        }
        mFirstName = (EditText) mView.findViewById(R.id.first_name);
        mLastName = (EditText) mView.findViewById(R.id.last_name);

        mSignUp = (Button) mView.findViewById(R.id.signup_button);
        mSignUp.setOnClickListener(clickSignIn);

        mShowSignIn = (FrameLayout) mView.findViewById(R.id.bottom_button);
        mShowSignIn.setOnClickListener(showSignIn);
    }

    private View.OnClickListener showSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((SignInActivity)getActivity()).showSignInFragment(mEmail.getText().toString(), mPassword.getText().toString());
        }
    };

    private View.OnClickListener clickSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Boolean validEmail = SignInActivity.isValidEmail(mEmail.getText().toString());
            Boolean validPassword = SignInActivity.isValidPassword(mPassword.getText().toString());
            Boolean validFirstName = SignInActivity.isValidName(mFirstName.getText().toString());
            Boolean validLastName = SignInActivity.isValidName(mLastName.getText().toString());
            if (validEmail && validPassword && validFirstName && validLastName){
                Bundle args = new Bundle();
                args.putString(UserRequest.EMAIL, mEmail.getText().toString());
                args.putString(UserRequest.PASSWORD, mPassword.getText().toString());
                args.putString(UserRequest.FIRST_NAME, mFirstName.getText().toString());
                args.putString(UserRequest.LAST_NAME, mLastName.getText().toString());
                UserRequest.registerUser(getActivity(), args, callback);
                progressDialog.show();;
            }
            else
            {
                String errorMessage = "";
                if (!validEmail) {
                    errorMessage = "Email not valid";
                }
                else if (!validPassword) {
                    errorMessage = errorMessage + " Password must be 8 characters";
                }
                else if (!validFirstName || !validLastName) {
                    errorMessage = errorMessage + " Must enter full name";
                }
                alertDialog.setMessage(errorMessage)
                        .show();
            }
        }
    };

    private ResponseCallback callback = new ResponseCallback() {
        @Override
        public void sendCallback(Object responseObject, String message) {
            progressDialog.cancel();
            if (responseObject == null) {
                alertDialog.setMessage(message).show();
            }
            else
            {
                alertDialog.setTitle("Welcome!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((SignInActivity) getActivity()).signUpSuccess();
                    }
                }).setMessage("You are now a part of TapFit. Enjoy your workout!").show();
            }
        }
    };
}
