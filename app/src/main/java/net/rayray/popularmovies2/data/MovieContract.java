package net.rayray.popularmovies2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rhawley on 8/15/15.
 */
public class MovieContract {

    // Content authority for my program
    public static final String CONTENT_AUTHORITY = "net.rayray.popularmovies2";

    // Base URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Program has four possible URIs for content retrieval
    // content://net.rayray.popularmovies2/movie - Returns the movie info for the poster view
    // content://net.rayray.popularmovies2/movie/$id - Returns the details for detail view for the specified id
    // content://net.rayray.popularmovies2/trailer/$id - Returns trailers for the specified id
    // content://net.rayray.popularmovies2/review/$id - Returns the reviews for the specified id
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    // Definitions of the "Movie" table in the database
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table Name
        public static final String TABLE_NAME = "movie";

        // The movie_id is the unique integer that is used to identify movies at themoviedb.org
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // The movie title
        public static final String COLUMN_MOVIE_TITLE = "title";

        // The movie release date
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // The movie poster path
        public static final String COLUMN_POSTER_PATH = "poster_path";

        // The movie vote average, as determined by users
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        // The movie's description
        public static final String COLUMN_DESCRIPTION = "overview";

        public static Uri buildMovieUri(int id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

        public static Uri buildMovieDetail(int movieId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movieId)).build();
        }


    }

    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        //Table name
        public static final String TABLE_NAME = "trailer";

        // Movie ID as the foreign key in the Movie table
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Youtube video id
        public static final String COLUMN_TRAILER_KEY = "trailer_key";

        // The title of the trailer, as given by themoviedb.org
        public static final String COLUMN_TRAILER_TITLE = "trailer_title";

        public static Uri buildTrailerUri(int id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

        public static Uri buildTrailer(int movieId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movieId)).build();
        }


    }

    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //Table name
        public static final String TABLE_NAME = "review";

        // Movie ID as the foreign key in the Movie table
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Name of the reviewer
        public static final String COLUMN_REVIEWER = "reviewer";

        // Text of the review
        public static final String COLUMN_REVIEW = "review";

        public static Uri buildReviewUri(int id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

        public static Uri buildReview(int movieId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movieId)).build();
        }


    }

}
