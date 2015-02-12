package com.example.bigsleek.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        ArrayAdapter< String > mForecastAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

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

            // Get a reference to the ListView, and attach an adapter to this
            ListView listView;
            listView = (ListView)
                    rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(mForecastAdapter);


            HttpURLConnection connection = null;
            BufferedReader reader     = null;
            String forecastJsonStr    = null;

            try {
                URL url    = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=02453&mode=json&units=metric&cnt=7");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuilder builder   = new StringBuilder();
                reader                  = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                forecastJsonStr = builder.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return rootView;
        }
    }
}
