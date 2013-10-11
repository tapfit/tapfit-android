package co.tapfit.android.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.HashMap;

import co.tapfit.android.MapListActivity;
import co.tapfit.android.PassActivity;
import co.tapfit.android.PaymentsActivity;
import co.tapfit.android.PlaceInfoActivity;
import co.tapfit.android.R;
import co.tapfit.android.helper.AnalyticsHelper;
import co.tapfit.android.helper.CreditCardImage;
import co.tapfit.android.helper.Log;
import co.tapfit.android.model.CreditCard;
import co.tapfit.android.model.Pass;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.WorkoutRequest;
import co.tapfit.android.view.TapFitProgressDialog;

/**
 * Created by zackmartinsek on 9/14/13.
 */
public class ConfirmPurchaseFragment extends BaseFragment {

    private static final String TAG = ConfirmPurchaseFragment.class.getSimpleName();
    private View mView;
    private Workout mWorkout;
    private Place mPlace;
    private User mUser;

    private static final Integer PAYMENT_ACTIVITY = 1;

    private FrameLayout mConfirmPurchase;

    public static final String WORKOUT_ID = "workout_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_confirm_purchase, null);

        mWorkout = dbWrapper.getWorkout(getArguments().getInt(WORKOUT_ID, -1));

        mPlace = dbWrapper.getPlace(getArguments().getInt(PlaceInfoActivity.PLACE_ID, -1));

        mUser = dbWrapper.getCurrentUser();

        setUpConfirmPurchase();

        return mView;
    }

    private void setUpConfirmPurchase() {

        DecimalFormat df = new DecimalFormat("0.00");

        setUpForm(R.id.workout_name, mWorkout.name, "");
        setUpForm(R.id.place_name, mPlace.name, "");
        setUpForm(R.id.user_name, mUser.first_name + " " + mUser.last_name, "");
        setUpForm(R.id.subtotal, "Subtotal", "$" + df.format(Math.round(mWorkout.price * 100.0) / 100.0));
        setUpForm(R.id.credits, "Your Credits", "$" + df.format(Math.round(mUser.credit_amount * 100.0) / 100.0));
        setUpForm(R.id.total, "Your Total", "$" + df.format(Math.round(Math.max(0, mWorkout.price - mUser.credit_amount) * 100.0) / 100.0));

        setUpPayment();

        mConfirmPurchase = (FrameLayout) mView.findViewById(R.id.bottom_button);
        mConfirmPurchase.setOnClickListener(buyWorkout);
    }

    private void setUpPayment() {

        if (mUser.credit_cards != null && mUser.credit_cards.size() > 0) {

            CreditCard creditCard = dbWrapper.getDefaulCard(mUser);
            if (creditCard == null) {
                setUpDefaultPaymentPrompt();
            }
            else {
                setUpPaymentPrompt(creditCard);
            }
        }
        else {
            setUpDefaultPaymentPrompt();
        }

    }

    private View.OnClickListener buyWorkout = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final TapFitProgressDialog progressDialog = new TapFitProgressDialog(getActivity());
            progressDialog.setMessage("Purchasing " + mWorkout.name);
            progressDialog.show();

            HashMap<String, String> args = new HashMap<String, String>();
            args.put("Workout", String.valueOf(mWorkout.id));
            args.put("Price", String.valueOf(mWorkout.price));

            AnalyticsHelper.getInstance(getActivity()).logEvent("Purchased Workout", args);

            WorkoutRequest.buyWorkout(getActivity(), mWorkout, new ResponseCallback() {
                @Override
                public void sendCallback(final Object responseObject, String message) {
                    progressDialog.cancel();
                    if (responseObject != null) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle("Success!")
                                .setPositiveButton("See Pass", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(getActivity(), PassActivity.class);
                                        intent.putExtra(PassFragment.PASS_ID, ((Pass) responseObject).id);
                                        intent.putExtra(PassActivity.CAME_FROM, PassActivity.PURCHASE_PAGE);
                                        startActivity(intent);
                                    }
                                }).create();

                        alertDialog.show();
                    }
                    else
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle("Failed to buy pass!")
                                .setMessage(message)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                }).create();

                        alertDialog.show();
                    }
                }
            });
        }
    };

    private void setUpPaymentPrompt(CreditCard creditCard) {
        View view = mView.findViewById(R.id.payments);
        ImageView imageView = (ImageView) view.findViewById(R.id.button_icon);
        imageView.setImageResource(CreditCardImage.getCreditCardImageFromUrl(creditCard.image_url));
        imageView.setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.content_title)).setText("****" + creditCard.last_four);
        view.setClickable(true);
        view.setOnClickListener(showPayments);
        int padding = (int) getResources().getDimension(R.dimen.padding);
        view.setPadding(padding, 0, padding, 0);
    }

    private void setUpDefaultPaymentPrompt() {

        View view = mView.findViewById(R.id.payments);
        ((TextView) view.findViewById(R.id.content_title)).setText("Set payment option");
        view.setClickable(true);
        view.setOnClickListener(showPayments);
        int padding = (int) getResources().getDimension(R.dimen.padding);
        view.setPadding(padding, 0, padding, 0);
    }

    View.OnClickListener showPayments = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AnalyticsHelper.getInstance(getActivity()).logEvent("Go To Payments Page");
            Intent intent = new Intent(getActivity(), PaymentsActivity.class);
            startActivityForResult(intent, PAYMENT_ACTIVITY);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == getActivity().RESULT_OK){
                setUpPayment();
            }
            if (resultCode == getActivity().RESULT_CANCELED) {
                Log.d(TAG, "Didn't log in");
            }
        }
    }

    private void setUpForm(Integer resource, String title, String value) {
        View form = mView.findViewById(resource);
        ((TextView) form.findViewById(R.id.content_title)).setText(title);
        ((TextView) form.findViewById(R.id.content_value)).setText(value);
    }
}
