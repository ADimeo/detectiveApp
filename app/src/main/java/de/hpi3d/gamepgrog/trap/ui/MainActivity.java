package de.hpi3d.gamepgrog.trap.ui;

import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.android.PermissionHelper;
import de.hpi3d.gamepgrog.trap.android.firebase.OurFirebaseMessagingService;
import de.hpi3d.gamepgrog.trap.api.ApiIntent;
import de.hpi3d.gamepgrog.trap.api.ApiService;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.future.Consumer;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        OurFirebaseMessagingService.init(this);

        if (!StorageManager.hasRegisteredUser(this)) {
            registerUserAndSendFBToken();
        }
//        new Task(10, "Test", "Hello World", "contact").execute(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        fetchData();
    }

    private void fetchData() {
        if (StorageManager.hasRegisteredUser(this)) {
            fetchClues((clues -> {
                saveClues(clues);
            }));
        }
    }

    private void fetchClues(Consumer<List<Clue>> callback) {
        ApiIntent
                .build(this)
                .setCall(ApiService.CALL_GET_CLUES)
                .put(ApiService.KEY_USER_ID, getUserId())
                .putReceiver((code, bundle) -> {
                    if (code == ApiService.SUCCESS) {
                        List<Clue> clues = ApiIntent.getResult(bundle);
                        callback.accept(clues);
                    }
                    // TODO handle Error
                })
                .start();
    }


    private void saveClues(List<Clue> clues) {
        // TODO
    }

    private void registerUserAndSendFBToken() {
        ApiIntent
                .build(this)
                .setCall(ApiService.CALL_REGISTER)
                .putReceiver((code, bundle) -> {
                    if (code == ApiService.SUCCESS) {
                        User user = ApiIntent.getResult(bundle);

                        // Save new user id in db
                        StorageManager.setUserId(this, user.getUserId());
                        StorageManager.setBotUrl(this, user.getRegisterURL());

                        // Get fb token
                        String token = StorageManager.getPlayerFBToken(this);

                        // If null, do nothing, it will get send when it is updated
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

    private int getUserId() {
        return StorageManager.getUserId(this);
    }

    public String getLanguage() {
        // TODO Wrap in object
        return Locale.getDefault().getLanguage();
    }

    private void getContinuousLocationUpdates() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        DataStealer locationStealer = new DataStealer(client);
        locationStealer.getContinuousLocationUpdates(getApplicationContext());
    }

}
