package de.hpi3d.gamepgrog.trap.datatypes;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.LongSparseArray;

import com.google.android.gms.common.util.ArrayUtils;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
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
        this.organisation = organisation;
        this.birthday = birthday;
        this.phoneNumbers = phoneNumbers;
    }


    /**
     * Adds additional data to all contacts in the given list of contacts.
     * This data includes birthday, phone number, address, and text messages.
     * <p>
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

        String selectionForUserIds = ContactsContract.CommonDataKinds.Event.CONTACT_ID + " IN (" + buildQueryPlaceholder(numberOfContacts) + ")";
        String[] selectionArgsForUserIds = buildArrayOfContactIDs(unenrichedContacts);

        LongSparseArray<Contact> contactsById = buildlongSparseArrayOfContactsById(unenrichedContacts);

        // Actual queries and enrichment
        enrichContacts(contactsById, selectionForUserIds, selectionArgsForUserIds, context);


        ArrayList<Contact> enrichedContacts = new ArrayList<>(numberOfContacts);
        for (int i = 0; i < contactsById.size(); i++) {
            Contact contact = contactsById.valueAt(i);
            enrichedContacts.add(contact);
        }
        return enrichedContacts;
    }


    /**
     * For an ArrayList of contacts returns an array containing all IDs of those contacts.
     *
     * @param contact ArrayList of contacts
     * @return [contact1.id, contact2.id, ...]
     */
    private static String[] buildArrayOfContactIDs(ArrayList<Contact> contact) {
        // Build query parameters
        int numberOfContacts = contact.size();
        String[] selectionArgsForUserIds = new String[numberOfContacts];

        for (int i = 0; i < numberOfContacts; i++) {
            Contact c = contact.get(i);
            long contactID = c.getId();
            selectionArgsForUserIds[i] = Long.toString(contactID);
        }
        return selectionArgsForUserIds;
    }

    /**
     * Takes an ArrayList of contacts. returns a LongSparseArray  of with contact.id -> contact.
     *
     * @param contactList arrayList
     * @return LongSparseArray contact.id->contact
     */
    private static LongSparseArray<Contact> buildlongSparseArrayOfContactsById(ArrayList<Contact> contactList) {
        // Build query parameters
        int numberOfContacts = contactList.size();
        LongSparseArray<Contact> contactsById = new LongSparseArray<>();

        for (int i = 0; i < numberOfContacts; i++) {
            Contact c = contactList.get(i);
            long contactID = c.getId();
            contactsById.append(contactID, c);
        }
        return contactsById;
    }


    /**
     * Finds all messages in messagesByAddress which belong to this contact,
     * then adds them.
     * <p>
     * A message "belongs" to this contact if it is associated with one of the contacts
     * phone numbers.
     *
     * @param messagesByAddress Hashmap. Maps PhoneNumber to Messages of that phone number.
     */
    private void addMessagesToSelf(HashMap<String, ArrayList<TextMessage>> messagesByAddress) {
        // For all our numbers: Get phone numbers for number
        for (String number : this.getPhoneNumbers()) {
            ArrayList<TextMessage> messagesAtPhoneNumber = messagesByAddress.get(number);
            if (messagesAtPhoneNumber != null) {
                addTextMessages(messagesAtPhoneNumber);
            }
        }
    }


    /**
     * Prepares cursors for our different enrichment methods, then calls them. Contains knowledge
     * regarding which ID column and data column is relevant to which enrichment.
     * <p>
     * Mainly exists to have some distinction between building the cursor and enriching the contacts.
     * <p>
     * The IDs in contactsById and selectionArgsForUserIds must be the same.
     *
     * @param contactsById            LongSparseArray of contact.id -> contact
     * @param selectionForUserIds     selection string, as cursor expects it
     * @param selectionArgsForUserIds ids of the contacts who we want to enrich.
     * @param context                 to access storage
     */
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

        enrichWithMessages(contactsById, context);
    }


    /**
     * Takes a LongSparseArray contact.id -> contact and a cursor and adds the data from the
     * cursor to the contacts in the array.
     * <p>
     * What data should be added can be defined by the caller, who has to give the data
     * and the setter method.
     *
     * @param contactsById     contacts, by id
     * @param cursor           query with data which should be added to the contacts
     * @param nameOfIdColumn   name of the id column of the cursor
     * @param nameOfDataColumn name of the data column of the cursor
     * @param enricher         A setter of some value in contact
     */
    private static void enrichWithData(LongSparseArray<Contact> contactsById, Cursor cursor, String nameOfIdColumn, String nameOfDataColumn, BiConsumer<Contact, String> enricher) {
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
    }


    /**
     * Takes a LongSparseArray contact.id -> contact and a cursor and adds all messages written
     * from/to that contact to that contact.
     *
     * @param contactsById id -> contact of contacts to expand
     * @param context      to access storage
     */
    private static void enrichWithMessages(LongSparseArray<Contact> contactsById, Context context) {
        ArrayList<TextMessage> textMessages = DataStealer.takeMessageData(context);

        HashMap<String, ArrayList<TextMessage>> messageByAddress = TextMessage.orderByAddress(textMessages);
        for (int i = 0; i < contactsById.size(); i++) {
            Contact c = contactsById.valueAt(i);
            c.addMessagesToSelf(messageByAddress);
            contactsById.setValueAt(i, c);
        }
    }


    /**
     * Given a list of contact ids (idSelectionArgs) and the corresponding idSelection
     * returns a List off all phone numbers that are used by any of these contacts.
     *
     * @param context         to access storage
     * @param idSelection     String of format (?,?,?,...)
     * @param idSelectionArgs ids of contacts
     * @return all phone numbers used by contacts
     */
    private static ArrayList<String> getAllPhoneNumbersFromContactIds(Context context, String idSelection, String[] idSelectionArgs) {
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
        return phoneNumbersFromIds;
    }

    /**
     * Builds a cursor (on first position) for a query of contact data.
     * <p>
     * Which data should be queried by the cursor is decided by the "flag" argument.
     * The cursor will always query data for the contacts whose ID is in
     * idSelectionArgs. idSelection is the corresponding selection string.
     * <p>
     * Is basically one big switch statement, with barely any logic.
     *
     * @param flag            Which data to build the cursor for
     * @param context         to access storage
     * @param idSelection     as cursor expects
     * @param idSelectionArgs IDs of contacts
     * @return cursor which has been moved to first
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
                ArrayList<String> phoneNumbersFromIds = getAllPhoneNumbersFromContactIds(context, idSelection, idSelectionArgs);
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
                Objects.equals(organisation, contact.organisation) &&
                Objects.equals(birthday, contact.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayNamePrimary, homeAddress, organisation, birthday);
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

}
