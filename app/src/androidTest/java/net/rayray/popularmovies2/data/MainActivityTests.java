package net.rayray.popularmovies2.data;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import net.rayray.popularmovies2.MainActivity;

/**
 * Created by rhawley on 8/19/15.
 */
public class MainActivityTests extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTests() {
        super(MainActivity.class);
    }

    @SmallTest
    public void testFirst() {
        MainActivity activity = getActivity();
        assertNotNull(null);
    }
}
