package com.example.bigsleek.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Forecast Array, that will be turned into an array List of strings
            String [] forecastArray = new String[]{
                    "Today - Sunny - 88 / 63",
                    "Tomorrow - Cloudy - 45 / 43",
                    "Weds - Sunny - 78 / 63",
                    "Thurs - Sunny - 85 / 63",
                    "Fri - Sunny - 22 / 11",
                    "Sat - Sunny - 34 / 34"
            };

            List<String> weekForecast = new ArrayList<String>(
                    Arrays.asList(forecastArray) );

            /*
             *  Now that we have some dummy data forecast,
             *  1. Create ArrayAdapter
             *  2. Give it a data source arraylist to populate the ListView it's attached to.
             */



            return rootView;
        }
    }
}
