package co.tapfit.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import co.tapfit.android.fragment.AccountFragment;
import co.tapfit.android.fragment.CustomerSupportFragment;
import co.tapfit.android.fragment.PassListFragment;
import co.tapfit.android.fragment.PreferencesFragment;
import co.tapfit.android.fragment.TapfitInfoFragment;
import co.tapfit.android.helper.AnalyticsHelper;
import co.tapfit.android.helper.LocationServices;
import co.tapfit.android.helper.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.flurry.android.monolithic.sdk.impl.acc;
import com.flurry.android.monolithic.sdk.impl.ft;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import co.tapfit.android.fragment.MapListFragment;
import co.tapfit.android.fragment.PlaceMapFragment;
import co.tapfit.android.model.Place;
import co.tapfit.android.request.PlaceRequest;
import co.tapfit.android.request.ResponseCallback;
import ly.count.android.api.Countly;

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

    private CameraPosition mMapLocation;

    public Boolean WORKOUT_CALLBACK_RECEIVED = false;

    private View mTapfitLogo;

    public static final String FAVORITES = "favorite";
    public static final String MAP = "map";
    public static final String PLACE_LIST = "place_list";
    public static final String ACCOUNT = "account";
    public static final String PASS_LIST = "pass_list";

    private MapListFragment mFavoriteListFragment;
    private MapListFragment mMapListFragment;
    private PlaceMapFragment mPlaceMapFragment;
    private AccountFragment mAccountFragment;
    private PassListFragment mPassListFragment;

    private ProgressDialog progressDialog;

    private String mCurrentFragment;

    private ProgressBar mLoadingBar;

    private Boolean mHasShownOutOfAreaMessage = false;

    @Override
    public void onResume(){
        super.onResume();

        if (mCurrentFragment.equals(PLACE_LIST) || mCurrentFragment.equals(MAP)) {
            AnalyticsHelper.getInstance(this).logEvent("Browse");
        }
        else if (mCurrentFragment.equals(FAVORITES)) {
            AnalyticsHelper.getInstance(this).logEvent("Favorites");
        }
        else if (mCurrentFragment.equals(ACCOUNT)) {
            AnalyticsHelper.getInstance(this).logEvent("Account");
        }
        else if (mCurrentFragment.equals(PASS_LIST)) {
            AnalyticsHelper.getInstance(this).logEvent("Passes");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FlurryAgent.onStartSession(this, getString(R.string.flurry_api_key));
        Countly.sharedInstance().onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
        Countly.sharedInstance().onStop();
    }

    @Override
    public void onDestroy() {

        AnalyticsHelper.getInstance(getApplicationContext()).sendEndOfSessionEvent();

        super.onDestroy();
    }

    @Override
    public void onPause(){
        super.onPause();

        PlaceRequest.removeCallback(placesCallback);
    }

    public void setAccountFragment(AccountFragment accountFragment) {
        this.mAccountFragment = accountFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Boolean.parseBoolean(getString(R.string.is_debug)))
        {
            Countly.sharedInstance().init(this, getString(R.string.countly_server), getString(R.string.countly_app_key_debug));
        }
        else
        {
            Countly.sharedInstance().init(this, getString(R.string.countly_server), getString(R.string.countly_app_key));
        }
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_map_list);

        initializeActionBar();

        initializeFragments();

        mMapLocation = CameraPosition.fromLatLngZoom(LocationServices.getLatLng(), 14);


        if (!PlaceRequest.GOT_INITIAL_PLACES) {
            Log.d(TAG, "GOT_INITIAL_PLACES is false");
            if (PlaceRequest.getPlaces(getApplicationContext(), LocationServices.getLatLng(), false, placesCallback)) {
                Log.d(TAG, "got places, returned true");
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading locations");
                progressDialog.setCancelable(true);
                progressDialog.show();
            }
        }
    }

    public void getPlaces(LatLng location) {
        PlaceRequest.getPlaces(getApplicationContext(), location, true, placesCallback);
    }

    public ResponseCallback placesCallback = new ResponseCallback() {
        @Override
        public void sendCallback(Object responseObject, String message) {
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.cancel();
            }
            if (mCurrentFragment == PLACE_LIST)
            {
                mMapListFragment.receivedPlacesCallback();
            }
            else if (mCurrentFragment == MAP)
            {
                mPlaceMapFragment.receivedPlacesCallback();
            }
        }
    };

    private void initializeFragments() {

        mBottomButtonText = (TextView) findViewById(R.id.bottom_button_text);
        mBottomButton = (FrameLayout) findViewById(R.id.bottom_button);

        mLoadingBar = (ProgressBar) findViewById(R.id.loading_bar);

        mMapListFragment = new MapListFragment();
        Bundle args = new Bundle();
        args.putInt(MapListFragment.LIST_TYPE, MapListFragment.MAP_LIST);
        mMapListFragment.setArguments(args);

        mFavoriteListFragment = new MapListFragment();
        args = new Bundle();
        args.putInt(MapListFragment.LIST_TYPE, MapListFragment.FAVORITE_LIST);
        mFavoriteListFragment.setArguments(args);

        mPlaceMapFragment = new PlaceMapFragment();

        mAccountFragment = new AccountFragment();

        mPassListFragment = new PassListFragment();

        replaceFragment(mMapListFragment, PLACE_LIST, false);

        mBottomButtonText.setText("View Map");

        mBottomButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mBottomButton.setBackgroundResource(R.color.blue);
                        if (mPlaceMapFragment.isVisible())
                        {
                            CameraPosition position = mPlaceMapFragment.getMapLocationCenter();
                            mMapListFragment.updateLocation(position.target);
                            mMapLocation = position;
                            addMapList();

                            mBottomButtonText.setText("View Map");

                        }
                        else
                        {
                            mPlaceMapFragment.setLoadingBar(mLoadingBar);
                            mPlaceMapFragment.setLocation(mMapLocation);
                            mPlaceMapFragment.setHasShowOutOfAreaMessage(mHasShownOutOfAreaMessage);
                            replaceFragment(mPlaceMapFragment, MAP, true);

                            mBottomButtonText.setText("View List");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mBottomButton.setBackgroundResource(R.color.dark_blue);
                        break;
                }
                return true;
            }
        });

        mBottomButton.setOnClickListener(switchMapAndList);
    }

    public void setHasShownOutOfAreaMessage(Boolean hasShownOutOfAreaMessage) {
        mHasShownOutOfAreaMessage = hasShownOutOfAreaMessage;
    }

    private void replaceFragment(Fragment fragment, String fragmentName, boolean addToBackStack) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, fragmentName)
                .setCustomAnimations(R.anim.abc_slide_out_bottom, R.anim.abc_slide_in_bottom);

        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();

        mCurrentFragment = fragmentName;
    }


    private void initializeActionBar() {

        mMenuOptions = getResources().getStringArray(R.array.menu_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.menu_list_item, R.id.menu_item, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setCustomView(R.layout.action_bar_logo);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        mTitle = mDrawerTitle = getTitle();

        getSupportActionBar().setTitle("");

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                //getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(R.color.red));
    }

    View.OnClickListener switchMapAndList = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //Log.d(TAG, "drawOpen: " + drawerOpen);
        //menu.findItem(R.id.action_menu).setVisible(!drawerOpen);
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
        if (item.getItemId() == R.id.action_search){
            replaceFragment(new PreferencesFragment(), "Preferences", true);
            mBottomButton.setVisibility(View.GONE);
            return true;
        }
        else  {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
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

    public void showCustomerSupport() {
        Intent intent = new Intent(this, ExtraSettingsActivity.class);
        intent.putExtra(ExtraSettingsActivity.PAGE, ExtraSettingsActivity.CUSTOMER_SUPPORT);
        startActivity(intent);
    }

    public void showTapfitInfo() {
        Intent intent = new Intent(this, ExtraSettingsActivity.class);
        intent.putExtra(ExtraSettingsActivity.PAGE, ExtraSettingsActivity.TAPFIT_INFO);
        startActivity(intent);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public View getBottomButton() {
        return mBottomButton;
    }

    public TextView getBottomButtonText() {
        return mBottomButtonText;
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {

        if (position == 0) {

            getSupportActionBar().setTitle("");

            getSupportActionBar().setDisplayShowCustomEnabled(true);
            addMapList();

            mBottomButton.setVisibility(View.VISIBLE);
            mBottomButtonText.setText("View Map");
        }
        else if (position == 1) {

            getSupportActionBar().setTitle("Favorites");

            replaceFragment(mFavoriteListFragment, FAVORITES, true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);

            mBottomButton.setVisibility(View.GONE);
        }
        else if (position == 2) {

            getSupportActionBar().setTitle("Passes");

            replaceFragment(mPassListFragment, PASS_LIST, true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);

            mBottomButton.setVisibility(View.GONE);
        }
        else if (position == 3) {

            getSupportActionBar().setTitle("Account");

            replaceFragment(mAccountFragment, ACCOUNT, true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);

            mBottomButton.setVisibility(View.GONE);
        }

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void addMapList() {
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(mMapListFragment, PLACE_LIST, false);
    }
}
