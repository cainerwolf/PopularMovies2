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

    private Movie movie;
    private Trailer[] trailers;
    private Review[] reviews;
    private SharedPreferences sharedPref;

    private Set<String> favoritesTemp = new HashSet<String>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if ( id == R.id.action_favorite ) {
            doFavorite();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isFavorite() {
        return favoritesTemp.contains(movie.getIdAsString());
    }

    public void doFavorite() {

        if ( isFavorite() ) {
            Toast.makeText(getActivity().getApplicationContext(), "Removing Favorite!", Toast.LENGTH_SHORT).show();
            favoritesTemp.remove(movie.getIdAsString());
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Adding Favorite!", Toast.LENGTH_SHORT).show();
            favoritesTemp.add(movie.getIdAsString());
        }

        updateIcon();
    }

    // This updates the Favorite Icon by forcing the activity to redraw the action bar
    public void updateIcon () {

        getActivity().invalidateOptionsMenu();

    }

    public DetailActivityFragment() {

        //TODO: Make the title bar thing permanent
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

        // Get SharedPreferences, the favorites list, and the Favorite menu item
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        Menu tempMenu = DetailActivity.getMenu();
//        Log.v("HEREHERE", "Temp Menu size is " + Integer.toString(tempMenu.size()));
//        Log.v("HEREHERE", "Action Favorite is " + Integer.toString(R.id.action_favorite));
//        this.favMenuItem = DetailActivity.getMenu().findItem(R.id.action_favorite);
//        this.favMenuItem = tempMenu.findItem(R.id.action_favorite);

        // TODO: Replace this with the actual favorites menu
        Set<String> favorites = sharedPref.getStringSet("favorites", null);
        if ( favorites == null ) {
            favoritesTemp.clear();
            favoritesTemp.add("211672");
            favoritesTemp.add("157336");
            favoritesTemp.add("76341");
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

            getActivity().setTitle(titleStr);

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
