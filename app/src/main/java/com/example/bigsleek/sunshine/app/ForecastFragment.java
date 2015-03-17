package com.example.bigsleek.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by BigSleek on 2/12/15.
 *
 * A placeholder fragment containing a simple view.
 */

public class ForecastFragment extends Fragment {
    ArrayAdapter< String > mForecastAdapter;

    public ForecastFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Allow the fragment handle action bar stuff
        setHasOptionsMenu(true);
    }

    // Inflate our forecastfragment xml unto the options menu
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            /*
             *  Now that we have some dummy data forecast,
             *  1. Create ArrayAdapter
             *  2. Give it a data source arraylist to populate the ListView it's attached to.
             *  3. Source is from the location preference.
             */

        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // Get application current context
                        R.layout.list_item_forecast, // Listview layout
                        R.id.list_item_forecast_TextView, // Listview textviews to populate
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach an adapter to this
        ListView listView;
        listView = (ListView)
                rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        // Add a list item onClickListener to interact with the list view when clicked.
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /**
                 * Start DetailedActivity Fragment with forecast
                 * information from listview
                 */

                startActivity(new Intent(getActivity(), DetailActivity.class)
                .putExtra(Intent.EXTRA_TEXT, mForecastAdapter.getItem(position).toString()));
            }
        });

        return rootView;
    }

    // Private class that fetches weather data via an Async tax and populate
    // list view
    private void updateWeather() {

        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);

        // Create a reference to a sharedpreference and retrieve location preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        fetchWeatherTask.execute(location);
    }

    /**
     * Override onstart method with update weather as well.
     * Get location preference weather data
     */
    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }
}