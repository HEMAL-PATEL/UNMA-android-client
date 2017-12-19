package com.paperplanes.unma.settings;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.paperplanes.unma.R;

/**
 * Created by abdularis on 15/12/17.
 */

public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_settings);
        }

        setPreferenceSummary(findPreference(getString(R.string.pref_notification_sound)));
        setPreferenceSummary(findPreference(getString(R.string.pref_in_app_notification_sound)));
    }

    private static Preference.OnPreferenceChangeListener sPreferenceChangeListener =
            (preference, newValue) -> {
                String value = (String) newValue;
                Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(value));
                if (ringtone != null) {
                    preference.setSummary(ringtone.getTitle(preference.getContext()));
                }
                return true;
            };

    private void setPreferenceSummary(Preference pref) {
        pref.setOnPreferenceChangeListener(sPreferenceChangeListener);

        sPreferenceChangeListener.onPreferenceChange(pref,
                PreferenceManager
                        .getDefaultSharedPreferences(this)
                        .getString(pref.getKey(), ""));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
