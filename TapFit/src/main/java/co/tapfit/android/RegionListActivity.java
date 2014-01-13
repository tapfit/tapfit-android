package co.tapfit.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import co.tapfit.android.adapter.RegionListAdapter;
import co.tapfit.android.helper.SharePref;
import co.tapfit.android.model.Region;
import co.tapfit.android.request.PlaceRequest;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.view.TapFitProgressDialog;

public class RegionListActivity extends BaseActivity {

    private ListView mRegionList;
    private RegionListAdapter mRegionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_list);

        mRegionList = (ListView) findViewById(R.id.region_list);

        mRegionAdapter = new RegionListAdapter(getApplicationContext(), dbWrapper.getRegions());

        mRegionList.setAdapter(mRegionAdapter);

        mRegionList.setOnItemClickListener(clickOnRegion);

    }

    AdapterView.OnItemClickListener clickOnRegion = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Region region = (Region) adapterView.getItemAtPosition(i);

            SharePref.setBooleanPref(getApplicationContext(), SharePref.SELECTED_REGION, true);
            if (region == RegionListAdapter.CURRENT_LOCATION) {
                startMapListActivity();
            }
            else if (region == RegionListAdapter.HEADER) {
                return;
            }
            else
            {
                LatLng location = new LatLng(region.lat, region.lon);

                if (isUserInRegion(location, region)){
                    startMapListActivity();
                }
                else {
                    final TapFitProgressDialog progressDialog = new TapFitProgressDialog(RegionListActivity.this);
                    progressDialog.setMessage("Loading " + region.name + "...");
                    progressDialog.show();
                    PlaceRequest.getPlaces(getApplicationContext(), location, true, new ResponseCallback() {
                        @Override
                        public void sendCallback(Object responseObject, String message) {
                            progressDialog.cancel();
                            startMapListActivity();
                        }
                    });
                }
            }
        }
    };

    private boolean isUserInRegion(LatLng location, Region region) {

        Double minLat = region.lat - ((double) region.radius / 69.0);
        Double maxLat = region.lat + ((double) region.radius / 69.0);
        Double minLon = region.lon - ((double) region.radius / 69.0);
        Double maxLon = region.lon + ((double) region.radius / 69.0);

        if (location.latitude >= minLat && location.latitude <= maxLat && location.longitude >= minLon && location.longitude <= maxLon) {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void startMapListActivity() {
        Intent intent = new Intent(RegionListActivity.this, MapListActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.region_list, menu);
        return true;
    }
    
}
