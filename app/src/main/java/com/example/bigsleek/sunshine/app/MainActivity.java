/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.bigsleek.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bigsleek.sunshine.app.sync.SunshineSyncAdapter;

public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback, SwipeRefreshLayout.OnRefreshListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mLocation;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f); // Avoid casting an action bar shadow in one pane mode
        }

        /**
         * Plumbing through from MainActivity to ForecastAdapter
         * to decide what listview to use for phone or tablet.
         */
        ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

        /* Get swipeRefreshLayout and assign listener for update */
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeLayout.canChildScrollUp();


        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideSwipeProgress();
                Log.d(LOG_TAG, "onRefresh Called");
                SunshineSyncAdapter.syncImmediately(getApplicationContext());
                Log.d(LOG_TAG, "Sync Called after onRefresh");
            }
        }, 5000);
    }

    /* To avoid showing progress */
    public void showSwipeProgress() {
        swipeLayout.setRefreshing(true);
    }


    /* To avoid showing progress */
    public void hideSwipeProgress() {
        swipeLayout.setRefreshing(false);
    }

    /* Enable swipe gesture */
    public void enableSwipeGesture() {
        swipeLayout.setEnabled(true);
    }

    /* To disable swipe gesture, this prevents manual gestures but can be started programmatically */
    public void disableSwipeGesture() {
        swipeLayout.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation( this );
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if ( null != ff ) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);

            if ( null != df ) {
               df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
       if (mTwoPane) {
           /**
            * In two-pane mode, show the detail view in this activity by
            * adding or replacing the detail fragment using a
            * fragment transaction
            */
           Bundle args = new Bundle();
           args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

           DetailFragment fragment = new DetailFragment();
           fragment.setArguments(args);

           getSupportFragmentManager().beginTransaction()
                   .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                   .commit();

       }
       else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
           startActivity(intent);
       }

    }
}
