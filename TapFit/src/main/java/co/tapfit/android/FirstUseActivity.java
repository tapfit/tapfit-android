package co.tapfit.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import co.tapfit.android.BaseActivity;

public class FirstUseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_use);
    }

}
