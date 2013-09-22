package co.tapfit.android;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import co.tapfit.android.BaseActivity;
import co.tapfit.android.fragment.PassFragment;

public class PassActivity extends BaseActivity {

    public static final Integer PURCHASE_PAGE = 1;
    public static final Integer PASS_LIST_PAGE = 2;

    public static final String CAME_FROM = "came_from";

    private Integer mCameFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_activity);

        PassFragment passFragment = new PassFragment();

        mCameFrom = getIntent().getIntExtra(CAME_FROM, PASS_LIST_PAGE);

        Bundle args = new Bundle();
        args.putInt(PassFragment.PASS_ID, getIntent().getIntExtra(PassFragment.PASS_ID, -1));

        passFragment.setArguments(args);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, passFragment).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pass, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mCameFrom == PASS_LIST_PAGE) {
            super.onBackPressed();
        }
        else
        {
            Intent intent = new Intent(this, MapListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
    
}
