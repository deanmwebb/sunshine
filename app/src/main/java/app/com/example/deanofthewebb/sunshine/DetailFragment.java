package app.com.example.deanofthewebb.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.example.deanofthewebb.sunshine.data.WeatherContract;

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = app.com.example.deanofthewebb.sunshine.DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    static final String DETAIL_URI = "URI";

    private String forecastStr;
    private Uri mUri;
    private ShareActionProvider shareActionProvider;
    private static final int DETAIL_LOADER = 0;


    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_PRESSURE = 5;
    private static final int COL_WEATHER_HUMIDITY = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView dayView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView humidtyView;
        public final TextView windView;
        public TextView pressureView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_forecast_icon);
            dayView = (TextView) view.findViewById(R.id.list_item_day_textview);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            humidtyView = (TextView) view.findViewById(R.id.list_item_humidity_textview);
            windView = (TextView) view.findViewById(R.id.list_item_wind_textview);
            pressureView = (TextView) view.findViewById(R.id.list_item_pressure_textview);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (forecastStr != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else {
            Log.d(LOG_TAG, "Share action provider is null?");
        }
    }


    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                forecastStr + FORECAST_SHARE_HASHTAG);

        return shareIntent;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }


    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (data == null || !data.moveToFirst()) { return; }

        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);

        // Read date from cursor and update views for day of week and date
        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);

        String weatherDescription = data.getString(COL_WEATHER_DESC);
        Boolean isMetric = Utility.isMetric(getActivity());

        double high = data.getDouble(COL_WEATHER_MAX_TEMP);
        String formattedHigh = Utility.formatTemperature(getActivity(), high, isMetric);
        double low = data.getDouble(COL_WEATHER_MIN_TEMP);
        String formattedLow = Utility.formatTemperature(getActivity(), low, isMetric);

        float windSpeedCol = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDirCol = data.getFloat(COL_WEATHER_DEGREES);
        String windSpeed = Utility.getFormattedWind(getActivity(), windSpeedCol, windDirCol);

        String weatherPressure = getActivity().getString(R.string.format_pressure, data.getFloat(COL_WEATHER_PRESSURE));
        String humidity = getActivity().getString(R.string.format_humidity, data.getFloat(COL_WEATHER_HUMIDITY));


        ViewHolder viewHolder = (ViewHolder) getView().getTag();


        viewHolder.dateView.setText(dateText);
        viewHolder.dayView.setText(friendlyDateText);
        viewHolder.descriptionView.setText(weatherDescription);
        viewHolder.highTempView.setText(formattedHigh);
        viewHolder.lowTempView.setText(formattedLow);
        viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        viewHolder.pressureView.setText(weatherPressure);
        viewHolder.humidtyView.setText(humidity);
        viewHolder.windView.setText(windSpeed);

        // We still need this for the share intent
        forecastStr = String.format("%s - %s - %s/%s", dateText, weatherDescription, high, low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (shareActionProvider != null && forecastStr != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}