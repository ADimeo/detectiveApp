package de.hpi3d.gamepgrog.trap.ui;

import android.os.Bundle;
import android.util.Log;
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
import de.hpi3d.gamepgrog.trap.future.BiConsumer;
import de.hpi3d.gamepgrog.trap.future.Consumer;

/**
 * Activity for our Settings.
 */
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
     * Calls "reset" endpoint on server.
     * <p>
     * This endpoint resets the whole server back to
     * intitial state.
     * Calls init after reset
     */
    public void reset() {
        ApiManager.api(this).reset(getUserId()).call(() -> {
            StorageManager.reset(getApplication());
            init();
        });
    }

    /**
     * Wrapper around registerUserAndSendFBToken.
     * Makes sure that we don't call it while our
     * user isn't properly registered by our server.
     * <p>
     * Mirrors method in MainActivity, but rule of three.
     */
    private void init() {
        OurFirebaseMessagingService.init(getApplication());
        if (!StorageManager.with(this).userid.exists()) {
            registerUserAndSendFBToken();
        }
    }

    /**
     * Firebase initialisation: get and upload our token.
     * Mirrors method in MainActivity, but rule of three.
     */
    private void registerUserAndSendFBToken() {
        ApiManager.api(this).register().call((user, code) -> {
            if (null == StorageManager.with(this).userid) {
                Log.e("ERROR", "Tried to reset DB with no user ID set");
                return;
            }
            StorageManager.with(this).userid.set(user.getUserId());
            StorageManager.with(this).botUrl.set(user.getRegisterURL());
            Log.d("BOT_URL", user.getRegisterURL());

            // Get fb token
            String token = StorageManager.with(this).fbtoken.getOrDefault(null);

            // If null, do nothing, it will getOrDefault send when it is updated
            if (token != null) {
                // Send gb token
                OurFirebaseMessagingService.sendNewToken(getApplication(), user.getUserId(), token);
            }
        });
    }


    /**
     * Returns user ID from storage manager
     *
     * @return users ID
     */
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

            StorageManager storage = StorageManager.with(getActivity());

            Preference resetServerButton = findPreference(getString(R.string.key_settings_reset));
            resetServerButton.setOnPreferenceClickListener((preference) -> {
                String currentUrl = StorageManager.with(getActivity()).botUrl.get();
                Toast.makeText(getContext(), currentUrl, Toast.LENGTH_SHORT).show();
                ((SettingsActivity) getActivity()).reset();
                return true;
            });

            createPreference(
                    R.string.key_change_number,
                    EditTextPreference::setText,
                    storage.phoneNumber,
                    number ->
                            ApiManager.api(getActivity()).sendPhoneNumber(storage.userid.get(), number));

            createPreference(
                    R.string.key_settings_safety_mode,
                    SwitchPreferenceCompat::setChecked,
                    storage.safetyMode);

            createPreference(
                    R.string.key_settings_url,
                    EditTextPreference::setText,
                    storage.serverUrl);
        }

        private <T extends Preference, K> T createPreference(
                int id,
                BiConsumer<T, K> setter,
                StorageManager.Preference<K> storage) {
            return createPreference(id, setter, storage, d -> {
            });
        }

        private <T extends Preference, K> T createPreference(
                int id,
                BiConsumer<T, K> setter,
                StorageManager.Preference<K> storage,
                Consumer<K> changed) {
            T preference = findPreference(getString(id));
            if (preference != null) {
                setter.accept(preference, storage.get());
                preference.setOnPreferenceChangeListener((p, o) -> {
                    K value = (K) o;
                    storage.set(value);
                    changed.accept(value);
                    return true;
                });
            }
            return preference;
        }
    }

}