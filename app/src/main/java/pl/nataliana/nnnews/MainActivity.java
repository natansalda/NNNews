package pl.nataliana.nnnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Info>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    public InfoAdapter mAdapter;
    public static final String REQUEST_URL = "http://content.guardianapis.com/";
    public TextView mEmptyStateTextView;
    public ProgressBar progressBar;
    public static int IS_SECTION_SEARCH = 0;
    public String mSectionToMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            IS_SECTION_SEARCH = bundle.getInt("IS_SECTION_SEARCH");
            mSectionToMonitor = bundle.getString("sectionToMonitor");
        }

        //Get the ListView
        ListView listView = (ListView) findViewById(R.id.list);

        //Get the TextView with the ID empty_view and set it as an empty view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);

        //Get the ProgressBar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //Create an InfoAdapter
        mAdapter = new InfoAdapter(this, new ArrayList<Info>());
        listView.setAdapter(mAdapter);

        //Create a ConnectivityManager to get network info
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get network info and create a boolean
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {

            getLoaderManager().initLoader(IS_SECTION_SEARCH, null, this);

        } else {

            //If there is no internet connection
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_wifi);
            Log.e(LOG_TAG, getString(R.string.no_wifi));
        }

    }

    @Override
    public Loader<List<Info>> onCreateLoader(int id, Bundle args) {

        //Get the topic indicated by the user in the Preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String topicToMonitor = sharedPrefs.getString(getString(R.string.settings_key),getString(R.string.settings_default)).toLowerCase().replace(" ", "");

        //Create a Uri and a UriBuilder
        Uri baseUri;
        Uri.Builder uriBuilder = new Uri.Builder();

        if (id == 0) {

            baseUri = Uri.parse(REQUEST_URL + "search");
            uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("q", topicToMonitor);
        }

        if (id == 1){

            String sectionToMonitor = mSectionToMonitor.toLowerCase().replace(" ", "");
            baseUri = Uri.parse(REQUEST_URL + sectionToMonitor);
            uriBuilder = baseUri.buildUpon();
        }

        //Append the API key to the request URL
        uriBuilder.appendQueryParameter("api-key", "test");

        //Create an InfoLoader with the request URL
        return new InfoLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Info>> loader, List<Info> news) {

        progressBar.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_news);

        //Clear the Adapter
        mAdapter.clear();

        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }

        //Set the section search constant back to default
        IS_SECTION_SEARCH = 0;

    }

    @Override
    public void onLoaderReset(Loader<List<Info>> loader) {

        //Clear the Adapter when reseting the Loader
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate the menu with menu.xml
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Create an Intent to open the Settings page when the settings icon is clicked
        if (id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
