package co.tapfit.android;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import co.tapfit.android.BaseActivity;
import co.tapfit.android.helper.SharePref;

public class FirstUseActivity extends Activity {

    private SharePref _appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_use);

        _appPrefs = new SharePref(getApplicationContext());

        Button nextButton = (Button) findViewById(R.id.next_button);
        Button resetButton = (Button) findViewById(R.id.reset_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _appPrefs.setFirstUse(false);
                startActivity(new Intent(FirstUseActivity.this, MapListActivity.class));
                finish();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _appPrefs.setFirstUse(true);
                finish();
            }
        });
    }

}
