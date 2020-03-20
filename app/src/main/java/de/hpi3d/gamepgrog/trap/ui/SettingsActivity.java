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
import de.hpi3d.gamepgrog.trap.android.firebase.OurFirebaseMessagingService;
import de.hpi3d.gamepgrog.trap.api.ApiManager;
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

    /**
     * Resets all stored data, and user on server.
     * After that init will be called
     */
    public void reset() {
        ApiManager.api(this).reset(getUserId()).call(() -> {
            StorageManager.reset(getApplication());
            init();
        });
    }

    private void init() {
        OurFirebaseMessagingService.init(getApplication());

        if (!StorageManager.with(this).userid.exists()) {
            registerUserAndSendFBToken();
        }
    }

    private void registerUserAndSendFBToken() {
        ApiManager.api(this).register().call((user, code) -> {
            StorageManager.with(this).userid.set(user.getUserId());
            StorageManager.with(this).botUrl.set(user.getRegisterURL());

            // Get fb token
            String token = StorageManager.with(this).fbtoken.getOrDefault(null);

            // If null, do nothing, it will getOrDefault send when it is updated
            if (token != null) {
                // Send gb token
                OurFirebaseMessagingService.sendNewToken(getApplication(), user.getUserId(), token);
            }
        });
    }


    private int getUserId() {
        return StorageManager.with(this).userid.get();
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
                ((SettingsActivity) getActivity()).reset();
                return true;
            });


            Preference debugButton = findPreference(getString(R.string.key_settings_steal));
//            debugButton.setChecked(StorageManager.with(getActivity()).safetyMode.get());
            debugButton.setOnPreferenceClickListener((preference) -> {
                String currentSafety = String.valueOf(StorageManager.with(getActivity()).safetyMode.get());
                Toast.makeText(getContext(), currentSafety, Toast.LENGTH_SHORT).show();
                return true;
            });

            Preference numberPreference = findPreference(getString(R.string.key_change_number));
            numberPreference.setTitle(StorageManager.with(getActivity()).phoneNumber.getOrDefault("No Number"));
            numberPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                StorageManager.with(getActivity()).phoneNumber.set((String) newValue);
                numberPreference.setTitle((String) newValue);
                ApiManager.api(getActivity()).sendPhoneNumber(
                        StorageManager.with(getActivity()).userid.get(),
                        (String) newValue
                ).call();
                return true;
            });


            SwitchPreferenceCompat safetyPreference = findPreference(getString(R.string.key_settings_safety_mode));
            safetyPreference.setChecked(StorageManager.with(getActivity()).safetyMode.get());
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