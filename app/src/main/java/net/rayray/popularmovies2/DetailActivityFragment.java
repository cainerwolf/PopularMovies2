package net.rayray.popularmovies2;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra("movie") && intent != null) {
            //Get the movie object from the Intent
            Movie movie = intent.getParcelableExtra("movie");

            // Create our objects for the ImageView and TextViews that need to be populated
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.posterImageView);
            TextView titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
            TextView voteAverageTextView = (TextView) rootView.findViewById(R.id.voteAverageTextView);
            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.releaseDateTextView);
            TextView synopsisTextView = (TextView) rootView.findViewById(R.id.synopsisTextView);


            // Get our Strings from the movie object passed in the Intent
            String url = movie.getFullPosterPath();
            String titleStr = movie.getTitle();
            String voteAverageStr = movie.getVoteAverage();
            String releaseDateStr = movie.getReleaseDate();
            String synopsisStr = movie.getSynopsis();

            // Populate the ImageView and TextViews with all our gathered info
            Picasso.with(rootView.getContext()).load(url).into(posterImageView);
            titleTextView.setText(titleStr);
            voteAverageTextView.setText(voteAverageStr);
            releaseDateTextView.setText(releaseDateStr);
            synopsisTextView.setText(synopsisStr);

        }

        return rootView;
    }
}
