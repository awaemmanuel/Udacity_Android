package com.example.bigsleek.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_map) {
            openPreferredLocationToMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper function to implicitly start an intent that maps the preferred location on map
     */
    private void openPreferredLocationToMap() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPreferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        /**
         * Using the URI scheme for showing a location found on a map
         */
        Uri geolocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();


        // Start a new intent with ACTION_VIEW to view map
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geolocation);

        // Start intent only if there is an app that can handle it
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            String error_string = "Couldn't find location: " + location + ", no receiving apps installed!";
            Log.d(LOG_TAG, error_string );
            Toast toast = Toast.makeText(getApplicationContext(), error_string, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
