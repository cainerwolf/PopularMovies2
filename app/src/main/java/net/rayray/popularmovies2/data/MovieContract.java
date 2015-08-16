package net.rayray.popularmovies2.data;

import android.net.Uri;

/**
 * Created by rhawley on 8/15/15.
 */
public class MovieContract {

    // Content authority for my program
    public static final String CONTENT_AUTHORITY = "net.rayray.popularmovies2";

    // Base URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // TODO: Make the contract for the locally stored movies.
}
