package net.rayray.popularmovies2;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    // Create a callback interface for use with my ASyncTask
    public interface iCallBack {
        public void onAsyncTaskCompleted(Movie[] Movies);
    }

    // This will update the movie posters on the main page by starting a new fetch of data
    public void refreshMovies() {

        FetchMovieListTask movieTask = new FetchMovieListTask(
                new iCallBack () {

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
            mMovies = (Movie[]) savedInstanceState.getParcelableArray("movies");
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

    public class FetchMovieListTask extends AsyncTask<String, Void, Movie[]> {

        // Create a log tag for when things go wrong
        private final String LOG_TAG = FetchMovieListTask.class.getSimpleName();

        // My db API key
        private final String API_KEY = "f4744e1783bf46316ba2da8cd9e1ef67";

        // Create a callback
        private iCallBack CallBack;

        // Create a Movie Array
        private Movie[] Movies;

        public FetchMovieListTask(iCallBack CallBack) {
            super();
            this.CallBack = CallBack;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            // If we didn't get the setting for how to sort the movies, quit.
            if (params.length == 0) {
                return null;
            }

            // Declare these outside the try/catch so they can be closed in finally
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // JSON response as string
            String movieInfoStr = null;

            try {
                // Make the URL for the API query
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORTING_METHOD = params[0];
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter("sort_by", SORTING_METHOD)
                        .appendQueryParameter("api_key", API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Connect to themoviedb.org
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    // Nothing to do.
                    return null;
                }

                movieInfoStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // The code didn't get the movie data.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream ", e);
                    }
                }
            }

            try {
                this.Movies = getMovieArray(movieInfoStr);
                return this.Movies;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // If all else fails, return null
            return null;

        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            CallBack.onAsyncTaskCompleted(this.Movies);
        }

        private Movie[] getMovieArray(String movieInfoStr)
                throws JSONException {

            // The following info will be retrieved from the JSON string,
            // and then added to an array of Movies which will be returned
            // title - title
            // release date - release_date
            // movie poster - poster_path
            // vote average - vote_average
            // plot synopsis - overview

            final String MDB_RESULTS = "results";
            final String MDB_TITLE = "title";
            final String MDB_RELEASEDATE = "release_date";
            final String MDB_POSTERPATH = "poster_path";
            final String MDB_VOTEAVERAGE = "vote_average";
            final String MDB_OVERVIEW = "overview";

            JSONObject movieJson = new JSONObject(movieInfoStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);

            Movie[] Movies = new Movie[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {
                // Create a new Movie in the Movies array for each movie returned
                JSONObject movieInfo = movieArray.getJSONObject(i);

                String strTitle = movieInfo.getString(MDB_TITLE);
                String strReleaseDate = movieInfo.getString(MDB_RELEASEDATE);
                String strPosterPath = movieInfo.getString(MDB_POSTERPATH);
                String strVoteAverage = movieInfo.getString(MDB_VOTEAVERAGE);
                String strOverview = movieInfo.getString(MDB_OVERVIEW);

                Movies[i] = new Movie(strTitle, strReleaseDate, strPosterPath, strVoteAverage, strOverview);
            }
            return Movies;
        }

    }

}
