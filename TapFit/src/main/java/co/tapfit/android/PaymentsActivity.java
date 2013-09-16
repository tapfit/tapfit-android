package co.tapfit.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

import co.tapfit.android.adapter.CreditCardAdapter;
import co.tapfit.android.model.User;

public class PaymentsActivity extends BaseActivity {

    private static final String TAG = PaymentsActivity.class.getSimpleName();

    private User mUser;

    private ListView mCreditCardList;
    private CreditCardAdapter mCreditCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        mUser = dbWrapper.getCurrentUser();

        mCreditCardList = (ListView) findViewById(R.id.credit_card_list);

        mCreditCardAdapter = new CreditCardAdapter(this, dbWrapper.getCreditCards(mUser));

        mCreditCardList.setAdapter(mCreditCardAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.payments, menu);
        return true;
    }
    
}
