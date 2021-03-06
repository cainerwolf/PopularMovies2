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
import java.util.ArrayList;

/**
 * Created by rhawley on 8/15/15.
 */
public class FetchTrailerTask extends AsyncTask<String, Void, Trailer[]> {

    // Create a callback interface for use with my ASyncTask
    public interface iCallBack {
        public void onAsyncTaskCompleted(Trailer[] Trailers);
    }

    // Create a log tag for when things go wrong
    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    // My db API key is now stored in its own class, ignored in Git.
    // You can add the file, and include:
    // public static final String AK="yourkeygoeshere"
    // or replace API.AK with your own key;
    private final String API_KEY = API.AK;

    // Create a callback
    private iCallBack CallBack;

    // Create a Trailer Array
    private Trailer[] Trailers;

    public FetchTrailerTask(iCallBack CallBack) {
        super();
        this.CallBack = CallBack;
    }

    @Override
    protected Trailer[] doInBackground(String... params) {

        // If we didn't get the setting for how to sort the movies, quit.
        if (params.length == 0) {
            return null;
        }

        // Declare these outside the try/catch so they can be closed in finally
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // JSON response as string
        String trailerInfoStr = null;

        try {
            // Make the URL for the API query
            final String MOVIE_ID = params[0];
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + MOVIE_ID +
                    "/videos?";
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

            trailerInfoStr = buffer.toString();

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
            this.Trailers = getTrailerArray(trailerInfoStr);
            return this.Trailers;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // If all else fails, return null
        return null;

    }

    @Override
    protected void onPostExecute(Trailer[] Trailers) {
        CallBack.onAsyncTaskCompleted(Trailers);
    }

    private Trailer[] getTrailerArray(String trailerInfoStr)
            throws JSONException {

        // The following info will be retrieved from the JSON string,
        // and then parsed to return the various videos whose type is "trailer"
        // it is also being parsed to only return those items that are on Youtube
        // Review - content

        final String MDB_RESULTS = "results";
        final String MDB_YOUTUBE_KEY = "key";
        final String MDB_NAME = "name";
        final String MDB_TYPE = "type";
        final String MDB_SITE = "site";

        JSONObject trailerJson = new JSONObject(trailerInfoStr);
        JSONArray trailerArray = trailerJson.getJSONArray(MDB_RESULTS);

        ArrayList<Trailer> tmpTrailer = new ArrayList<Trailer>(trailerArray.length());

        for (int i = 0; i < trailerArray.length(); i++) {
            // Create a new Trailer in the Trailer ArrayList for each item
            // that has a type of "trailer" and a site of "youtube"
            JSONObject trailerInfo = trailerArray.getJSONObject(i);

            if (trailerInfo.getString(MDB_TYPE).equals("Trailer") &&
                    trailerInfo.getString(MDB_SITE).equals("YouTube")) {
                String strName = trailerInfo.getString(MDB_NAME);
                String strYoutubeKey = trailerInfo.getString(MDB_YOUTUBE_KEY);
                tmpTrailer.add(new Trailer(strName,strYoutubeKey));
            }
        }

        tmpTrailer.trimToSize();

        Trailer[] Trailers = new Trailer[tmpTrailer.size()];

        tmpTrailer.toArray(Trailers);

        return Trailers;
    }

}

