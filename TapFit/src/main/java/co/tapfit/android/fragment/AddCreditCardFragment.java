package co.tapfit.android.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.venmo.touch.client.VenmoTouchClient;
import com.venmo.touch.controller.VTComboCardViewController;
import com.venmo.touch.view.SingleLineCardEntryView;
import com.venmo.touch.view.VTComboCardView;

import java.util.regex.Pattern;

import co.tapfit.android.PaymentsActivity;
import co.tapfit.android.R;
import co.tapfit.android.helper.AnalyticsHelper;
import co.tapfit.android.helper.BraintreePayments;
import co.tapfit.android.helper.Log;
import co.tapfit.android.model.User;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;
import ly.count.android.api.Countly;

/**
 * Created by zackmartinsek on 9/17/13.
 */
public class AddCreditCardFragment extends BaseFragment {

    private View mView;

    private static final String TAG = AddCreditCardFragment.class.getSimpleName();

    private static String MERCHANT_ID;
    private static String MERCHANT_SECRET;

    private EditText mCardNumber;
    private EditText mExpirationDate;
    private EditText mPostalCode;
    private EditText mSecurityCode;

    private SingleLineCardEntryView mSingleLineCardEntry;

    private ProgressDialog progressDialog;

    private Button mAddCreditCard;

    @Override
    public void onResume() {
        super.onResume();

        AnalyticsHelper.getInstance(getActivity()).logEvent("Add Credit Card");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_add_credit_card, null);

        BraintreePayments.getInstance(getActivity().getApplicationContext());

        setUpCreditCardViews();

        return mView;
    }

    private void setUpCreditCardViews() {


        //mCardNumber = (EditText) mView.findViewById(R.id.credit_card_number);

        //mCardNumber.setNextFocusDownId(R.id.expiration_date);

        mSingleLineCardEntry = (SingleLineCardEntryView) mView.findViewById(R.id.credit_card_number);


        mAddCreditCard = (Button) mView.findViewById(R.id.add_card);
        mAddCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AnalyticsHelper.getInstance(getActivity()).logEvent("Added Credit Card");

                if (mSingleLineCardEntry.isValid()){
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Adding Credit Card...");
                    progressDialog.show();
                    UserRequest.addPaymentMethod(getActivity(), BraintreePayments.encryptPayment(mSingleLineCardEntry.getCardDetails()), addCardCallback);
                }
            }
        });

        //mSingleLineCardEntry.getCardDetails().
        /*mExpirationDate = (EditText) mView.findViewById(R.id.expiration_date);
        mExpirationDate.addTextChangedListener(expirationListener);

        mExpirationDate.setNextFocusDownId(R.id.cvv);

        mPostalCode = (EditText) mView.findViewById(R.id.postal_code);
        mSecurityCode = (EditText) mView.findViewById(R.id.cvv);

        mSecurityCode.setNextFocusDownId(R.id.postal_code);*/
    }

    private ResponseCallback addCardCallback = new ResponseCallback() {
        @Override
        public void sendCallback(Object responseObject, String message) {
            if (progressDialog != null) {
                progressDialog.cancel();
            }
            ((PaymentsActivity) getActivity()).hideAddCreditCardFragment();
        }
    };

    private TextWatcher expirationListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.d(TAG, "String we are going to change: " + s);

            if (s.length() < 3) {
                try {
                    if (count > before) {
                        int num = Integer.parseInt(s.toString());
                        if (num < 13 && num >= 0) {
                            if (num < 10 && num > 1) {
                                String text = "0" + num + "/";
                                mExpirationDate.setText(text);
                                mExpirationDate.setSelection(text.length());
                            }
                            else if (num > 1) {
                                String text = num + "/";
                                mExpirationDate.setText(text);
                                mExpirationDate.setSelection(text.length());
                            }
                        }
                        else
                        {
                            mExpirationDate.setError(s + " is not a valid month");
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Failed to parse text", e);
                }
            }
            else {
                try {
                    Boolean matches = Pattern.matches("(0[1-9]|1[0-2])\\/[0-9]{2}", s);
                    if (!matches) {
                        if (count >= 5) {
                            mExpirationDate.setError(s + " is not valid expiration");
                        }
                    }
                    else
                    {
                        int year = Integer.parseInt(s.toString().split("/")[1]);

                        if (year < 13) {
                            mExpirationDate.setError(year + " is not a valid year");
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Failed to do Regex on: " + s, e);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
