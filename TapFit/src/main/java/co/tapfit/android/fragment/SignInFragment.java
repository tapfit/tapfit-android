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

import com.google.android.gms.internal.di;

import co.tapfit.android.R;
import co.tapfit.android.SignInActivity;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;

/**
 * Created by zackmartinsek on 9/13/13.
 */
public class SignInFragment extends BaseFragment {

    private static final String TAG = SignInFragment.class.getSimpleName();
    private View mView;

    private EditText mEmail;
    private EditText mPassword;

    private Button mSignIn;

    private FrameLayout mShowSignUp;

    AlertDialog.Builder alertDialog;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_sign_in, null);

        setUpViews(getArguments().getString(UserRequest.EMAIL), getArguments().getString(UserRequest.PASSWORD));
        
        setUpAlertDialog();

        setUpProgressDialog();

        return mView;
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.setTitle("Signing in....");
    }

    private void setUpAlertDialog() {
        alertDialog = new AlertDialog.Builder(getActivity()).setTitle("Oops!? Something Went Wrong")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ;
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

        mSignIn = (Button) mView.findViewById(R.id.signup_button);
        mSignIn.setOnClickListener(clickSignIn);

        mShowSignUp = (FrameLayout) mView.findViewById(R.id.bottom_button);
        mShowSignUp.setOnClickListener(showSingUp);
    }

    private View.OnClickListener showSingUp = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((SignInActivity)getActivity()).showSignUpFragment(mEmail.getText().toString(), mPassword.getText().toString());
        }
    };

    private View.OnClickListener clickSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Boolean validEmail = SignInActivity.isValidEmail(mEmail.getText().toString());
            Boolean validPassword = SignInActivity.isValidPassword(mPassword.getText().toString());
            if (validEmail && validPassword){
                Bundle args = new Bundle();
                args.putString(UserRequest.EMAIL, mEmail.getText().toString());
                args.putString(UserRequest.PASSWORD, mPassword.getText().toString());
                UserRequest.loginUser(getActivity(), args, callback);
                progressDialog.show();;
            }
            else
            {
                String errorMessage = "";
                if (!validEmail) {
                    errorMessage = "Email not valid";
                }
                else if (!validPassword) {
                    errorMessage = errorMessage + "Password must be 8 characters";
                }
                alertDialog.setMessage(errorMessage).show();
            }
        }
    };

    private ResponseCallback callback = new ResponseCallback() {
        @Override
        public void sendCallback(Object responseObject, String message) {
            progressDialog.cancel();
            if (responseObject == null) {
                alertDialog.setMessage(message)
                        .setNegativeButton("Sign Up?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((SignInActivity) getActivity()).showSignUpFragment(mEmail.getText().toString(), mPassword.getText().toString());
                            }
                        }).show();
            }
            else
            {
                alertDialog.setTitle("Success!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((SignInActivity) getActivity()).signUpSuccess();
                    }
                })
                .setMessage("Sign in Success").show();
            }
        }
    };
}
