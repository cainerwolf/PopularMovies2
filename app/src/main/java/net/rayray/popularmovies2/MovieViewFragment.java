package net.rayray.popularmovies2;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieViewFragment extends Fragment {

    // movieAdapter is for displaying the movie posters
    private MovieImageAdapter movieAdapter;

    // Movies is an array of a created class, Movie.
    private Movie[] mMovies;

    // String capture of the "current" setting we're using, to compare when OnResume is called
    private String sortSetting;

    public MovieViewFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray("movies", mMovies);
        super.onSaveInstanceState(outState);
    }

    // This will update the movie posters on the main page by starting a new fetch of data
    public void refreshMovies() {

        FetchMovieListTask movieTask = new FetchMovieListTask(
                new FetchMovieListTask.iCallBack() {

                    @Override
                    public void onAsyncTaskCompleted(Movie[] Movies) {
                        mMovies = Movies;
                        updateMovieGrid(mMovies);
                    }

                }
        );
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortSetting = sharedPref.getString("sort", "popularity.desc");
        movieTask.execute(sortSetting);

    }

    public void updateMovieGrid(Movie[] Movies) {
        String[] posterURLs = new String[Movies.length];

        for (int i = 0; i < posterURLs.length; i++) {
            posterURLs[i] = Movies[i].getFullPosterPath();
        }

        movieAdapter.clear();
        movieAdapter.addAll(new ArrayList<String>(Arrays.asList(posterURLs)));
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Create a new movie adapter for updating the grid view

        movieAdapter =
                new MovieImageAdapter(
                        getActivity(),
                        R.layout.list_item_movie,
                        R.id.list_item_movie_imageview,
                        new ArrayList<String>());

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // When a movie poster is clicked, start the detail activity.
                // A Movie object is passed to the Detail Activity, which is then retrieved
                // as a parcel.

                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie", mMovies[position]);

                startActivity(detailIntent);
            }
        });

        // Check to see if we have a saved instance state.  If we do, then assign it to our
        // mMovies array.  If not, then let's pull our data!

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            refreshMovies();
        }
        else {
            Parcelable[] pa = savedInstanceState.getParcelableArray("movies");
            mMovies = new Movie[pa.length];
            for (int i = 0; i < pa.length ; i++ ) {
                mMovies[i] = (Movie) pa[i];
            }
//            mMovies = (Movie[]) savedInstanceState.getParcelableArray("movies");
            updateMovieGrid(mMovies);
        }


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Use our saved "sortSetting" value to determine if the sorting method has changed.  If it has
        // then we refresh the movies to pull our new titles.
        if (sortSetting != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (!sortSetting.equals(sharedPref.getString("sort", "popularity.desc"))) {
                refreshMovies();
            }
        }
    }

    // Create a new array adapter to display the movie images.

    public class MovieImageAdapter extends ArrayAdapter<String> {

        public MovieImageAdapter(Context context, int resource, int imageViewResourceId, List<String> objects) {
            super(context, resource, imageViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView view ;
            if (convertView == null) {
                view = new ImageView(parent.getContext());
                // The following two lines were obtained from the Udacity forums
                // https://discussions.udacity.com/t/troubles-with-gridview-picasso-screen-sizes/25496/3
                view.setAdjustViewBounds(true);
                view.setPadding(0,0,0,0);
            }
            else {
                view = (ImageView) convertView;
            }
            String url = getItem(position);

            Picasso.with(parent.getContext()).load(url).into(view);
            return view;
        }
    }
}
