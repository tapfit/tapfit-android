package co.tapfit.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import co.tapfit.android.fragment.CustomerSupportFragment;
import co.tapfit.android.fragment.SignInFragment;
import co.tapfit.android.fragment.SignUpFragment;
import co.tapfit.android.fragment.TapfitInfoFragment;
import co.tapfit.android.request.UserRequest;

public class ExtraSettingsActivity extends BaseActivity {

    private static final String TAG = ExtraSettingsActivity.class.getSimpleName();

    private CustomerSupportFragment mCustomerSupportFragment;

    private TapfitInfoFragment mTapfitInfoFragment;

    public static final String PAGE = "page";

    public static final Integer CUSTOMER_SUPPORT = 0;
    public static final Integer TAPFIT_INFO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_settings);

        setupActionBar();

        setUpFragments();
    }

    private void setUpFragments() {

        mCustomerSupportFragment = new CustomerSupportFragment();
        mTapfitInfoFragment = new TapfitInfoFragment();

        if (getIntent().getIntExtra(PAGE, CUSTOMER_SUPPORT) == CUSTOMER_SUPPORT) {

            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mCustomerSupportFragment).commit();

            getSupportActionBar().setTitle("Customer Support");
        }
        else if (getIntent().getIntExtra(PAGE, CUSTOMER_SUPPORT) == TAPFIT_INFO) {

            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, mTapfitInfoFragment).commit();

            getSupportActionBar().setTitle("TapFit Info");
        }
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.extra_settings, menu);
        return true;
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
