package net.rayray.popularmovies2;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rhawley on 8/15/15.
 */
public class FetchFavoritesListTask extends AsyncTask<String[], Void, Movie[]> {

    // Create a callback interface for use with my ASyncTask
    public interface iCallBack {
        public void onAsyncTaskCompleted(Movie[] Movies);
    }

    // Create a log tag for when things go wrong
    private final String LOG_TAG = FetchFavoritesListTask.class.getSimpleName();

    // My db API key is now stored in its own class, ignored in Git.
    // You can add the file, and include:
    // public static final String AK="yourkeygoeshere"
    // or replace API.AK with your own key
    private final String API_KEY = API.AK;

    // Create a callback
    private iCallBack CallBack;

    // Create a Movie Array
    private Movie[] Movies;

    public FetchFavoritesListTask(iCallBack CallBack) {
        super();
        this.CallBack = CallBack;
    }

    private String getMovieInfo(String movieId) {

        // Declare these outside the try/catch so they can be closed in finally
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // JSON response as string
        String movieInfoStr = null;

        try {
            // Make the URL for the API query
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + movieId;
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
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

        return movieInfoStr;
    }

    @Override
    protected Movie[] doInBackground(String[]... params) {

        // If we didn't get the any favorite movies to pull, then quit.
        if (params.length == 0) {
            return null;
        }

        // Get our list of favorite movies, then create a new array of movies to store the
        // returned values in
        String[] MovieIds = params[0];
        Movie[] Movies = new Movie[MovieIds.length];

        // For each movie, we're going to pull data from themoviedb.org and then add it
        // to an array

        try {
            for (int i=0; i<MovieIds.length; i++) {
                String movieInfoStr = getMovieInfo(MovieIds[i]);
                Movies[i] = getMovie(movieInfoStr);
            }
            this.Movies = Movies;
            return this.Movies;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // If all else fails, return null
        return null;

    }

    @Override
    protected void onPostExecute(Movie[] Movies) {
        CallBack.onAsyncTaskCompleted(Movies);
    }

    private Movie getMovie(String movieInfoStr)
            throws JSONException {

        // The following info will be retrieved from the JSON string
        // movie id - id
        // title - title
        // release date - release_date
        // movie poster - poster_path
        // vote average - vote_average
        // plot synopsis - overview

        final String MDB_ID = "id";
        final String MDB_TITLE = "title";
        final String MDB_RELEASEDATE = "release_date";
        final String MDB_POSTERPATH = "poster_path";
        final String MDB_VOTEAVERAGE = "vote_average";
        final String MDB_OVERVIEW = "overview";

        JSONObject movieInfo = new JSONObject(movieInfoStr);


        // Create a new Movie
        int id = movieInfo.getInt(MDB_ID);
        String strTitle = movieInfo.getString(MDB_TITLE);
        String strReleaseDate = movieInfo.getString(MDB_RELEASEDATE);
        String strPosterPath = movieInfo.getString(MDB_POSTERPATH);
        String strVoteAverage = movieInfo.getString(MDB_VOTEAVERAGE);
        String strOverview = movieInfo.getString(MDB_OVERVIEW);

        Movie Movie = new Movie(id, strTitle, strReleaseDate, strPosterPath, strVoteAverage,
                strOverview);

        return Movie;
    }
}

