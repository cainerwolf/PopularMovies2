package net.rayray.popularmovies2;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Preferences Screen
 */
public class PrefsFragment extends PreferenceFragment {

    public PrefsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }

}
