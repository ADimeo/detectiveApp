package de.hpi3d.gamepgrog.trap.android;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Looper;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Profile;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.Language;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.datatypes.TextMessage;
import de.hpi3d.gamepgrog.trap.future.Consumer;


public class DataStealer {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;


    public static ArrayList<TextMessage> takeMessageData(Context context) {
        String[] projection = new String[]{
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.DATE_SENT,
                Telephony.Sms.BODY,
                Telephony.Sms.TYPE,
        };

        Cursor cursor = context.getContentResolver().query(
                Telephony.Sms.CONTENT_URI,
                projection,
                null,
                null,
                null);

        ArrayList<TextMessage> textMessages = new ArrayList<>();

        if (null != cursor && cursor.moveToFirst()) {
            int positionOfIdColumn = cursor.getColumnIndex(Telephony.Sms._ID);
            int positionOfAddressColumn = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
            int positionOfDateSentColumn = cursor.getColumnIndex(Telephony.Sms.DATE_SENT);
            int positionOfBodyColumn = cursor.getColumnIndex(Telephony.Sms.BODY);
            int positionOfMessageTypeColumn = cursor.getColumnIndex(Telephony.Sms.TYPE);

            do {
                long id = cursor.getLong(positionOfIdColumn);
                String address = cursor.getString(positionOfAddressColumn);
                long dateSent = cursor.getLong(positionOfDateSentColumn);
                String body = cursor.getString(positionOfBodyColumn);
                int typeFlag = cursor.getInt(positionOfMessageTypeColumn);

                boolean inbound = typeFlag == Telephony.Sms.MESSAGE_TYPE_INBOX;

                TextMessage textMessage = new TextMessage(id, dateSent, body, address, inbound);
                textMessages.add(textMessage);

            } while (cursor.moveToNext());
            cursor.close();
        }

        return textMessages;
    }

    /**
     * Reads out data from contacts on the device and returns them.
     * Currently only reads names of contacts. Can be expanded to read more data.
     * <p>
     * For additional info regarding the used ApiBuilder see
     * https://developer.android.com/guide/topics/providers/contacts-provider.html
     *
     * @param context to access contentResolver
     * @return ArrayList of Contacts
     */
    public static ArrayList<Contact> takeContactData(Context context) {
        // Sets the columns to retrieve for the user profile
        String[] projection = new String[]{
                Profile._ID,
                Profile.DISPLAY_NAME_PRIMARY,
                Profile.LOOKUP_KEY,
                Profile.PHOTO_THUMBNAIL_URI
        };

        // Retrieves the profile from the Contacts Provider
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                null);

        ArrayList<Contact> extractedContacts = new ArrayList<>();
        if (null != cursor && cursor.moveToFirst()) {
            int positionOfNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int positionOfIdColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);

            do {
                String contactName = cursor.getString(positionOfNameColumn);
                long id = cursor.getLong(positionOfIdColumn);
                Contact contact = new Contact(id, contactName);
                Log.d("NEW CONTACT CREATED", contact.toString());
                extractedContacts.add(contact);
            } while (cursor.moveToNext());
            cursor.close();
        }

        extractedContacts = Contact.enrich(extractedContacts, context);

        return extractedContacts;
    }

    public static ArrayList<CalendarEvent> takeCalendarData(Context context) throws SecurityException {

        String[] projection = new String[]{
                Events._ID,
                Events.TITLE,
                Events.EVENT_LOCATION,
                Events.DTSTART,
                Events.DTEND
        };

        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                null,
                null,
                null);

        return CalendarEvent.createFromCursor(cursor);
    }

    public static ArrayList<Language> takeLanguage(Context c) { // Argument for lambdas
        ArrayList<Language> languageList = new ArrayList<>();
        languageList.add(Language.getCurrentLanguage());
        return languageList;

    }

    public DataStealer(FusedLocationProviderClient client) {
        fusedLocationClient = client;
    }


    /**
     * inserts a single LocationData into the given consumer
     *
     * @param context
     * @param consumer
     */
    public void takeLocationData(Context context, Consumer<LocationData[]> consumer) {
        FusedLocationProviderClient client = new FusedLocationProviderClient(context);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(50);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Give out data
                    consumer.accept(new LocationData[]{new LocationData(location)});
                    client.removeLocationUpdates(locationCallback);
                }
            }
        };


        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    /**
     * To getOrDefault continuous location updates while app is in the foreground.
     * Can be rewritten to getOrDefault location exactly once.
     * <p>
     * Android documentation heavily discourages trying to getOrDefault location data while in the background.
     */
    public void getContinuousLocationUpdates(Context context) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(50);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        getLastCoarseLocation(fusedLocationClient, context);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    buildLocationToast(location, context);
                }
            }
        };

        System.out.println("======Calling callback");


        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

        Thread stopLocationUpdates = new Thread() {

            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    // This is mostly debug, so don't care at all.
                } finally {
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                }

            }
        };
        stopLocationUpdates.start();

    }


    public static void getLastCoarseLocation(FusedLocationProviderClient fusedLocationClient, Context context) {
        // "Coarse" is roughly a cityblock.
        // Only works if location has been requested before by something else, which might not be
        // the case.

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        buildLocationToast(location, context);
                    }
                });

    }

    private static void buildLocationToast(Location location, Context context) {
        if (location != null) {

            // location.getSpeed();
            // location.getExtras(); // Number of satellites for the fix
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            long time = location.getTime(); // UTC stamp in milliseconds

            StringBuilder s = new StringBuilder();
            s.append("Position: ")
                    .append("long: ")
                    .append(lon)
                    .append(" | lat: ")
                    .append(lat)
                    .append(" | TIME: ")
                    .append(time);
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, s.toString(), duration);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, "Location disabled or not yet received", Toast.LENGTH_LONG);
            toast.show();
            // User has disabled "location" in device settings
        }
    }


}
