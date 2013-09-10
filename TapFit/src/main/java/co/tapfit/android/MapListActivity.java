package co.tapfit.android;

import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import co.tapfit.android.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import co.tapfit.android.fragment.MapListFragment;
import co.tapfit.android.fragment.PlaceMapFragment;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.request.PlaceRequest;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;

public class MapListActivity extends BaseActivity {

    private static final String TAG = MapListActivity.class.getSimpleName();

    private String[] mMenuOptions;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private TextView mBottomButtonText;
    private FrameLayout mBottomButton;

    private MapListFragment mMapListFragment;
    private PlaceMapFragment mPlaceMapFragment;

    private ResponseCallback callback = new ResponseCallback() {
        @Override
        public void sendCallback(Object responseObject, String message) {
            Log.d(TAG, "Received the callback");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);

        initializeActionBar();

        initializeFragments();

    }

    private void initializeFragments() {

        mBottomButtonText = (TextView) findViewById(R.id.bottom_button_text);
        mBottomButton = (FrameLayout) findViewById(R.id.bottom_button);

        mMapListFragment = new MapListFragment();

        mPlaceMapFragment = new PlaceMapFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_frame, mPlaceMapFragment, "Map Fragment")
                .add(R.id.content_frame, mMapListFragment, "List Fragment")
                .hide(mMapListFragment)
                .commit();

        mBottomButtonText.setText("View List");

        mBottomButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mBottomButton.setBackgroundResource(R.color.dark_gray);
                        if (mPlaceMapFragment.isVisible())
                        {
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.hide(mPlaceMapFragment)
                                    .show(mMapListFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();

                            mBottomButtonText.setText("View Map");

                        }
                        else
                        {
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.hide(mMapListFragment)
                                    .show(mPlaceMapFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                    .commit();
                            mBottomButtonText.setText("View List");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mBottomButton.setBackgroundResource(R.color.light_gray);
                        break;
                }
                return true;
            }
        });

        mBottomButton.setOnClickListener(switchMapAndList);
    }

    private void initializeActionBar() {

        mMenuOptions = getResources().getStringArray(R.array.menu_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.menu_list_item, R.id.menu_item, mMenuOptions));

        mTitle = mDrawerTitle = getTitle();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    View.OnClickListener switchMapAndList = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        Log.d(TAG, "drawOpen: " + drawerOpen);
        menu.findItem(R.id.action_menu).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_list, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
}
