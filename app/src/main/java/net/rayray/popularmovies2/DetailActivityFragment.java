package net.rayray.popularmovies2;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


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

            Trailer[] Test = {
                new Trailer("Trailer 1", "kkkkkkkkk"),
                new Trailer("Trailer 2", "jjjjjjjjj"),
                new Trailer("Trailer 3", "lllllllll")
            };

            LinearLayout trailerContainer = (LinearLayout) rootView.findViewById(R.id.llTrailerContainer);

            for (int i=0; i < Test.length; i++) {
                TrailerView trailer = new TrailerView(this.getActivity().getBaseContext());
                trailer.setText(Test[i].getName());
                trailer.doOnClick(Test[i].getYoutubeKey());
                trailerContainer.addView(trailer);
            }

            trailerContainer.setVisibility(View.VISIBLE);

            Review[] Testr = {
                    new Review("Reviewer 1", "This movie was so overrated I almost puked in my popcorn bucket."),
                    new Review("Reviewer 2", "I can't believe they actually got people to make this movie, it seems like such a waste of money.  I want my $12 back, the 3d was dumb too."),
                    new Review("Reviewer 3", "It was ok.")
            };

            LinearLayout reviewContainer = (LinearLayout) rootView.findViewById(R.id.llReviewContainer);

            for (int i=0; i< Testr.length; i++) {
                ReviewView review = new ReviewView(this.getActivity().getBaseContext());
                review.setText(Testr[i].getReviewer(), Testr[i].getReview());
                reviewContainer.addView(review);
            }

            reviewContainer.setVisibility(View.VISIBLE);

            getActivity().setTitle(titleStr);

        }

        return rootView;
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

            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast t = Toast.makeText(getActivity(), newText, Toast.LENGTH_SHORT);
                    t.show();
                    Log.v("CLICKY", newText);
                }
            });

            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast t = Toast.makeText(getActivity(), newText, Toast.LENGTH_SHORT);
                    t.show();
                    Log.v("CLICKY", newText );
                }
            });

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
