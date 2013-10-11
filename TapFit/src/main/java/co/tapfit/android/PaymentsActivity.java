package co.tapfit.android;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.venmo.touch.activity.PaymentFormActivity;
import com.venmo.touch.client.VenmoTouchClient;

import co.tapfit.android.adapter.CreditCardAdapter;
import co.tapfit.android.fragment.AddCreditCardFragment;
import co.tapfit.android.helper.AnalyticsHelper;
import co.tapfit.android.model.CreditCard;
import co.tapfit.android.model.User;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;
import co.tapfit.android.view.TapFitProgressDialog;

public class PaymentsActivity extends BaseActivity {

    private static final String TAG = PaymentsActivity.class.getSimpleName();

    private User mUser;

    private ListView mCreditCardList;
    private CreditCardAdapter mCreditCardAdapter;
    private Button mAddCardButton;

    private AddCreditCardFragment mAddCreditCardFragment;

    @Override
    public void onResume() {
        super.onResume();

        setCreditCardList();

        AnalyticsHelper.getInstance(this).logEvent("Payments Page");

    }

    private void setCreditCardList() {
        if (mCreditCardList != null) {
            mCreditCardAdapter = new CreditCardAdapter(this, dbWrapper.getCreditCards(dbWrapper.getCurrentUser()));
            mCreditCardList.setAdapter(mCreditCardAdapter);
            mCreditCardList.setLayoutParams(new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, getListViewHeight()));
        }
    }

    private int getListViewHeight() {
        return (int) (getResources().getDimension(R.dimen.normal_button_size) * mCreditCardAdapter.getCount());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        mUser = dbWrapper.getCurrentUser();

        mCreditCardList = (ListView) findViewById(R.id.credit_card_list);

        mCreditCardList.setOnItemClickListener(editCardState);

        mCreditCardAdapter = new CreditCardAdapter(this, dbWrapper.getCreditCards(mUser));

        mAddCreditCardFragment = new AddCreditCardFragment();

        mCreditCardList.setAdapter(mCreditCardAdapter);

        mAddCardButton = (Button) findViewById(R.id.add_card_button);

        mAddCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
                mAddCreditCardFragment = new AddCreditCardFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mAddCreditCardFragment, "Credit Card").commit();
                //Intent intent = PaymentFormActivity.getSandboxStartIntent(PaymentsActivity.this, MERCHANT_ID, MERCHANT_SECRET);
                //startActivityForResult(intent, 1);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    AdapterView.OnItemClickListener editCardState = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long id) {
            final TapFitProgressDialog progress = new TapFitProgressDialog(PaymentsActivity.this);
            CreditCard creditCard = (CreditCard) adapterView.getItemAtPosition(position);
            AlertDialog alertDialog = new AlertDialog.Builder(PaymentsActivity.this)
                    .setTitle("Edit Card")
                    .setMessage(creditCard.card_type + "   ****" + creditCard.last_four)
                    .setPositiveButton("Set Default", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progress.setMessage("Setting Default...");
                            progress.show();
                            CreditCard card = (CreditCard) adapterView.getItemAtPosition(position);
                            UserRequest.setDefaultCard(PaymentsActivity.this, card, new ResponseCallback() {
                                @Override
                                public void sendCallback(Object responseObject, String message) {
                                    progress.cancel();
                                    if (responseObject == null) {
                                        AlertDialog dialog = new AlertDialog.Builder(PaymentsActivity.this)
                                                .setMessage("Failed to set default card")
                                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.cancel();
                                                    }
                                                })
                                                .show();
                                    } else {
                                        setCreditCardList();
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (dbWrapper.getCreditCards(mUser).size() < 2) {
                                AlertDialog alertDialog = new AlertDialog.Builder(PaymentsActivity.this)
                                        .setMessage("Need to have at least one card on file")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        }).create();

                                alertDialog.show();
                            } else {
                                progress.setMessage("Deleting Card...");
                                progress.show();
                                CreditCard card = (CreditCard) adapterView.getItemAtPosition(position);
                                UserRequest.deleteCreditCard(PaymentsActivity.this, card, new ResponseCallback() {
                                    @Override
                                    public void sendCallback(Object responseObject, String message) {
                                        progress.cancel();
                                        if (responseObject == null) {
                                            AlertDialog dialog = new AlertDialog.Builder(PaymentsActivity.this)
                                                    .setMessage("Failed to delete card")
                                                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.cancel();
                                                        }
                                                    })
                                                    .create();
                                            dialog.show();
                                        } else {
                                            setCreditCardList();
                                        }
                                    }
                                });
                            }
                        }
                    })
                    .create();
            alertDialog.show();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.payments, menu);
        return true;
    }

    public void hideAddCreditCardFragment() {

        setCreditCardList();
        findViewById(R.id.content_frame).setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().remove(mAddCreditCardFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("Credit Card") == mAddCreditCardFragment)
        {
            hideAddCreditCardFragment();
        }
        else
        {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
