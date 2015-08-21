package net.rayray.popularmovies2;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    // Create class variables for the movie, the trailer array, and the review array
    private Movie movie;
    private Trailer[] trailers;
    private Review[] reviews;

    // Create a shared preferences object for use with determining and adding/removing movie favorites
    private SharedPreferences sharedPref;

    // Temporary Set<String> to hold the favorites in, so we aren't messing with the set returned
    // from SharedPreferences
    private Set<String> favoritesTemp = new HashSet<String>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if ( id == R.id.action_favorite ) {
            adjustFavorite();
        }

        return super.onOptionsItemSelected(item);
    }

    // Check to see if the movie is a favorite
    public boolean isFavorite() {
        return favoritesTemp.contains(movie.getIdAsString());
    }


    // This happens when someone clicks the "favorite" icon in the action bar
    // If the movie is already a favorite, then remove it from the favorites.
    // If the movie is not a favorite, then add it to the favorites
    public void adjustFavorite() {

        if ( isFavorite() ) {
            Toast.makeText(getActivity().getApplicationContext(), "Removing Favorite!", Toast.LENGTH_SHORT).show();
            favoritesTemp.remove(movie.getIdAsString());
            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putStringSet("favorites", favoritesTemp);
            sharedPrefEditor.apply();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Adding Favorite!", Toast.LENGTH_SHORT).show();
            favoritesTemp.add(movie.getIdAsString());
            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putStringSet("favorites", favoritesTemp);
            sharedPrefEditor.apply();
        }

        // This updates the Favorite Icon by forcing the activity to redraw the action bar
        getActivity().invalidateOptionsMenu();
    }

    public DetailActivityFragment() {

        //TODO: Adjust styles to include color changes
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray("trailers", trailers);
        outState.putParcelableArray("reviews", reviews);
        outState.putParcelable("movie", movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure the activity knows that this fragment handles menu options
        setHasOptionsMenu(true);

        // Get SharedPreferences
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Create a temporary String Set based on the set returned from SharedPreferences
        // Based on comments made here, it is a bad idea to change any within the set returned
        // by Shared Preferences
        // http://developer.android.com/reference/android/content/SharedPreferences.html#getStringSet(java.lang.String, java.util.Set<java.lang.String>)
        // If the set returned from SharedPreferences is null (there are no SharedPreferences)
        // then we'll just make sure our temporary set is empty
        Set<String> favorites = sharedPref.getStringSet("favorites", null);
        if ( favorites == null ) {
            favoritesTemp.clear();
        } else {
            favoritesTemp.clear();
            favoritesTemp.addAll(favorites);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra("movie") && intent != null) {
            //Get the movie object from the Intent
            movie = intent.getParcelableExtra("movie");

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

            // Set the action bar title to the movie title
            getActivity().setTitle(titleStr);


            if (savedInstanceState == null || !savedInstanceState.containsKey("trailers")) {
                refreshTrailers();
            } else {
                Parcelable[] pa = savedInstanceState.getParcelableArray("trailers");
                trailers = new Trailer[pa.length];
                for (int i = 0; i<pa.length; i++) {
                    trailers[i] = (Trailer) pa[i];
                }
                updateTrailers((LinearLayout) rootView.findViewById(R.id.llTrailerContainer), trailers);
            }

            if (savedInstanceState == null || !savedInstanceState.containsKey("reviews")) {
                refreshReviews();
            } else {
                Parcelable[] pa = savedInstanceState.getParcelableArray("reviews");
                reviews = new Review[pa.length];
                for (int i = 0; i < pa.length ; i++ ) {
                    reviews[i] = (Review) pa[i];
                }
                updateReviews((LinearLayout) rootView.findViewById(R.id.llReviewContainer), reviews);
            }

        }

        return rootView;
    }

    private void updateTrailers(LinearLayout container, Trailer[] trailers) {

        LinearLayout trailerContainer;

        if (container == null) {
            trailerContainer = (LinearLayout) getActivity().findViewById(R.id.llTrailerContainer);
        } else {
            trailerContainer = container;
        }

        // Get our initial Child Count
        int initialChildCount = trailerContainer.getChildCount();

        // Add our trailers
        for (int i=0; i < trailers.length; i++) {
            TrailerView trailer = new TrailerView(this.getActivity().getBaseContext());
            trailer.setText(trailers[i].getName());
            trailer.doOnClick(trailers[i].getYoutubeKey());
            trailerContainer.addView(trailer);
        }

        // If we added anything to the container, then make the container visible
        if ( trailerContainer.getChildCount() > initialChildCount ) {
            trailerContainer.setVisibility(View.VISIBLE);
        }

    }

    private void updateReviews(LinearLayout container, Review[] reviews) {

        LinearLayout reviewContainer;

        if (container ==null) {
            reviewContainer = (LinearLayout) getActivity().findViewById(R.id.llReviewContainer);
        } else {
            reviewContainer = container;
        }

        //get our initial Child Count
        int initialChildCount = reviewContainer.getChildCount();

        // Add our reviews
        for (int i=0; i< reviews.length; i++) {
            ReviewView review = new ReviewView(this.getActivity().getBaseContext());
            review.setText(reviews[i].getReviewer(), reviews[i].getReview());
            reviewContainer.addView(review);
        }

        // If we added anything to the container, then make the container visible
        if ( reviewContainer.getChildCount() > initialChildCount ) {
            reviewContainer.setVisibility(View.VISIBLE);
        }

    }

    public void refreshTrailers() {

        FetchTrailerTask trailerTask = new FetchTrailerTask(
                new FetchTrailerTask.iCallBack() {
                    @Override
                    public void onAsyncTaskCompleted(Trailer[] Trailers) {
                        trailers = Trailers;
                        updateTrailers(null, trailers);
                    }
                }
        );
        trailerTask.execute(movie.getIdAsString());
    }

    public void refreshReviews () {

        FetchReviewsTask reviewsTask = new FetchReviewsTask(
                new FetchReviewsTask.iCallBack() {
                    @Override
                    public void onAsyncTaskCompleted(Review[] Reviews) {
                        reviews = Reviews;
                        updateReviews(null, reviews);
                    }
                }
        );
        reviewsTask.execute(movie.getIdAsString());
    }

    // Childview class example taken from http://stackoverflow.com/questions/14798826/duplicate-views-on-android-during-run-time

    public class TrailerView extends LinearLayout {

        private TextView tv;
        private ImageView iv;

        public TrailerView(final Context context) {
            super(context);

            View.inflate(context, R.layout.trailer_item, this);
            tv = (TextView) findViewById(R.id.tvTrailer);
            iv = (ImageView) findViewById(R.id.ivYouTubeIcon);

        }

        public void setText(String text) {
            tv.setText(text);
        }

        public void doOnClick(String text) {

            final String newText = text;

            OnClickListener click = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Youtube Intent taken from http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + newText));
                    startActivity(intent);
                }
            };

            tv.setOnClickListener(click);

            iv.setOnClickListener(click);

        }

    }

    public class ReviewView extends LinearLayout {

        private TextView tvReviewer;
        private TextView tvReview;

        public ReviewView(final Context context) {
            super(context);

            View.inflate(context, R.layout.review_item, this);
            tvReviewer = (TextView) findViewById(R.id.tvReviewer);
            tvReview = (TextView) findViewById(R.id.tvReview);

        }

        public void setText(String strReviewer, String strReview) {
            tvReviewer.setText(strReviewer);
            tvReview.setText(strReview);
        }

    }

}
