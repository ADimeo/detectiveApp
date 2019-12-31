package de.hpi3d.gamepgrog.trap.datatypes;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.LongSparseArray;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Represents data from a single contact
 **/
@Parcel(Parcel.Serialization.BEAN)
public class Contact implements UserData {

    private long id;
    private String displayNamePrimary;
    private String homeAddress = "";
    private String email = ""; // Not implemented yet
    private String organisation = ""; // Not implemented yet
    private String birthday = ""; // Birthday only for now

    public Contact(long Id, String primaryName) {
        this.id = Id;
        displayNamePrimary = primaryName;
    }

    public Contact(String primaryName) {
        displayNamePrimary = primaryName;
    }

    @ParcelConstructor
    public Contact(long id, String displayNamePrimary, String homeAddress, String email, String organisation, String birthday) {
        this.id = id;
        this.displayNamePrimary = displayNamePrimary;
        this.homeAddress = homeAddress;
        this.email = email;
        this.organisation = organisation;
        this.birthday = birthday;
    }

    public long getId() {
        return id;
    }

    public String getDisplayNamePrimary() {
        return displayNamePrimary;
    }

    public void setDisplayNamePrimary(String displayNamePrimary) {
        this.displayNamePrimary = displayNamePrimary;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * Adds additional data to all contacts in a list of contacts.
     * Contacts need to have their id set. This method then queries the local ContentProviders
     * for additional data, and returns a list of the same contacts, but with additional fields set.
     *
     * @param unenrichedContacts ArrayList of contacts to enrich
     * @param context            used to query android system
     * @return contacts with additional data
     */
    public static ArrayList<Contact> enrichContacts(ArrayList<Contact> unenrichedContacts, Context context) {
        int numberOfContacts = unenrichedContacts.size();
        LongSparseArray<Contact> contactsById = new LongSparseArray<>();

        String selectionForUserIds = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " IN (" + buildQueryPlaceholder(numberOfContacts) + ")";
        String[] selectionArgsForUserIds = new String[numberOfContacts];

        for (int i = 0; i < numberOfContacts; i++) {

            Contact c = unenrichedContacts.get(i);
            long contactID = c.getId();

            contactsById.append(contactID, c);
            selectionArgsForUserIds[i] = Long.toString(contactID);
        }


        Cursor cursor = prepareCursor(CURSORFLAG_BIRTHDAY, context, selectionForUserIds, selectionArgsForUserIds);
        enrichBirthdays(contactsById, cursor);
        cursor.close();

        cursor = prepareCursor(CURSORFLAG_ADDRESS, context, selectionForUserIds, selectionArgsForUserIds);
        enrichAddresses(contactsById, cursor);


        ArrayList<Contact> enrichedContacts = new ArrayList<Contact>(numberOfContacts);
        for (int i = 0; i < contactsById.size(); i++) {
            Contact contact = contactsById.valueAt(i);
            enrichedContacts.add(contact);
        }
        return enrichedContacts;
    }

    private static LongSparseArray<Contact> enrichBirthdays(LongSparseArray<Contact> contactsById, Cursor cursor) {
        do {
            int positionOfContactID = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.CONTACT_ID);
            int positionOfDate = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);

            long contactKey = cursor.getLong(positionOfContactID);
            String birthday = cursor.getString(positionOfDate);

            Contact contactToEnrich = contactsById.get(contactKey);
            contactToEnrich.setBirthday(birthday);
            contactsById.put(contactKey, contactToEnrich);
        } while (cursor.moveToNext());

        return contactsById;
    }

    private static LongSparseArray<Contact> enrichAddresses(LongSparseArray<Contact> contactsById, Cursor cursor) {
        do {
            int positionOfContactID = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID);
            int positionOfAddress = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);

            long contactKey = cursor.getLong(positionOfContactID);
            String address = cursor.getString(positionOfAddress);

            Contact contactToEnrich = contactsById.get(contactKey);
            contactToEnrich.setHomeAddress(address);
            contactsById.put(contactKey, contactToEnrich);
        } while (cursor.moveToNext());

        return contactsById;
    }


    private static final int CURSORFLAG_ADDRESS = 0;
    private static final int CURSORFLAG_BIRTHDAY = 4;


    private static Cursor prepareCursor(int flag, Context context, String idSelection, String[] idSelectionArgs) {
        Uri CONTENT_URI = ContactsContract.Data.CONTENT_URI;
        String[] projection = null;
        String selection = null; // Modified for mimetype
        String[] selectionArgs = null; // modified

        switch (flag) {
            case CURSORFLAG_ADDRESS:
                projection = new String[]{
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID,
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE
                };

                selection = ContactsContract.CommonDataKinds.StructuredPostal.TYPE + " = ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? AND "
                        + idSelection;

                selectionArgs = ArrayUtils.concat(
                        new String[]{
                                String.valueOf(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME),
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE},
                        idSelectionArgs);
                break;
            case CURSORFLAG_BIRTHDAY:
                projection = new String[]{ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Event.START_DATE,
                        ContactsContract.CommonDataKinds.Event.TYPE};
                selection = ContactsContract.CommonDataKinds.Event.TYPE + " = ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? AND "
                        + idSelection;

                selectionArgs = ArrayUtils.concat(
                        new String[]{
                                String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY),
                                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE},
                        idSelectionArgs);
                break;
        }


        Cursor cursor = context.getContentResolver().query(
                CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );
        cursor.moveToFirst();
        return cursor;
    }


    /**
     * Creates a String of the form "?, ?, ..., ?", with the length defined by the number
     * of arguments.
     * <p>
     * Use in combination with IN ( ?,?)
     *
     * @param length number of ? to continue, greater than 0
     * @return String for selection
     */
    private static String buildQueryPlaceholder(int length) {
        String questionMark = "?, ";
        StringBuilder builder = new StringBuilder();

        int i = 1; // This is the final questionmark we already added
        while (i < length) {
            builder.append(questionMark);
            i++;
        }
        builder.append("?");
        return builder.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return id + " ||| " + displayNamePrimary + " ||| " + birthday + " ||| " + homeAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return id == contact.id &&
                Objects.equals(displayNamePrimary, contact.displayNamePrimary) &&
                Objects.equals(homeAddress, contact.homeAddress) &&
                Objects.equals(email, contact.email) &&
                Objects.equals(organisation, contact.organisation) &&
                Objects.equals(birthday, contact.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayNamePrimary, homeAddress, email, organisation, birthday);
    }
}
