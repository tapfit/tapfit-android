package co.tapfit.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import co.tapfit.android.PlaceInfoActivity;
import co.tapfit.android.R;
import co.tapfit.android.model.Place;
import co.tapfit.android.model.User;
import co.tapfit.android.model.Workout;

/**
 * Created by zackmartinsek on 9/14/13.
 */
public class ConfirmPurchaseFragment extends BaseFragment {

    private static final String TAG = ConfirmPurchaseFragment.class.getSimpleName();
    private View mView;
    private Workout mWorkout;
    private Place mPlace;
    private User mUser;

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
            Toast.makeText(getActivity(), "Show payments", 1000).show();
        }
    };

    private void setUpForm(Integer resource, String title, String value) {
        View form = mView.findViewById(resource);
        ((TextView) form.findViewById(R.id.content_title)).setText(title);
        ((TextView) form.findViewById(R.id.content_value)).setText(value);
    }
}
