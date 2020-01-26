package de.hpi3d.gamepgrog.trap.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.api.StorageManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Preference resetServerButton = findPreference(getString(R.string.key_settings_reset));
            resetServerButton.setOnPreferenceClickListener((preference) -> {
                String currentUrl = StorageManager.with(getActivity()).botUrl.get();
                Toast.makeText(getContext(), currentUrl, Toast.LENGTH_SHORT).show();
                return true;
            });


            Preference debugButton = findPreference(getString(R.string.key_settings_steal));
            debugButton.setOnPreferenceClickListener((preference) -> {
                String currentSafety = String.valueOf(StorageManager.with(getActivity()).safetyMode.get());
                Toast.makeText(getContext(), currentSafety, Toast.LENGTH_SHORT).show();
                return true;
            });


            SwitchPreferenceCompat safetyPreference = findPreference(getString(R.string.key_settings_safety_mode));
            safetyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                StorageManager.with(getActivity()).safetyMode.set((boolean) newValue);
                return true;
            });


            EditTextPreference serverUrlPreference = findPreference(getString(R.string.key_settings_url));
            serverUrlPreference.setOnPreferenceChangeListener(((preference, newValue) -> {
                StorageManager.with(getActivity()).botUrl.set((String) newValue);
                return true;
            }));

        }
    }
}