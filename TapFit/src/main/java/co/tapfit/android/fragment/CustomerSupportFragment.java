package co.tapfit.android.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.tapfit.android.R;

/**
 * Created by zackmartinsek on 9/16/13.
 */
public class CustomerSupportFragment extends BaseFragment {

    private static final String TAG = CustomerSupportFragment.class.getSimpleName();
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_customer_support, null);

        setUpCustomerSupportViews();

        return mView;
    }

    private void setUpCustomerSupportViews() {

        setUpForm(R.id.email, "EMAIL", "support@tapfit.co", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@tapfit.co"});
                Intent mailer = Intent.createChooser(intent, null);
                startActivity(mailer);
            }
        });

        setUpForm(R.id.phone, "PHONE", "888-487-9301", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "tel:8884879301";
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                startActivity(intent);
            }
        });

        setUpForm(R.id.twitter, "TWITTER", "@TapFitSupport", null);

    }

    private void setUpForm(Integer resource, String title, String value, View.OnClickListener listener) {
        View form = mView.findViewById(resource);
        ((TextView) form.findViewById(R.id.content_title)).setText(title);
        ((TextView) form.findViewById(R.id.content_value)).setText(value);

        if (listener != null) {
            form.setOnClickListener(listener);
            form.setClickable(true);
        }
    }
}
