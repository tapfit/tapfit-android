package co.tapfit.android.helper;

import android.content.Context;
import android.os.Bundle;

import com.braintreegateway.encryption.Braintree;
import com.venmo.touch.model.CardDetails;

import java.util.Arrays;
import java.util.List;

import co.tapfit.android.R;

/**
 * Created by zackmartinsek on 9/17/13.
 */
public class BraintreePayments {

    public static final String CARD_NUMBER = "card_number";
    public static final String EXPIRATION_MONTH = "expiration_month";
    public static final String EXPIRATION_YEAR = "expiration_year";
    public static final String CVV = "cvv";
    public static final String ZIPCODE = "zipcode";

    private static final List<String> KEYS = Arrays.asList(CARD_NUMBER, EXPIRATION_MONTH, EXPIRATION_YEAR, CVV);

    private static Braintree mBraintree;

    private static BraintreePayments ourInstance;

    public static BraintreePayments getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new BraintreePayments(context);
        }
        return ourInstance;
    }

    private BraintreePayments(Context context) {
        if (Boolean.parseBoolean(context.getResources().getString(R.string.is_braintree_sandbox))) {
            mBraintree = new Braintree(context.getResources().getString(R.string.braintree_encryption_debug));
        }
        else {
            mBraintree = new Braintree(context.getResources().getString(R.string.braintree_encryption_release));
        }
    }

    public static Bundle encryptPayment(CardDetails cardDetails) {

        Bundle encryptedArgs = new Bundle();

        encryptedArgs.putString(CARD_NUMBER, mBraintree.encrypt(cardDetails.getAccountNumber()));
        encryptedArgs.putString(EXPIRATION_MONTH, mBraintree.encrypt(String.valueOf(cardDetails.getExpirationMonth())));
        encryptedArgs.putString(EXPIRATION_YEAR, mBraintree.encrypt(String.valueOf(cardDetails.getExpirationYear())));
        encryptedArgs.putString(CVV, mBraintree.encrypt(String.valueOf(cardDetails.getSecurityCode())));
        encryptedArgs.putString(ZIPCODE, mBraintree.encrypt(cardDetails.getZipCode()));

        return encryptedArgs;
    }
}
