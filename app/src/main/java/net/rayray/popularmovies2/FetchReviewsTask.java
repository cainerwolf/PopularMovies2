package net.rayray.popularmovies2;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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
public class FetchReviewsTask extends AsyncTask<String, Void, Review[]> {

    // Create a callback interface for use with my ASyncTask
    public interface iCallBack {
        public void onAsyncTaskCompleted(Review[] Reviews);
    }

    // Create a log tag for when things go wrong
    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    // My db API key is now stored in its own class, ignored in Git.
    // You can add the file, and include:
    // public static final String AK="yourkeygoeshere"
    // or replace API.AK with your own key;
    private final String API_KEY = API.AK;

    // Create a callback
    private iCallBack CallBack;

    // Create a Movie Array
    private Review[] Reviews;

    public FetchReviewsTask(iCallBack CallBack) {
        super();
        this.CallBack = CallBack;
    }

    @Override
    protected Review[] doInBackground(String... params) {

        // If we didn't get the setting for how to sort the movies, quit.
        if (params.length == 0) {
            return null;
        }

        // Declare these outside the try/catch so they can be closed in finally
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // JSON response as string
        String reviewInfoStr = null;

        try {
            // Make the URL for the API query
            final String MOVIE_ID = params[0];
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + MOVIE_ID + "/reviews?";
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

            reviewInfoStr = buffer.toString();

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
            this.Reviews = getReviewArray(reviewInfoStr);
            return this.Reviews;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // If all else fails, return null
        return null;

    }

    @Override
    protected void onPostExecute(Review[] Reviews) {
        CallBack.onAsyncTaskCompleted(Reviews);
    }

    private Review[] getReviewArray(String reviewInfoStr)
            throws JSONException {

        // The following info will be retrieved from the JSON string,
        // and then added to an array of Reviews which will be returned
        // Reviewer - author
        // Review - content

        final String MDB_RESULTS = "results";
        final String MDB_REVIEWER = "author";
        final String MDB_REVIEW = "content";

        JSONObject reviewJson = new JSONObject(reviewInfoStr);
        JSONArray reviewArray = reviewJson.getJSONArray(MDB_RESULTS);

        Review[] Reviews = new Review[reviewArray.length()];

        for (int i = 0; i < reviewArray.length(); i++) {
            // Create a new Movie in the Movies array for each movie returned
            JSONObject reviewInfo = reviewArray.getJSONObject(i);

            String strReviewer = reviewInfo.getString(MDB_REVIEWER);
            String strReview = reviewInfo.getString(MDB_REVIEW);

            Reviews[i] = new Review(strReviewer, strReview);
        }
        return Reviews;
    }

}

