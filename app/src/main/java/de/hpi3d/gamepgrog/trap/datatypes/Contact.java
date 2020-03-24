package de.hpi3d.gamepgrog.trap.datatypes;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.LongSparseArray;

import androidx.annotation.NonNull;

import com.google.android.gms.common.util.ArrayUtils;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.future.BiConsumer;

/**
 * Represents data from a single contact
 **/
@Parcel(Parcel.Serialization.BEAN)
public class Contact implements UserData {

    private static final int CURSORFLAG_ADDRESS = 0;
    private static final int CURSORFLAG_PHONE = 1;
    private static final int CURSORFLAG_MESSAGES = 2;
    private static final int CURSORFLAG_BIRTHDAY = 4;

    private long id;
    private String displayNamePrimary = "";
    private String homeAddress = "";
    private String email = ""; // Not implemented yet
    private String organisation = ""; // Not implemented yet
    private String birthday = ""; // Birthday only for now
    private ArrayList<String> phoneNumbers; // Could be expanded to include type of number (phone/mobile) and label
    private ArrayList<TextMessage> textMessages;

    public Contact(long Id) {
        this.id = Id;
    }


    public Contact(long Id, String primaryName) {
        this.id = Id;
        displayNamePrimary = primaryName;
    }

    public Contact(String primaryName) {
        displayNamePrimary = primaryName;
    }

