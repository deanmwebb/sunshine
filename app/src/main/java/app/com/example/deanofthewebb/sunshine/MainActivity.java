package app.com.example.deanofthewebb.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Printing from the onCreate method");
        setContentView(R.layout.activity_main);
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
        else if (id == R.id.action_view_map) {
            OpenPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void OpenPreferredLocationInMap() {

        //Start Implicit Intent
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String location = preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        Uri locationUri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(locationUri);

        if ( mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
        else {
            Log.d(LOG_TAG,"Could not call " + location + ": No action found");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "Printing from the onPause method");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "Printing from the onResume method");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(LOG_TAG, "Printing from the onStop method");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "Printing from the onStart method");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "Printing from the onDestroy method");
    }

}

