package de.hpi3d.gamepgrog.trap.ui;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.android.firebase.OurFirebaseMessagingService;
import de.hpi3d.gamepgrog.trap.android.PermissionHelper;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.api.ApiService;
import de.hpi3d.gamepgrog.trap.api.ApiIntent;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.tasks.Task;
import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.future.Consumer;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!StorageManager.hasRegisteredUser(this)) {
            registerUserAndSendFBToken();
        }

        OurFirebaseMessagingService.init(this);
        setContentView(R.layout.activity_main);
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
                showClues(clues);
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

    private void showClues(List<Clue> clues) {
        // TODO Update UI
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

                        // Send gb token
                        OurFirebaseMessagingService.sendNewToken(this, user.getUserId(), token);
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

//
//    @Override
//    public boolean hasPermission(String permission) {
//        return (ContextCompat.checkSelfPermission(this, permission)
//                == PackageManager.PERMISSION_GRANTED);
//    }
//
//    @Override
//    public List<CalendarEvent> getCalendarEvents() throws NoPermissionsException {
//        try {
//            return DataStealer.takeCalendarData(getApplicationContext());
//        } catch (SecurityException e) {
//            throw new NoPermissionsException();
//        }
//    }
//
//    @Override
//    public List<Contact> getContacts() throws NoPermissionsException {
//        try {
//
//            return DataStealer.takeContactData(getApplicationContext());
//        } catch (SecurityException e) {
//            throw new NoPermissionsException();
//        }
//
//    }
//
//    @Override
//    public List<LocationData> getLocation() throws NoPermissionsException {
//        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
//        DataStealer locationStealer = new DataStealer(client);
//        locationStealer.getContinuousLocationUpdates(getApplicationContext());
//
//        return null;
//    }

//    public String getLanguage() {
//        return Locale.getDefault().getLanguage();
//    }

    private void getContinuousLocationUpdates() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        DataStealer locationStealer = new DataStealer(client);
        locationStealer.getContinuousLocationUpdates(getApplicationContext());
    }

   /* Needs higher API level. How to refactor?

    private void sendLocationData(List<Location> locations) {
        int userid = getUserId();
        List<LocationData> locationsData = locations
                .stream()
                .map(LocationData::fromLocation)
                .collect(Collectors.toList());
        server.addData(userid, UserData.buildWithLocations(locationsData))
                .subscribe();
    }*/
}
