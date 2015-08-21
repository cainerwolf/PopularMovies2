package net.rayray.popularmovies2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.Set;


public class DetailActivity extends Activity {

    private String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the movieID from the Intent
        Intent intent = getIntent();
        if ( intent.hasExtra("movie") ) {
            Movie movie = intent.getParcelableExtra("movie");
            this.movieId = movie.getIdAsString();
            // Refresh the options menu now that we have the movie id
            invalidateOptionsMenu();
        }

        setContentView(R.layout.activity_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        // Set the favorites icon based on preferences
        // Get favorites icon
        MenuItem favItem = menu.findItem(R.id.action_favorite);

        // Check to see if we have the movie ID yet, and if we do let's see if its a favorite
        if ( this.movieId != null ) {
            // Get the list of favorite movies from Shared Preferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            Set<String> prefFavorites = sharedPref.getStringSet("favorites", new HashSet<String>());

            // If the movie is a favorite, make sure we use the filled-in star icon
            // If the movie is not a favorite, use the empty star icon
            if (prefFavorites.contains(movieId)) {
                favItem.setIcon(R.mipmap.ic_star_black_48dp);
            } else {
                favItem.setIcon(R.mipmap.ic_star_border_black_48dp);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // The following lines were taken from the Udacity forums, at
        // https://discussions.udacity.com/t/bug-artist-list-activity-is-destroyed-on-navigating-up-but-not-back/21076/12?u=raymond_277807911216
        // to fix the problem of a new Main Activity being created when the "Up" button
        // in the top-left of the action bar is clicked

        // overwrite "Home"-Button (Navigation Button on left) to emulate the behaviour of
        // the hardware back-button so that the state of the previous activity is retained
        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_favorite) {
            // Handled in Fragment
            return false;
        }

        return super.onOptionsItemSelected(item);
    }
}
