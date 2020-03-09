package de.hpi3d.gamepgrog.trap.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.firebase.OurFirebaseMessagingService;
import de.hpi3d.gamepgrog.trap.api.ApiIntent;
import de.hpi3d.gamepgrog.trap.api.ApiService;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.User;

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
        ApiIntent
                .build(this)
                .setCall(ApiService.CALL_RESET)
                .put(ApiService.KEY_USER_ID, getUserId())
                .putReceiver((code, bundle) -> {
                    StorageManager.reset(getApplication());
                    init();
                })
                .start();
    }

    private void init() {
        OurFirebaseMessagingService.init(getApplication());

        if (!StorageManager.with(this).userid.exists()) {
            registerUserAndSendFBToken();
        }
    }

    private void registerUserAndSendFBToken() {
        ApiIntent
                .build(this)
                .setCall(ApiService.CALL_REGISTER)
                .putReceiver((code, bundle) -> {
                    if (code == ApiService.SUCCESS) {
                        User user = ApiIntent.getResult(bundle);

                        // Save new user id in db
                        StorageManager.with(this).userid.set(user.getUserId());
                        StorageManager.with(this).botUrl.set(user.getRegisterURL());

                        // Get fb token
                        String token = StorageManager.with(this).fbtoken.getOrDefault(null);

                        // If null, do nothing, it will getOrDefault send when it is updated
                        if (token != null) {
                            // Send gb token
                            OurFirebaseMessagingService.sendNewToken(this, user.getUserId(), token);
                        }
                    }
                })
                .start();
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


            Preference debugStealButton = findPreference(getString(R.string.key_settings_steal));
            debugStealButton.setOnPreferenceClickListener((preference) -> {
                String currentSafety = String.valueOf(StorageManager.with(getActivity()).safetyMode.get());

                String result = StorageManager.with(getActivity()).serverUrl.getOrDefault("default");
               result = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("key_settings_phone_number", "default") ;

                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();

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