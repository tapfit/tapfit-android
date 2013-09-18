package co.tapfit.android.helper;

/**
 * Created by zackmartinsek on 9/17/13.
 */
public class BraintreePayments {
    private static BraintreePayments ourInstance = new BraintreePayments();

    public static BraintreePayments getInstance() {
        return ourInstance;
    }

    private BraintreePayments() {
    }
}
