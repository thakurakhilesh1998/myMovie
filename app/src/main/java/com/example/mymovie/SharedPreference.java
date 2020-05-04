package com.example.mymovie;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SharedPreference extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_visualiser);
    }
}
