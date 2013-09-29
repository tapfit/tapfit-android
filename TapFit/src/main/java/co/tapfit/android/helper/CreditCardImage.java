package co.tapfit.android.helper;

import com.venmo.touch.R;

/**
 * Created by zackmartinsek on 9/26/13.
 */
public class CreditCardImage {



    private static CreditCardImage ourInstance = new CreditCardImage();

    public static CreditCardImage getInstance() {
        return ourInstance;
    }

    private CreditCardImage() {
    }

    public static int getCreditCardImageFromUrl(String url) {

        if (url == null)
        {
            return R.drawable.bt_generic_card;
        }
        else if (url.contains("visa"))
        {
            return R.drawable.bt_visa;
        }
        else if (url.contains("american_express"))
        {
            return R.drawable.bt_amex;
        }
        else if (url.contains("diners"))
        {
            return R.drawable.bt_diners_club;
        }
        else if (url.contains("discover"))
        {
            return R.drawable.bt_discover;
        }
        else if (url.contains("mastercard"))
        {
            return R.drawable.bt_mastercard;
        }
        else if (url.contains("jcb"))
        {
            return R.drawable.bt_jcb;
        }
        else
        {
            return R.drawable.bt_generic_card;
        }

    }
}
