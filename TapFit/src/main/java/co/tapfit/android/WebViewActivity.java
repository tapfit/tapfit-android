package co.tapfit.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class WebViewActivity extends BaseActivity {

    public static final String URL = "url";
    public static final String TITLE = "title";
    private static final String TAG = WebViewActivity.class.getSimpleName();

    private String mUrl;
    private String mTitle;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mUrl = getIntent().getStringExtra(URL);
        mTitle = getIntent().getStringExtra(TITLE);

        getSupportActionBar().setTitle(mTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = (WebView) findViewById(R.id.web_view);

        mWebView.loadUrl(mUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.web_view, menu);
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
    
}
