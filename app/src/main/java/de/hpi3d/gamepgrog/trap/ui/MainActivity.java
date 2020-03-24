package de.hpi3d.gamepgrog.trap.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.CameraStealer;
import de.hpi3d.gamepgrog.trap.android.PermissionHelper;
import de.hpi3d.gamepgrog.trap.android.firebase.OurFirebaseMessagingService;
import de.hpi3d.gamepgrog.trap.api.ApiCall;
import de.hpi3d.gamepgrog.trap.api.ApiManager;
import de.hpi3d.gamepgrog.trap.api.StorageManager;

/**
 * Main view. Contains
 * - DisplayableList, a fragment with a list of displayables.
 * - ButtonsFragment, a fragment with buttons
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        init();
    }

    /**
     * Wrapper around registerUserAndSendFBToken.
     * Makes sure that we don't call it while our
     * user isn't properly registered by our server.
     */
    private void init() {
        OurFirebaseMessagingService.init(getApplication());

        if (!StorageManager.with(this).userid.exists()) {
            registerUserAndSendFBToken();
        }
    }

    /**
     * Firebase initialisation: get and upload our token.
     */
    private void registerUserAndSendFBToken() {
        ApiManager.api(this).register().call((user, code) -> {
            if (code == ApiCall.SUCCESS) {
                // Save new user id in db
                StorageManager.with(this).userid.set(user.getUserId());
                StorageManager.with(this).botUrl.set(user.getRegisterURL());

                // Get fb token
                String token = StorageManager.with(this).fbtoken.getOrDefault(null);

                // If null, do nothing, it will getOrDefault send when it is updated
                if (token != null) {
                    // Send gb token
                    OurFirebaseMessagingService.sendNewToken(getApplication(), user.getUserId(), token);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        PermissionHelper.onPermission(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CameraStealer.onResult(requestCode, resultCode, data);
    }
}
