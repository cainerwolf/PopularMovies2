package net.rayray.popularmovies2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
