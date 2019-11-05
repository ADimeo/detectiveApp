package de.hpi3d.gamepgrog.trap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;

public class MainActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_IDENTIFIER_READ_CONTACTS = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent registerPlayer = new Intent(this, BackendManagerIntentService.class);
        registerPlayer.putExtra(BackendManagerIntentService.KEY_MANAGE_TYPE, BackendManagerIntentService.MANAGE_PLAYER_REGISTRATION);
        startService(registerPlayer);

    }


    /**
     * Requests permission to read contacts. Unsure where to put this, feel free to move it
     * around once we know from where we read device contacts.
     *
     * Mostly taken from https://developer.android.com/training/permissions/requesting#java
     */
    private void requestContactsPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // TODO Show justification for requesting permission here
            } else {
                // No explanation needed; request the this
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSION_REQUEST_IDENTIFIER_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            ArrayList<Contact> contacts = DataStealer.takeContactData(getApplicationContext());

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
                    ArrayList<Contact> contacts = DataStealer.takeContactData(getApplicationContext());

                } else {
                    // permission denied

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }






}
