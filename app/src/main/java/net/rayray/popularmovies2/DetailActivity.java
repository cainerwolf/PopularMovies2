package net.rayray.popularmovies2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;


public class DetailActivity extends Activity implements DetailActivityFragment.Callback {

    private String mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        // If we have a movieId stored, then let's use it with the icon

        if (mMovieId != null) {

            // Get favorites menu item
            MenuItem mFavItem = menu.findItem(R.id.action_favorite);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            Set<String> prefFavorites = sharedPref.getStringSet("favorites", new HashSet<String>());

            // If the movie is a favorite, make sure we use the filled-in star icon
            // If the movie is not a favorite, use the empty star icon
            if (prefFavorites.contains(mMovieId)) {
                mFavItem.setIcon(R.mipmap.ic_star_black_48dp);
                mFavItem.setVisible(true);
            } else {
                mFavItem.setIcon(R.mipmap.ic_star_border_black_48dp);
                mFavItem.setVisible(true);
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
        // https://discussions.udacity.com/t/bug-artist-list-activity-is-destroyed-on-navigating-up-
        // but-not-back/21076/12?u=raymond_277807911216
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

    public void onFavoritesClicked(String movieId) {
        // This method will either add or remove movieId from the SharedPreferences favorites list
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        Set<String> prefFavorites = sharedPref.getStringSet("favorites", new HashSet<String>());

        Set<String> favoritesTemp = new HashSet<String>();

        // Per the android development documentation, it does not recommend making any changes
        // to a Set<String> that has been retreived from SharedPreferences.  For that reason, I
        // copy all the items to a temporary Set<String>, and maniuplate that temporary Set instead
        favoritesTemp.addAll(prefFavorites);

        if (favoritesTemp.contains(movieId)) {
            Toast.makeText(this.getApplicationContext(), "Removing Favorite!",
                    Toast.LENGTH_SHORT).show();
            favoritesTemp.remove(movieId);
            sharedPrefEditor.putStringSet("favorites", favoritesTemp)
                    .apply();
        } else {
            Toast.makeText(this.getApplicationContext(), "Adding Favorite!",
                    Toast.LENGTH_SHORT).show();
            favoritesTemp.add(movieId);
            sharedPrefEditor.putStringSet("favorites", favoritesTemp)
                    .apply();
        }

        // Adjust the icon
        isFavorite(movieId);

    }

    public void isFavorite(String movieId) {
        // This method will set the movieId for the Activity, and then force the activity
        // to redraw the action bar.

        // If the incoming movieId is null, then we're not doing anything

        if (movieId != null ) {
            mMovieId = movieId;
            invalidateOptionsMenu();
        }

    }

}
