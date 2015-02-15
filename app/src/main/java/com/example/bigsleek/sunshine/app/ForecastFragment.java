package com.example.bigsleek.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
            fetchWeatherTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Forecast Array, that will be turned into an array List of strings
        String [] forecastArray = new String[]{
                "Today - Sunny - 80 / 63",
                "Tomorrow - Cloudy - 45 / 43",
                "Weds - Sunny - 78 / 63",
                "Thurs - Sunny - 85 / 63",
                "Fri - Sunny - 22 / 11",
                "Sat - Sunny - 34 / 34",
                "Sun - Snow  - 44 / 67"
        };

        List<String> weekForecast = new ArrayList<String>(
                Arrays.asList(forecastArray) );

            /*
             *  Now that we have some dummy data forecast,
             *  1. Create ArrayAdapter
             *  2. Give it a data source arraylist to populate the ListView it's attached to.
             */

        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // Get application current context
                        R.layout.list_item_forecast, // Listview layout
                        R.id.list_item_forecast_TextView, // Listview textviews to populate
                        weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach an adapter to this
        ListView listView;
        listView = (ListView)
                rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection connection = null;
            BufferedReader reader     = null;
            String forecastJsonStr    = null;

            try {
                URL url    = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=02453&mode=json&units=metric&cnt=7");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                String line;

                if (inputStream == null) {
                    // Nothing to do
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));


                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                // Make sure buffer isn't empty
                if ( buffer.length() == 0 ) {
                    // Nothing to do
                    return null;
                }

                forecastJsonStr = buffer.toString();

                // Verbose log statement to verify the async task returns something.
                Log.v(LOG_TAG, "Forecast json string: " + forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                // If no success in retrieving the weather data, return null
                return  null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

    }
}