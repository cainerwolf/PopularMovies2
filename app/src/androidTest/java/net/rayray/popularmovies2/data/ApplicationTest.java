package net.rayray.popularmovies2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import net.rayray.popularmovies2.Movie;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends AndroidTestCase {

    static final Movie myMovie = new Movie(88888, "Head of the Class", "07/20/1979", "crap.jpg", "7.9", "Holy cow it's going in");

    void deleteDatabase() { mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME); }

    static ContentValues createCV() {
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, myMovie.getId());
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, myMovie.getTitle());
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, myMovie.getReleaseDate());
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, myMovie.getPosterPath());
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, myMovie.getVoteAverage());
        testValues.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, myMovie.getSynopsis());

        return testValues;
    }

    public void setUp() {
        deleteDatabase();
    }

    @SmallTest
    public void testFirst(Context context) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = createCV();

        long locationRowId;
        locationRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert data", locationRowId != -1);
        assertTrue(false);
    }

}