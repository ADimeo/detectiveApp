package de.hpi3d.gamepgrog.trap;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Profile;
import android.provider.CalendarContract.Events;

import java.util.ArrayList;

import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;


public class DataStealer {

    /**
     * Reads out data from contacts on the device and returns them.
     * Currently only reads names of contacts. Can be expanded to read more data.
     * <p>
     * For additional info regarding the used APIBuilder see
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
            do {
                String contactName = cursor.getString(positionOfNameColumn);
                Contact contact = new Contact(contactName);
                extractedContacts.add(contact);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return extractedContacts;
    }

    public static ArrayList<CalendarEvent> takeCalendarData(Context context){

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

        ArrayList<CalendarEvent> cEvents = CalendarEvent.createFromCursor(cursor);




        return cEvents;

    }




}
