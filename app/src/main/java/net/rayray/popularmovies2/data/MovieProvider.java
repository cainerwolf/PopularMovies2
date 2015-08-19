package net.rayray.popularmovies2.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by rhawley on 8/19/15.
 */
public class MovieProvider extends ContentProvider {

    // Uri Matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    // Match Codes
    static final int MOVIE = 100;
    static final int MOVIE_DETAILS = 101;
    static final int TRAILER = 200;
    static final int REVIEW = 300;

    static UriMatcher buildUriMatcher() {
        // Uri Matcher for my URIs

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // Each URI has its code
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_DETAILS);
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW);

        return matcher;
    }

    private static final SQLiteQueryBuilder sTrailerQueryBuilder;

    static {
        sTrailerQueryBuilder = new SQLiteQueryBuilder();

        // Create an inner join between the Movie table and the Trailer table
        sTrailerQueryBuilder.setTables(MovieContract.TrailerEntry.TABLE_NAME + " INNER JOIN " +
                MovieContract.MovieEntry.TABLE_NAME + " ON " +
                        MovieContract.TrailerEntry.TABLE_NAME + " . " +
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = " +
                        MovieContract.MovieEntry.TABLE_NAME + ". " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID
        );
    }

    private static final SQLiteQueryBuilder sReviewQueryBuilder;

    static {
        sReviewQueryBuilder = new SQLiteQueryBuilder();

        // Create an inner join between the Movie table and the Review table
        sReviewQueryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME + " ON " +
                        MovieContract.ReviewEntry.TABLE_NAME + " . " +
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = " +
                        MovieContract.MovieEntry.TABLE_NAME + ". " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID
        );
    }

    private static final String sMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?" ;

    private Cursor getTrailerById(Uri uri, String[] projection, String sortOrder) {
        int movieId = MovieContract.MovieEntry.getIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMovieSelection;
        selectionArgs = new String[]{Integer.toString(movieId)};

        return sTrailerQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getReviewById(Uri uri, String[] projection, String sortOrder) {
        int movieId = MovieContract.MovieEntry.getIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMovieSelection;
        selectionArgs = new String[]{Integer.toString(movieId)};

        return sReviewQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return null;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_DETAILS:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
