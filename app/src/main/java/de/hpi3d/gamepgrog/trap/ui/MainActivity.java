package de.hpi3d.gamepgrog.trap.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.CameraStealer;
import de.hpi3d.gamepgrog.trap.android.PermissionHelper;
import de.hpi3d.gamepgrog.trap.android.firebase.OurFirebaseMessagingService;
import de.hpi3d.gamepgrog.trap.api.ApiIntent;
import de.hpi3d.gamepgrog.trap.api.ApiService;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.User;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Call the App with "--ez MOCKAPI true" to enable the MockApi
        StorageManager.with(this).useMockApi.set(getIntent().getBooleanExtra("MOCKAPI", false));

        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        init();
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
