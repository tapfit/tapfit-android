package co.tapfit.android.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import co.tapfit.android.R;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;

/**
 * Created by zackmartinsek on 9/29/13.
 */
public class ForgotPasswordFragment extends BaseFragment {

    private static final String TAG = ForgotPasswordFragment.class.getSimpleName();

    private View mView;

    private Button mResetPasswordButton;

    private EditText mEmailEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_forgot_password, null);

        mResetPasswordButton = (Button) mView.findViewById(R.id.reset_password_button);

        mEmailEditText = (EditText) mView.findViewById(R.id.email);

        mResetPasswordButton.setOnClickListener(resetPassword);

        return mView;
    }

    private View.OnClickListener resetPassword = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            UserRequest.resetPassword(getActivity().getApplicationContext(), mEmailEditText.getText().toString(), new ResponseCallback() {
                @Override
                public void sendCallback(Object responseObject, String message) {
                    Boolean success = (Boolean) responseObject;
                    if (success) {
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setMessage("Check your email to reset your password")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                    else
                    {
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setMessage(message)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }
            });
        }
    };
}