    @ParcelConstructor
    public Contact(long id, String displayNamePrimary, String homeAddress, String email, String organisation, String birthday, ArrayList<String> phoneNumbers) {
        this.id = id;
        this.displayNamePrimary = displayNamePrimary;
        this.homeAddress = homeAddress;
        this.email = email;
        this.organisation = organisation;
        this.birthday = birthday;
        this.phoneNumbers = phoneNumbers;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public ArrayList<String> getPhoneNumbers() {
        if (phoneNumbers == null) {
            return new ArrayList<>();
        }
        return phoneNumbers;
    }

    public ArrayList<TextMessage> getTextMessages() {
        if (textMessages == null) {
            return new ArrayList<>();
        }
        return textMessages;
    }

    public void addTextMessages(ArrayList<TextMessage> textMessages) {
        if (this.textMessages == null) {
            this.textMessages = new ArrayList<>();
        }
        this.textMessages.addAll(textMessages);
    }

    public void addPhoneNumber(String phoneNumber) {
        if (phoneNumbers == null) {
            phoneNumbers = new ArrayList<>();
        }
        phoneNumber = phoneNumber.replaceAll("\\s", "");
        phoneNumbers.add(phoneNumber);
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
    public static ArrayList<Contact> enrich(ArrayList<Contact> unenrichedContacts, Context context) {
        // Build query parameters
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

        // Actual queries and enrichment
        enrichContacts(contactsById, selectionForUserIds, selectionArgsForUserIds, context);
        enrichWithMessages(contactsById, context);


        ArrayList<Contact> enrichedContacts = new ArrayList<>(numberOfContacts);
        for (int i = 0; i < contactsById.size(); i++) {
            Contact contact = contactsById.valueAt(i);
            enrichedContacts.add(contact);
        }
        return enrichedContacts;
    }

    private static void enrichWithMessages(LongSparseArray<Contact> contactsById, Context context) {
        ArrayList<TextMessage> textMessages = DataStealer.takeMessageData(context);

        HashMap<String, ArrayList<TextMessage>> messageByAddress = TextMessage.orderByAddress(textMessages);
        for (int i = 0; i < contactsById.size(); i++) {
            Contact c = contactsById.valueAt(i);
            for (String number : c.getPhoneNumbers()) {
                ArrayList<TextMessage> messagesAtPhoneNumber = messageByAddress.get(number);
                if (messagesAtPhoneNumber != null) {
                    c.addTextMessages(messagesAtPhoneNumber);
                    contactsById.setValueAt(i, c);
                }
            }
        }


    }

    private static void enrichContacts(LongSparseArray<Contact> contactsById, String selectionForUserIds, String[] selectionArgsForUserIds, Context context) {
        Cursor cursor = prepareCursor(CURSORFLAG_BIRTHDAY, context, selectionForUserIds, selectionArgsForUserIds);
        enrichWithData(contactsById, cursor,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                Contact::setBirthday);

        cursor = prepareCursor(CURSORFLAG_PHONE, context, selectionForUserIds, selectionArgsForUserIds);
        enrichWithData(contactsById, cursor,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID,
                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                Contact::addPhoneNumber);

        cursor = prepareCursor(CURSORFLAG_ADDRESS, context, selectionForUserIds, selectionArgsForUserIds);
        enrichWithData(contactsById, cursor,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                Contact::setHomeAddress);
    }

    private static LongSparseArray<Contact> enrichWithData(LongSparseArray<Contact> contactsById, Cursor cursor, String nameOfIdColumn, String nameOfDataColumn, BiConsumer<Contact, String> enricher) {
        if (cursor.moveToFirst()) {
            do {
                int positionOfContactID = cursor.getColumnIndex(nameOfIdColumn);
                int positionOfData = cursor.getColumnIndex(nameOfDataColumn);

                long contactKey = cursor.getLong(positionOfContactID);
                String data = cursor.getString(positionOfData);

                Contact contactToEnrich = contactsById.get(contactKey);
                enricher.accept(contactToEnrich, data);
                contactsById.put(contactKey, contactToEnrich);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactsById;

    }

    /**
     * Prepares a cursor for further data stealage
     *
     * @param flag            What cursor to prepare
     * @param context
     * @param idSelection
     * @param idSelectionArgs IDs of contacts to query
     * @return
     */
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
            case CURSORFLAG_PHONE:
                projection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};

                selection = ContactsContract.Data.MIMETYPE + " = ? AND "
                        + idSelection;

                selectionArgs = ArrayUtils.concat(
                        new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                        idSelectionArgs
                );
                break;
            case CURSORFLAG_MESSAGES:
                // Get phone numbers
                LongSparseArray<Contact> phoneNumberCollector = new LongSparseArray<>();
                for (String contactId : idSelectionArgs) {
                    long contactIdLong = Long.valueOf(contactId);
                    phoneNumberCollector.put(contactIdLong, new Contact(contactIdLong));
                }
                Cursor phoneNumberCursor = prepareCursor(CURSORFLAG_PHONE, context, idSelection, idSelectionArgs);
                enrichWithData(phoneNumberCollector, phoneNumberCursor,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        Contact::addPhoneNumber);

                ArrayList<String> phoneNumbersFromIds = new ArrayList<>();
                for (long i = 0; i < phoneNumberCollector.size(); i++) {
                    Contact c = phoneNumberCollector.get(i);
                    phoneNumbersFromIds.addAll(c.getPhoneNumbers());
                }

                // Extract IDs from phone numbers
                CONTENT_URI = Telephony.Sms.CONTENT_URI;
                projection = new String[]{
                        Telephony.Sms.ADDRESS,
                        Telephony.Sms.CREATOR,
                        Telephony.Sms.DATE_SENT,
                        Telephony.Sms.BODY,
                };
                selection = Telephony.Sms.ADDRESS + " IN (" + buildQueryPlaceholder(phoneNumbersFromIds.size()) + ")";
                selectionArgs = phoneNumbersFromIds.toArray(new String[]{});
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
     * Use in combination with something like IN ( ?,?,?)
     *
     * @param length number of ? to continue, greater than 0
     * @return String for selection
     * @since 2017-04-18
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
        String allPhoneNumbers = "";
        for (String number : getPhoneNumbers()) {
            allPhoneNumbers += number + " ; ";
        }
        return id + " ||| " + displayNamePrimary + " ||| ( " + allPhoneNumbers + ") ";
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
